/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
package de.mpg.mpdl.dlc.util;

import java.net.URI;

import javax.faces.context.FacesContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import de.mpg.mpdl.dlc.beans.ApplicationBean;

public class UrlHelper
{
    public static String getParameterValue(String parameterName)
    {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(parameterName);
    }

    public static boolean getParameterBoolean(String parameterName)
    {
        String str = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(parameterName);
        if ("1".equals(str))
        {
            return true;
        }
        return false;
    }

    public static boolean isValidURI(URI uri)
    {
        try
        {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(uri.toString());
            client.executeMethod(method);
            return true;
        }
        catch (Exception e)
        {
            MessageHelper.errorMessage("'" + uri + "'" + InternationalizationHelper.getMessage("error_invalidUri"));
        }
        return false;
    }
}
