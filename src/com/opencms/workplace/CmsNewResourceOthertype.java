/*
* File   : $Source: /alkacon/cvs/opencms/src/com/opencms/workplace/Attic/CmsNewResourceOthertype.java,v $
* Date   : $Date: 2004/06/04 10:48:52 $
* Version: $Revision: 1.43 $
*
* This library is part of OpenCms -
* the Open Source Content Mananagement System
*
* Copyright (C) 2001  The OpenCms Group
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* For further information about OpenCms, please see the
* OpenCms Website: http://www.opencms.org
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.opencms.workplace;

import org.opencms.file.CmsObject;
import org.opencms.i18n.CmsEncoder;
import org.opencms.main.CmsException;
import org.opencms.main.I_CmsConstants;
import org.opencms.workplace.CmsWorkplaceAction;

import com.opencms.core.I_CmsSession;
import com.opencms.legacy.CmsXmlTemplateLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * Template class for displaying the new resource screen for a new simple document
 * of the OpenCms workplace.<P>
 * Reads template files of the content type <code>CmsXmlWpTemplateFile</code>.
 *
 * @author Michael Emmerich
 * @version $Revision: 1.43 $ $Date: 2004/06/04 10:48:52 $
 */

public class CmsNewResourceOthertype extends CmsWorkplaceDefault {


    /**
     * Overwrites the getContent method of the CmsWorkplaceDefault.<br>
     * Gets the content of the new resource othertype template and processed the data input.
     * @param cms The CmsObject.
     * @param templateFile The lock template file
     * @param elementName not used
     * @param parameters Parameters of the request and the template.
     * @param templateSelector Selector of the template tag to be displayed.
     * @return Bytearry containing the processed data of the template.
     * @throws Throws CmsException if something goes wrong.
     */

    public byte[] getContent(CmsObject cms, String templateFile, String elementName,
            Hashtable parameters, String templateSelector) throws CmsException {

        // the template to be displayed
        String template = null;
        String filename = null;
        String title = null;
        String keywords = null;
        String description = null;
        String foldername = null;
        String type = null;
        I_CmsSession session = CmsXmlTemplateLoader.getSession(cms.getRequestContext(), true);

        // clear session values on first load
        String initial = (String)parameters.get(C_PARA_INITIAL);
        if(initial == null) {

            filename = CmsXmlTemplateLoader.getRequest(cms.getRequestContext()).getParameter(C_PARA_RESOURCE);
            title = CmsEncoder.redecodeUriComponent(CmsXmlTemplateLoader.getRequest(cms.getRequestContext()).getParameter(C_PARA_TITLE));
            keywords = CmsEncoder.redecodeUriComponent((String)parameters.get(C_PARA_KEYWORDS));
            description = CmsEncoder.redecodeUriComponent((String)parameters.get(C_PARA_DESCRIPTION));
            // foldername = (String)session.getValue(C_PARA_FILELIST);
            foldername = CmsWorkplaceAction.getCurrentFolder(CmsXmlTemplateLoader.getRequest(cms.getRequestContext()).getOriginalRequest());
            if(foldername == null) {
                foldername = cms.readAbsolutePath(cms.rootFolder());
            }
            type = (String)session.getValue("type");
            // create the new file
            List properties = null;
            if (title != null) {
                properties = new ArrayList();
                properties.add(new org.opencms.file.CmsProperty(I_CmsConstants.C_PROPERTY_TITLE, title, null));
            } else {
                properties = Collections.EMPTY_LIST;
            }            
            cms.createResource(foldername, filename, cms.getResourceTypeId(type), properties, new byte[0]);

            if( keywords != null && !keywords.equals("") ) {
                cms.writeProperty(foldername + filename, C_PROPERTY_KEYWORDS, keywords);
            }
            if( description != null && !description.equals("") ) {
                cms.writeProperty(foldername + filename, C_PROPERTY_DESCRIPTION, description);
            }

            // now return to filelist
            try {
                CmsXmlTemplateLoader.getResponse(cms.getRequestContext()).sendCmsRedirect(getConfigFile(cms).getWorkplaceActionPath() + CmsWorkplaceAction.getExplorerFileUri(CmsXmlTemplateLoader.getRequest(cms.getRequestContext()).getOriginalRequest()));
            }
            catch(Exception e) {
                throw new CmsException("Redirect fails :" + getConfigFile(cms).getWorkplaceActionPath() + CmsWorkplaceAction.getExplorerFileUri(CmsXmlTemplateLoader.getRequest(cms.getRequestContext()).getOriginalRequest()), CmsException.C_UNKNOWN_EXCEPTION, e);
            }
            return null;
        } else {
            session.putValue("type", CmsXmlTemplateLoader.getRequest(cms.getRequestContext()).getParameter("type"));

            // get the document to display
            CmsXmlWpTemplateFile xmlTemplateDocument = new CmsXmlWpTemplateFile(cms, templateFile);

            // process the selected template
            return startProcessing(cms, xmlTemplateDocument, "", parameters, template);
        }
    }
}
