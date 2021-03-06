/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at LICENSE or https://escidoc.org/JSPWiki/en/CommonDevelopmentAndDistributionLicense. 
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
package de.mpg.mpdl.dlc.oai;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;

import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.component.Component;
import de.mpg.mpdl.dlc.util.PropertyReader;

public class DLCOAIUtils {
	
	private static Logger logger = Logger.getLogger(DLCOAIUtils.class);
	private static String ESCIDOC_URL = "", IMAGE_URL = "", VOLUME_CMODEL_ID = "";
	
	static {
		try {
			ESCIDOC_URL = PropertyReader.getProperty("escidoc.common.framework.url");
			IMAGE_URL = PropertyReader.getProperty("image-upload.url.download");
			VOLUME_CMODEL_ID = PropertyReader.getProperty("dlc.content-model.volume.id");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns the map of required parameters for transforming an escidoc item into mets xml.
	 * The required parameters are: itemId, teiUrl, metsUrl, imageUrl, parentUrl
	 * and the DLC internal shit: cModelId, cModelName, ctxId, ctxName, ouId, ouName
	 * @param item
	 * @return {@link HashMap}
	 */
	public HashMap<String, String> itemParameterMap(Item item) {
		HashMap<String, String> parameters = new HashMap<>();
		
		try {
		parameters.put("itemId", item.getObjid());
		
		if (item.getProperties().getPid() != null) {
			parameters.put("itemHdl", item.getProperties().getPid().replace("hdl:", "http://hdl.handle.net/"));
		}
		
		String teiUri = null;
		if (item.getComponents().size() > 0) {
			for (int i = 0; i< item.getComponents().size(); i++)
			{
				Component comp = item.getComponents().get(i);
				if (comp.getProperties().getContentCategory().equals("tei-sd"))
				{
					teiUri = comp.getXLinkHref().substring(1)+"/content";
				}
			}
		} else {
			teiUri = null;
		}
		parameters.put("teiUrl", ESCIDOC_URL + teiUri);
		
		String metsUri = null;
		if (!(item.getMetadataRecords().get("mets") == null)) {
			metsUri = item.getMetadataRecords().get("mets").getXLinkHref().substring(1);
		} else {
			metsUri = null;
		}
		parameters.put("metsUrl", ESCIDOC_URL + metsUri);
		
		parameters.put("imageUrl", IMAGE_URL);
		
		String cModelId = item.getProperties().getContentModel().getObjid();
		String parentModsUri = null;
		Item parent = null;
		
		if (cModelId.equalsIgnoreCase(VOLUME_CMODEL_ID)) {
			if (item.getRelations().size() > 0) {
				String parentId = item.getRelations().get(0).getObjid();
				parent = getItem(parentId);
				parentModsUri = parent.getMetadataRecords().get("escidoc").getXLinkHref().substring(1);
			}
		}
		parameters.put("parentUrl", ESCIDOC_URL + parentModsUri);
		
		parameters.put("cModelId", cModelId);
		
		parameters.put("cModelName", item.getProperties().getContentModel().getXLinkTitle());
		
		parameters.put("ctxId", item.getProperties().getContext().getObjid());

		parameters.put("ctxName", item.getProperties().getContext().getXLinkTitle());

		Context ctx = getContext(item.getProperties().getContext().getObjid());
		
		parameters.put("ouId", ctx.getProperties().getOrganizationalUnitRefs().get(0).getObjid());

		parameters.put("ouName", ctx.getProperties().getOrganizationalUnitRefs().get(0).getXLinkTitle());


		return parameters;
		} catch (NullPointerException npe) {
			logger.error(npe.getMessage(), npe);
		}
		return null;
		
	}
	
	/**
	 * Returns the map of required parameters for transforming the ListSet xml.
	 * The required parameters are: ctxId, ctxName
	 * @param item
	 * @return {@link HashMap}
	 */
	public HashMap<String, String> ctxParameterMap(Item item) {
		HashMap<String, String> parameters = new HashMap<>();
		try {
		parameters.put("ctxId", item.getProperties().getContext().getObjid());

		parameters.put("ctxName", item.getProperties().getContext().getXLinkTitle());

		return parameters;
		} catch (NullPointerException npe) {
			logger.error(npe.getMessage(), npe);
		}
		return null;
		
	}
	
	public Item getItem(String id) {
		ItemHandlerClient ihc;
		try {
			ihc = new ItemHandlerClient(new URL(ESCIDOC_URL));
			logger.info("trying to get " + id + " from " + ESCIDOC_URL);
			Item item = ihc.retrieve(id);
			return item;

		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (EscidocException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (InternalClientException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (TransportException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		
		return null;	
	}
	
	public Context getContext(String id) {
		try {
			ContextHandlerClient chc = new ContextHandlerClient(new URL(ESCIDOC_URL));
			Context ctx = chc.retrieve(id);
			return ctx;
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (EscidocException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (InternalClientException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (TransportException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return null;
	}
}
