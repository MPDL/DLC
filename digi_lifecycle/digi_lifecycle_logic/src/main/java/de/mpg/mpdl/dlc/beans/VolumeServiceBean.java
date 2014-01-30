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
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;

import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import net.sf.saxon.xqj.SaxonXQDataSource;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.SearchHandlerClient;
import de.escidoc.core.client.StagingHandlerClient;
import de.escidoc.core.client.interfaces.StagingHandlerClientInterface;
import de.escidoc.core.resources.HttpInputStream;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.Result;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.common.reference.ItemRef;
import de.escidoc.core.resources.common.reference.Reference;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.StorageType;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.ComponentContent;
import de.escidoc.core.resources.om.item.component.ComponentProperties;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.images.ImageController;
import de.mpg.mpdl.dlc.images.ImageHelper;
import de.mpg.mpdl.dlc.images.ImageHelper.Type;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;
import de.mpg.mpdl.dlc.searchLogic.FilterBean;
import de.mpg.mpdl.dlc.searchLogic.SearchBean;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion;
import de.mpg.mpdl.dlc.searchLogic.SearchCriterion.SearchType;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.CombinedSortCriterion;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion.SortIndices;
import de.mpg.mpdl.dlc.searchLogic.SortCriterion;
import de.mpg.mpdl.dlc.searchLogic.Criterion.Operator;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.IngestImage;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.mets.Mets;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.mods.ModsLocationSEC;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.mods.ModsRelatedItem;
import de.mpg.mpdl.dlc.vo.mods.ModsURLSEC;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;



public class VolumeServiceBean {
	
	private static Logger logger = Logger.getLogger(VolumeServiceBean.class); 
	
	public static String monographContentModelId;
	public static String multivolumeContentModelId;
	public static String volumeContentModelId;
	
	//private static JAXBContext jaxbVolumeContext;
	private static JAXBContext jaxbTeiContext;
	public static JAXBContext jaxbModsContext;
	private static JAXBContext jaxbMetsContext;
	
	public static IBindingFactory bfactMets;
	public static IBindingFactory bfactTei;
	//private static IBindingFactory bfactMods;
	
	 public static String tmpDir;
	

	public static TransformerFactory transfFact;
	
	private static EntityManagerFactory emf;

	static
	{
		try {
			
			Map<String, String> persistenceProps = new HashMap<String, String>();
			persistenceProps.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
			persistenceProps.put("javax.persistence.jdbc.url", PropertyReader.getProperty("dlc.batch_ingest.database.connection.url"));
			persistenceProps.put("javax.persistence.jdbc.user", PropertyReader.getProperty("dlc.batch_ingest.database.admin_user.name"));
			persistenceProps.put("javax.persistence.jdbc.password", PropertyReader.getProperty("dlc.batch_ingest.database.admin_user.password"));
			
			emf = Persistence.createEntityManagerFactory("dlc", persistenceProps);
		} catch (Exception e) {
			logger.error("Could not create Entity Manager Factory",e);
		}
		
		try 
		{
			monographContentModelId = PropertyReader.getProperty("dlc.content-model.monograph.id");
			multivolumeContentModelId = PropertyReader.getProperty("dlc.content-model.multivolume.id");
			volumeContentModelId = PropertyReader.getProperty("dlc.content-model.volume.id");
		} catch (Exception e) 
		{
			logger.error("Error while initializing static properties for Search Bean", e);
		}
		
		try {
			//jaxbVolumeContext = JAXBContext.newInstance(Volume.class);
			jaxbTeiContext = JAXBContext.newInstance(TeiSd.class);
			jaxbModsContext = JAXBContext.newInstance(ModsMetadata.class);
			jaxbMetsContext = JAXBContext.newInstance(Mets.class);
			
		} catch (JAXBException e) {
			logger.error("Error while creating JAXB context", e);
		}
		
		
		try {
			bfactMets = BindingDirectory.getFactory(Mets.class);
			bfactTei = BindingDirectory.getFactory(TeiSd.class);
			//bfactMods = BindingDirectory.getFactory(ModsMetadata.class);
		} catch (JiBXException e) {
			logger.error("Error while creating JibX binding factory", e);
		}
		
		if(transfFact==null)
		{
			//System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
			transfFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
			//transfFact = TransformerFactory.newInstance();
					
		}
		
		try {
			tmpDir =  PropertyReader.getProperty("image-upload.tmpDir");
		} catch (Exception e) {
			logger.error("Cannot find tmpDir", e);
		}
	}
	
	public enum VolumeTypes{
		MONOGRAPH(monographContentModelId),
		MULTIVOLUME(multivolumeContentModelId),
		VOLUME(volumeContentModelId);
		
		private String contentModelId;
		
		private VolumeTypes(String contentModelId)
		{
			this.contentModelId=contentModelId;
		}

		public String getContentModelId() {
			return contentModelId;
		}

	}
	
	public enum VolumeStatus{
		pending("pending"),
		submitted("submitted"),
		released("released"),
		withdrawn("withdrawn");
		
		private String status;
		
		private VolumeStatus(String status)
		{
			this.status = status;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
		
		
		
	}
	
	

	
	
	
	
//	//delete relations of MultiVolume
//	public Volume updateMultiVolume(Item item, String userHandle) throws Exception
//	{  
//		logger.info("Trying to update Multivolume item" + item.getObjid());
//		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
//		client.setHandle(userHandle);
//		
//
//		TaskParam taskParam=new TaskParam(); 
//	    taskParam.setComment("Update Volume");
//		taskParam.setLastModificationDate(item.getLastModificationDate());
//	    
//		Result updateResult = client.removeContentRelations(item.getObjid(), "Update Volume");
//	
//		Relations relations = new Relations();
//		Reference ref = new ItemRef("/ir/item/"+"escidoc:4037","Item "+"escidoc:4037");
//		
//		Relation relation = new Relation(ref);
//		relation.setPredicate("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasPart");
//		relations.add(relation);
//		item.setRelations(relations);
//		taskParam=new TaskParam(); 
//	    taskParam.setComment("Update Volume");
//		client.update(item);
//
//		logger.info("Item updated: " + item.getObjid());
//		
//		Volume vol = retrieveVolume("escidoc:1007", userHandle);
//		vol = releaseVolume(vol, userHandle);
//		return vol;
//	}
	

	
	public Volume retrieveVolume(String id, String userHandle) throws Exception
	{
		System.out.println("retrieve " + id);
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		if(userHandle!=null)
		{
			client.setHandle(userHandle);
		}
		
		Item item = client.retrieve(id);
		return createVolumeFromItem(item, userHandle);
		
	}
	

	
	
	
	/*
	public Volume createNewVolume(String contentModel, String contextId, String multiVolumeId,String userHandle, ModsMetadata modsMetadata, List<DiskFileItem> images, FileItem teiFile) throws Exception
	{
		
		logger.info("Creating new volume/monograph");
		
		Item item = createNewEmptyItem(contentModel,contextId, userHandle, modsMetadata);
		Volume parent = null;
		
		if(multiVolumeId != null)
		{
			parent = retrieveVolume(multiVolumeId, userHandle);
			parent = updateMultiVolume(parent, item.getObjid(), userHandle);
			parent = releaseVolume(parent, userHandle);
			
			//Also add the md record of the multivolume to each volume for indexing etc.
			MetadataRecord mdRecMv = new MetadataRecord("multivolume");
			mdRecMv.setContent(parent.getItem().getMetadataRecords().get("escidoc").getContent());
			item.getMetadataRecords().add(mdRecMv);
			
			//Add the relation to the multivolume
			Relations relations = new Relations();
			Reference ref = new ItemRef("/ir/item/"+parent.getItem().getObjid(),"Item "+ parent.getItem().getObjid());
			
			Relation relation = new Relation(ref);
			relation.setPredicate("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isPartOf");
			relations.add(relation);
			item.setRelations(relations);
		}
		

		Volume vol = new Volume();
		File teiFileWithPbConvention = null;
		try 
		{
			Mets metsData = new Mets();
			
			File teiFileWithIds = null;

			List<String> pbIds = new ArrayList<String>();
			
			
			
			
			
			//Convert and upload images 
			long start = System.currentTimeMillis();			
			for(DiskFileItem imageItem : images)
			{
				String filename = getJPEGFilename(imageItem.getName());
				String itemIdWithoutColon = item.getObjid().replaceAll(":", "_");
				
				String thumbnailsDir =  ImageHelper.THUMBNAILS_DIR + itemIdWithoutColon;
				File thumbnailFile = ImageHelper.scaleImage(imageItem.getStoreLocation(), filename, Type.THUMBNAIL);
				String thumbnailsResultDir = ImageController.uploadFileToImageServer(thumbnailFile, thumbnailsDir, filename);
								
				String webDir = ImageHelper.WEB_DIR + itemIdWithoutColon;;
				File webFile = ImageHelper.scaleImage(imageItem.getStoreLocation(), filename, Type.WEB);
				String webResultDir = ImageController.uploadFileToImageServer(webFile, webDir, filename);
				
				String originalDir = ImageHelper.ORIGINAL_DIR + itemIdWithoutColon;
				File originalFile = ImageHelper.scaleImage(imageItem.getStoreLocation(), filename, Type.ORIGINAL);
				String originalResultDir = ImageController.uploadFileToImageServer(originalFile, originalDir, filename);
				
				
				int pos = images.indexOf(imageItem);
				Page p = new Page();
				
				if(teiFile!=null)
				{
					p.setId(pbIds.get(pos));
				}
				else
				{
					p.setId("page_" + pos);
				}

				p.setOrder(pos);
				p.setOrderLabel("");
				p.setType("page");
				p.setContentIds(itemIdWithoutColon + "/"+ filename);
				p.setLabel(imageItem.getName());
				metsData.getPages().add(p);
				
				
			}
			long time = System.currentTimeMillis()-start;
			logger.info("Time to upload images: " + time);


			
			StagingHandlerClientInterface sthc = new StagingHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			sthc.setHandle(userHandle);
			
			if(teiFile!=null)	
			{
				logger.info("TEI file found");
				teiFileWithPbConvention = applyPbConventionToTei(teiFile.getInputStream());
				teiFileWithIds = addIdsToTei(new FileInputStream(teiFileWithPbConvention));
				pbIds = getAllPbIds(new FileInputStream(teiFileWithIds));
				
				
				//Transform TEI to Tei-SD and add to volume
				String teiSdString = transformTeiToTeiSd(new FileInputStream(teiFileWithIds));
				IUnmarshallingContext unmCtx = bfactTei.createUnmarshallingContext();
				TeiSd teiSd = (TeiSd)unmCtx.unmarshalDocument(new StringReader(teiSdString));
				vol.setTeiSd(teiSd);
				

				//Add paged TEI as component
				String pagedTei = transformTeiToPagedTei(new FileInputStream(teiFileWithIds));
				URL uploadedPagedTei = sthc.upload(new ByteArrayInputStream(pagedTei.getBytes("UTF-8")));
				Component pagedTeiComponent = new Component();
				ComponentProperties pagedTeiCompProps = new ComponentProperties();
				pagedTeiComponent.setProperties(pagedTeiCompProps);
				
				pagedTeiComponent.getProperties().setMimeType("text/xml");
				pagedTeiComponent.getProperties().setContentCategory("tei-paged");
				pagedTeiComponent.getProperties().setVisibility("public");
				ComponentContent pagedTeiContent = new ComponentContent();
				pagedTeiComponent.setContent(pagedTeiContent);
				pagedTeiComponent.getContent().setStorage(StorageType.INTERNAL_MANAGED);
				pagedTeiComponent.getContent().setXLinkHref(uploadedPagedTei.toExternalForm());
				item.getComponents().add(pagedTeiComponent);
				
				
				//Add original TEI as component
				URL uploadedTei = sthc.upload(new FileInputStream(teiFileWithIds));
				Component teiComponent = new Component();
				ComponentProperties teiCompProps = new ComponentProperties();
				teiComponent.setProperties(teiCompProps);
				
				teiComponent.getProperties().setMimeType("text/xml");
				teiComponent.getProperties().setContentCategory("tei");
				teiComponent.getProperties().setVisibility("public");
				ComponentContent teiContent = new ComponentContent();
				teiComponent.setContent(teiContent);
				teiComponent.getContent().setStorage(StorageType.INTERNAL_MANAGED);
				teiComponent.getContent().setXLinkHref(uploadedTei.toExternalForm());
				item.getComponents().add(teiComponent);
				
			}
			
			
			
			//if vol has a tei Sd
			if(vol.getTeiSd()!=null)
			{
				IMarshallingContext marshContext = bfactTei.createMarshallingContext();
				StringWriter sw = new StringWriter();
				marshContext.marshalDocument(vol.getTeiSd(), "utf-8", null, sw);

				URL uploadedTeiSd = sthc.upload(new ByteArrayInputStream(sw.toString().getBytes("UTF-8")));
				Component teiSdComponent = new Component();
				ComponentProperties teiSdCompProps = new ComponentProperties();
				teiSdComponent.setProperties(teiSdCompProps);
				
				teiSdComponent.getProperties().setMimeType("text/xml");
				teiSdComponent.getProperties().setContentCategory("tei-sd");
				teiSdComponent.getProperties().setVisibility("public");
				ComponentContent teiSdContent = new ComponentContent();
				teiSdComponent.setContent(teiSdContent);
				teiSdComponent.getContent().setStorage(StorageType.INTERNAL_MANAGED);
				teiSdComponent.getContent().setXLinkHref(uploadedTeiSd.toExternalForm());
				item.getComponents().add(teiSdComponent);
			}

			vol.setMets(metsData);
			vol.setProperties(item.getProperties());
			vol.setModsMetadata(modsMetadata);
			vol.setItem(item);
			

			vol = updateVolume(vol, userHandle, true);
			vol = releaseVolume(vol, userHandle);
		} 
		catch (Exception e) 
		{
			logger.error("Error while creating Volume. Trying to rollback", e);
			rollbackCreation(vol, userHandle);
			throw new Exception(e);
			}
		return vol;
	}
	
	
	*/
	
	
/*
	
	public Volume createNewVolume(String operation, String contentModel, String contextId, String multiVolumeId,String userHandle, ModsMetadata modsMetadata, List<IngestImage> images, DiskFileItem footer, DiskFileItem teiFile, DiskFileItem codicologicalFile) throws Exception
	{
		
		logger.info("Creating new volume/monograph");
		
		Volume volume = new Volume();
		Item item = createNewEmptyItem(contentModel,contextId, userHandle, modsMetadata);
		
	
		
		MetadataRecords mdRecs = item.getMetadataRecords();
		
		if(mdRecs==null)
		{
			mdRecs = new MetadataRecords();
			item.setMetadataRecords(mdRecs);
		}
		
		volume.setItem(item);
		volume.setProperties(volume.getItem().getProperties());
		volume.setModsMetadata(modsMetadata);
		
		Mets metsData = new Mets();
		volume.setMets(metsData);
		
		try{

			Volume parent = null;

			if(multiVolumeId != null)
			{
				parent = retrieveVolume(multiVolumeId, userHandle);
				parent = updateMultiVolume(parent, item.getObjid(), userHandle);
				parent = releaseVolume(parent.getItem().getObjid(), userHandle);
				
				//Also add the md record of the multivolume to each volume for indexing etc.
				MetadataRecord mdRecMv = new MetadataRecord("multivolume");
				mdRecMv.setContent(parent.getItem().getMetadataRecords().get("escidoc").getContent());
				item.getMetadataRecords().add(mdRecMv);
				
				//Add the relation to the multivolume
				Relations relations = new Relations();
				Reference ref = new ItemRef("/ir/item/"+parent.getItem().getObjid(),"Item "+ parent.getItem().getObjid());
				
				Relation relation = new Relation(ref);
				relation.setPredicate("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isPartOf");
				relations.add(relation);
				item.setRelations(relations);
				
			}

				//Convert and upload images 
				long start = System.currentTimeMillis();		
				
				uploadImagesAndCreateMets(images, footer, item.getObjid(), volume);
				
				long time = System.currentTimeMillis()-start;
				logger.info("Time to upload images: " + time);
			
			
				
				volume = updateVolume(volume, userHandle, teiFile, codicologicalFile, true);
			
				if(operation.equalsIgnoreCase("release"))
					volume = releaseVolume(volume.getItem().getObjid(), userHandle);
			}
		
			catch (Exception e) 
			{
				logger.error("Error while creating Volume. Trying to rollback", e);
				rollbackCreation(volume, userHandle);
				throw new Exception(e);
			}
			
			return volume;
	}  
	
	*/
	
	
	/**
	 * Writes the tei-sd link to the mods relatedItem element and updates the item.
	 * @param vol
	 * @param userHandle
	 * @return
	 * @throws Exception
	 */
	/*
	public static Item updateMd (Volume vol, String userHandle) throws Exception
	{
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		// Add MODS element 'relatedItem'
		String teiSdUrl = "";
		for (int i = 0; i< vol.getItem().getComponents().size(); i++)
			{
				Component comp = vol.getItem().getComponents().get(i);
				if (comp.getProperties().getContentCategory().equals("tei-sd"))
				{
					teiSdUrl = comp.getXLinkHref();
				}
			}
		
		if (teiSdUrl != "")
		{
			//add component url of tei sd to mods md (related item)
			ModsRelatedItem mri = new ModsRelatedItem();
			List <ModsRelatedItem> relList = vol.getModsMetadata().getRelatedItems();
			
			for (int i =0; i< relList.size(); i++)
			{
				if (relList.get(i).getDisplayLabel().equalsIgnoreCase("tei-sd"))
				{
					relList.remove(i);
				}
			}
			
			mri.setDisplayLabel("tei-sd");
			ModsURLSEC url = new ModsURLSEC();
			url.setValue("http://dlc.mpdl.mpg.de:8080" + teiSdUrl + "/content");
			ModsLocationSEC loc = new ModsLocationSEC();
			loc.setSec_url(url);
			mri.setSec_location(loc);
			vol.getModsMetadata().getRelatedItems().add(mri);
		}

		MetadataRecord eSciDocMdRec =vol.getItem().getMetadataRecords().get("escidoc");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document modsDoc = builder.newDocument();
		Marshaller m = jaxbModsContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(vol.getModsMetadata(), modsDoc);
		eSciDocMdRec.setContent(modsDoc.getDocumentElement());
		
		Item updatedItem = client.update(vol.getItem());
		return updatedItem;
	}
	*/
	public static String getJPEGFilename(String filename)
	{
		
//		if(filename.matches("\\.(jpg|JPEG|jpeg|JPG|Jpeg)$"))
		// if(filename.matches(".+?(\\.jpg|\\.JPEG|\\.jpeg|\\.JPG|\\.Jpeg)"))
		if(filename.matches("^.*?(jpg|jpeg|JPG|JPEG)$"))
		{
			return filename;
		}
		else
		{
			return filename + ".jpg";
		}
	}


	


	
	
	/*
	public Volume addRelationToMultiVolume(Volume vol, String relationId, String userHandle) throws Exception
	{
		logger.info("Trying to update Multivolume item" + vol.getProperties().getVersion().getObjid());
		
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		
		Item item = vol.getItem();
		Relations relations = new Relations();
		if(item.getRelations()!=null)
			relations = item.getRelations();
		Reference ref = new ItemRef("/ir/item/"+relationId,"Item "+relationId);
		
		Relation relation = new Relation(ref);
		relation.setPredicate("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasPart");
		relations.add(relation);
		item.setRelations(relations);
		Item updatedItem = client.update(item);
		
		logger.info("Item updated: " + updatedItem.getObjid());
		String currentStatus = updatedItem.getProperties().getVersion().getStatus();
		
		if(currentStatus.equals("pending") || currentStatus.equals("in-revision"))
		{
			TaskParam taskParam=new TaskParam(); 
		    taskParam.setComment("Submit Volume");
			taskParam.setLastModificationDate(updatedItem.getLastModificationDate());
			Result res = client.submit(updatedItem.getObjid(), taskParam);
		}
		

		logger.info("Item updated and submitted: " + updatedItem.getObjid());
		
		return retrieveVolume(updatedItem.getObjid(), userHandle);
	}
	
	*/
	
	
	
	
	public String updateMultiVolumeFromId(String multiId, List<String> volIds, String userHandle) throws Exception
	{
		logger.info("Trying to update Multivolume item" + multiId);
		
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		Item item = client.retrieve(multiId);

		Relations relations = new Relations();
		for(String volId : volIds)
		{
			if(item.getRelations()!=null)
				relations = item.getRelations();
			Reference ref = new ItemRef("/ir/item/"+ volId,"Item " + volId);
			Relation relation = new Relation(ref);
			relation.setPredicate("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasPart");
			relations.add(relation);
		}

		item.setRelations(relations);
		
		Item updatedItem = client.update(item);
		logger.info("Item updated: " + updatedItem.getObjid());
		String currentStatus = updatedItem.getProperties().getVersion().getStatus();
		
		if(currentStatus.equals("pending") || currentStatus.equals("in-revision"))
		{
			TaskParam taskParam=new TaskParam(); 
		    taskParam.setComment("Submit Volume");
			taskParam.setLastModificationDate(updatedItem.getLastModificationDate());
			Result res = client.submit(updatedItem.getObjid(), taskParam);
			logger.info("Item submitted: " + updatedItem.getObjid());
		}

		
		
		return updatedItem.getObjid();
	}
	
	
	/**
	 * Adds a relation to the given item, means adding a volume to a multivolume
	 * @param vol
	 * @param relationId
	 * @param userHandle
	 * @return
	 * @throws Exception
	 */
	/*
	public Volume addRelationToMultivolume(Volume vol, String relationId, String userHandle) throws Exception
	{
		logger.info("Adding relation " + relationId + " to multivolume " + vol.getProperties().getVersion().getObjid());
		
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		Item item = vol.getItem();
		
		String filter = "<param last-modification-date=\"" + item.getLastModificationDate() +"\"><relation>" +
				  "<targetId>" + relationId + "</targetId>" +
				  "<predicate>http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasPart</predicate>" +
				  "</relation></param>";
		
		
		client.addContentRelations(item, filter);
		
		return retrieveVolume(item.getObjid(), userHandle);
	}
	*/
	
	public VolumeSearchResult filterSearch(String query, List<SortCriterion> sortList, int limit, int offset, String index, String userHandle) throws Exception
	{
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		SearchRetrieveRequestType request = new SearchRetrieveRequestType();
		request.setQuery(query);
		
		SearchRetrieveResponse response = null;
		response = client.retrieveItems(request);
		
		List<Volume> volumeResult = new ArrayList<Volume>();
		
		for(SearchResultRecord rec : response.getRecords())
		{
			Item item = (Item) rec.getRecordData().getContent();
			
			Volume vol = createVolumeFromItem(item, null);
			vol.setSearchResultHighlight(rec.getRecordData().getHighlight());
			volumeResult.add(vol);
		}
		return new VolumeSearchResult(volumeResult, response.getNumberOfRecords());
		
	}
	
	
	/*
	public Volume updateVolume(Volume vol, String userHandle, boolean initial) throws Exception
	{
		    
		logger.info("Trying to update volume " +vol.getProperties().getVersion().getObjid());
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		Item item = vol.getItem();
		
		MetadataRecords mdRecs = item.getMetadataRecords();
		if(mdRecs==null)
		{
			mdRecs = new MetadataRecords();
			item.setMetadataRecords(mdRecs);
		}
		
		MetadataRecord mdRec = mdRecs.get("escidoc");
		if(mdRec == null)
		{
			mdRec = new MetadataRecord("escidoc");
			mdRecs.add(mdRec);
		}
				

		MetadataRecord metsMdRec = mdRecs.get("mets"); 
		if(metsMdRec ==null && vol.getMets() != null)
		{
			metsMdRec = new MetadataRecord("mets");
			mdRecs.add(metsMdRec);
		}
		

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document modsDoc = builder.newDocument();
		Marshaller m = jaxbModsContext.createMarshaller();
		
		//Set MODS in md-record
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(vol.getModsMetadata(), modsDoc);
		mdRec.setContent(modsDoc.getDocumentElement());
		
		
		//Set METS in md-record
		if(vol.getMets() != null)
		{
			IMarshallingContext mCont = bfactMets.createMarshallingContext();
			StringWriter sw = new StringWriter();
			mCont.marshalDocument(vol.getMets(), "UTF-8", null, sw);
			sw.close();
			Document metsDoc = builder.parse(new InputSource(new StringReader(sw.toString())));
			metsMdRec.setContent(metsDoc.getDocumentElement());
		}
		
		
		if(vol.getMets() != null)
		{
			IMarshallingContext mCont = bfactMets.createMarshallingContext();
			StringWriter sw = new StringWriter();
			mCont.marshalDocument(vol.getMets(), "UTF-8", null, sw);
			sw.close();
			Document metsDoc = builder.parse(new InputSource(new StringReader(sw.toString())));
			metsMdRec.setContent(metsDoc.getDocumentElement());
		}

		
		
		
		URL uploadedMets = new URL(PropertyReader.getProperty("escidoc.common.framework.url"));
		
		if(vol.getMets()!=null)
		{

			IMarshallingContext mCont = bfactMets.createMarshallingContext();
			StringWriter sw = new StringWriter();
			mCont.marshalDocument(vol.getMets(), "UTF-8", null, sw);
			sw.close();
			uploadedMets = sthc.upload(new ByteArrayInputStream(sw.toString().getBytes("UTF-8")));
		}
		
		
		//Check if component already exists
		

		item = client.update(item);
		logger.info("Item updated: " + item.getObjid());
		return createVolumeFromItem(item, userHandle);
		
	}
	
	*/
	
	
	
	public static ModsMetadata mabXMLToMODSTest(File mabXML) throws Exception
	{
		MabXmlTransformation transform = new MabXmlTransformation();
		File modsFile = transform.mabToMods(null, mabXML);
		FileInputStream xml = new FileInputStream(modsFile);
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { ModsMetadata.class });
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		ModsMetadata md = (ModsMetadata)unmarshaller.unmarshal(xml);
//		for(int i= 0; i<md.getRelatedItems().size();i++)
//		{
//			System.err.println(i+" type = " + md.getRelatedItems().get(i).getType());
//			System.err.println(i+" label = " + md.getRelatedItems().get(i).getDisplayLabel());
//			System.err.println(i+" title = " + md.getRelatedItems().get(i).getTitle());
//			System.err.println(i+" parent = " + md.getRelatedItems().get(i).getParentId_010());
//			System.err.println("___________________");
//		}
//		System.out.println("title= " +md.getTitles().size());
//		for(int j=0; j<md.getParts().size(); j++)
//		{
//			System.out.println(j + " type = " + md.getParts().get(j).getType());
//			System.out.println(j + " order = " + md.getParts().get(j).getOrder());
//			System.out.println("value length = " + md.getParts().get(j).getValue().length());
//			System.out.println(j + " value= " + md.getParts().get(j).getValue());
//			System.out.println(j + " volumeDescriptive_089 = " + md.getParts().get(j).getVolumeDescriptive_089());
//			System.out.println(j + " subseries_361 = " + md.getParts().get(j).getSubseries_361());
//			System.err.println("___________________");
//		}
		return md;
	}
	
	
	
	/*
	 public static void newOU() throws Exception
	 {
		 
			String url = "http://dlc.mpdl.mpg.de:8080";
			Authentication auth = new Authentication(new URL(url), "sysadmin", "dlcadmin");
			OrganizationalUnitHandlerClient ouc = new OrganizationalUnitHandlerClient(auth.getServiceAddress());
			ouc.setHandle(auth.getHandle());
			
			OrganizationalUnit ou = new OrganizationalUnit();
			ou.getProperties().setName("Test Organization for DLC");
			ou.getProperties().setDescription("This is a test organizational unit");
			
			
			MetadataRecord mdRecord = new MetadataRecord("escidoc");
		
		    String str =
		        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		            + "<ou:organization-details "
		            + "xmlns:ou=\"http://escidoc.mpg.de/metadataprofile/schema/0.1/organization\">\n"
		            + "<dc:title xmlns:dc=\"http://purl.org/dc/elements/1.1/\">"
		            + "Test Organization for DLC</dc:title>\n"
		            + "</ou:organization-details>";
		    InputStream in = new ByteArrayInputStream(str.getBytes());
		
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document doc = builder.parse(in);
		    mdRecord.setContent(doc.getDocumentElement());
		
		
		    MetadataRecords mdRecords = new MetadataRecords();
		    mdRecords.add(mdRecord);
		
		    ou.setMetadataRecords(mdRecords);
			
			
			ou = ouc.create(ou);
			TaskParam taskParam = new TaskParam();
		    taskParam.setComment("Open OU");
		    taskParam.setLastModificationDate(ou.getLastModificationDate());
		    ouc.open(ou.getObjid(), taskParam);
		    System.out.println("OU: " + ou.getObjid());
			

    
    }
    
    */
	

	
	
	
	public static void main(String[] args) throws Exception
	{
		
		System.out.println("abs".substring(1,2));
		
		
		/*
		
		File tei = new File("C:/Users/haarlae1/Documents/Digi Lifecycle/Examples/E29_001_1914_konvertiert teisd.xml");
		IUnmarshallingContext icont = bfactTei.createUnmarshallingContext();
		TeiSd teiSd = (TeiSd) icont.unmarshalDocument(new FileInputStream(tei), null);
		*/
		
		/*	
		File tei = new File("R:/dlc Ingest Daten/test_Berlin/B836F1_1885/B836F1_1885.xml");
		
		SchemaFactory factory = SchemaFactory.newInstance("http://relaxng.org/ns/structure/1.0", "com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory", null);
	       
       
        
        Schema schema = factory.newSchema(new File("C:/Projects/digi_lifecycle/digi_lifecycle_presentation/src/main/resources/schemas/DLC-TEI.rng"));
    
      
        Validator validator = schema.newValidator();

       
        validator.validate(new StreamSource(tei));
		
		List<XdmNode> list = VolumeServiceBean.getAllPbs(new StreamSource(tei));
		System.out.println(list);
		*/
		
		
		/*
		
		File tei = new File("R:/dlc Ingest Daten/test_KHI/tei/L_1122_a-tei.xml");
		File teiFileWithPbConvention = applyPbConventionToTei(new StreamSource(tei));
		File teiFileWithIds = addIdsToTei(new StreamSource(teiFileWithPbConvention));

		//Transform TEI to Tei-SD and add to volume
		String teiSdString = transformTeiToTeiSd(new StreamSource(teiFileWithIds));
		System.out.println(teiSdString);
		IUnmarshallingContext unmCtx = bfactTei.createUnmarshallingContext();
		TeiSd teiSd = (TeiSd)unmCtx.unmarshalDocument(new StringReader(teiSdString));
		*/
		

        
        
		
	}
	
	
	public static void importVolumeIntoVolume(Volume volWithData, Volume volumeWithoutData, String userHandle)throws Exception
	{
		createVolumeFromItem(volWithData.getItem(), userHandle, volumeWithoutData);
	}
	
	public static Volume createVolumeFromItem(Item item, String userHandle) throws Exception
	{
		return createVolumeFromItem(item, userHandle, null);
	}
	
	public static Volume createVolumeFromItem(Item item, String userHandle, Volume oldVol ) throws Exception
	{
		//MetadataRecord mdRec = item.getMetadataRecords().get("escidoc");
//		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
//		client.setHandle(userHandle); 
		//System.err.println("item id = " + item.getObjid());
		//TeiSd teiSd = null;
		Document teiSdXml = null;
		Volume vol = null;
		String tei = null;
		String pagedTei = null;
		
		
		//Unmarshall mods from md-record and set in item
		long startMods = System.currentTimeMillis();
		Unmarshaller modsUnmarshaller = jaxbModsContext.createUnmarshaller();

		//Unmarshaller unmarshaller = JaxBWrapper.getInstance("", schemaLocation)
		ModsMetadata md = (ModsMetadata)modsUnmarshaller.unmarshal(item.getMetadataRecords().get("escidoc").getContent());
		
		//Clear md-record in order to keep size of Volume object (and therefore of the session) smaller
		item.getMetadataRecords().get("escidoc").setContent(null);
		
		if(oldVol==null){
			vol = new Volume();
		}
		else
		{
			vol = oldVol;
		}
		
		vol.setModsMetadata(md);
		
		long timeMods = System.currentTimeMillis()-startMods;
		//System.out.println("Time MODS: " + timeMods);
		
		if(item.getMetadataRecords().get("mets")!=null)
		{
			long start = System.currentTimeMillis();
			
			IUnmarshallingContext uctx = bfactMets.createUnmarshallingContext();
			  
		    DOMSource source = new DOMSource(item.getMetadataRecords().get("mets").getContent());  
		    StringWriter xmlAsWriter = new StringWriter();  
		    StreamResult result = new StreamResult(xmlAsWriter);  
		    TransformerFactory.newInstance().newTransformer().transform(source, result); 
		    //System.out.println(xmlAsWriter.toString());
		      
		    // write changes  
		    InputStream inputStream = new ByteArrayInputStream(xmlAsWriter.toString().getBytes("UTF-8"));  
			vol.setMets((Mets) uctx.unmarshalDocument(inputStream, null));
			
			/* 
			 Unmarshaller metsUnmarshaller = jaxbMetsContext.createUnmarshaller();
			vol.setMets((Mets)metsUnmarshaller.unmarshal(item.getMetadataRecords().get("mets").getContent()));
			*/
			
			long time = System.currentTimeMillis()-start;
			//System.out.println("Time METS: " + time);
			
			//Clear md-record in order to keep size of Volume object (and therefore of the session) smaller
			item.getMetadataRecords().get("mets").setContent(null);
		}
		
		
		
		for(Component c : item.getComponents())
		{
			/*
			if (c.getProperties().getContentCategory().equals("mets"))
			{
				
				long start = System.currentTimeMillis();
				vol.setMets((Mets)unmarshaller.unmarshal(item.getMetadataRecords().get("mets").getContent());
				long time = System.currentTimeMillis()-start;
				System.out.println("Time METS: " + time);
				

			}
			*/
			
			/*
			else if (c.getProperties().getContentCategory().equals("tei-sd"))
			{
				long start = System.currentTimeMillis();
				DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
				fac.setNamespaceAware(true);
				teiSdXml = fac.newDocumentBuilder().parse(client.retrieveContent(item.getObjid(), c.getObjid()));
				teiSd = (TeiSd)unmarshaller.unmarshal(teiSdXml);
				long time = System.currentTimeMillis()-start;
				System.out.println("Time TEI-SD: " + time);
			}
			*/
			
			/*
			else if (c.getProperties().getContentCategory().equals("tei"))
			{
				long start = System.currentTimeMillis();
				tei = convertStreamToString(client.retrieveContent(item.getObjid(), c.getObjid()));
				long time = System.currentTimeMillis()-start;
				System.out.println("Time TEI: " + time);
			}
			
			else if (c.getProperties().getContentCategory().equals("tei-paged"))
			{
				long start = System.currentTimeMillis();
				pagedTei = convertStreamToString(client.retrieveContent(item.getObjid(), c.getObjid()));
				long time = System.currentTimeMillis()-start;
				System.out.println("Time Paged: " + time);
			}
			*/
			
			
			
			
		}
		
		
		vol.setItem(item);
		vol.setProperties(item.getProperties());
		//vol.setTeiSd(teiSd);
		vol.setTeiSdXml(teiSdXml);
		vol.setTei(tei);
		vol.setPagedTei(pagedTei);
		
		if(item.getRelations().size() > 0)
		{
			List<String> relatedVols = new ArrayList<String>();
			for(Relation r : item.getRelations())
			{
				relatedVols.add(r.getObjid());
			}
			vol.setRelatedVolumes(relatedVols);
		}
		
		return vol;
	}
	
	
	public String loadTei(Volume vol, String userHandle) throws Exception
	{
		if(!vol.getItem().getProperties().getPublicStatus().equals(PublicStatus.WITHDRAWN))
		{
			ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
	
			String tei = null;
			for(Component c : vol.getItem().getComponents())
			{
				
				
				if (c.getProperties().getContentCategory().equals("tei"))
				{
	
					tei = convertStreamToString(client.retrieveContent(vol.getItem().getObjid(), c.getObjid()));
				}
			}
			vol.setTei(tei);
			return tei;
		}
		return null;
	}
	
	public Document loadTeiSd(Volume vol, String userHandle) throws Exception
	{
		if(!vol.getItem().getProperties().getPublicStatus().equals(PublicStatus.WITHDRAWN))
		{
			ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			Document teiSdXml = null;
			
			//TeiSd teiSd = null;
			for(Component c : vol.getItem().getComponents())
			{
				
				
				if (c.getProperties().getContentCategory().equals("tei-sd"))
				{
	
					long start = System.currentTimeMillis();
	
					/*
					HttpInputStream is = client.retrieveContent(vol.getItem().getObjid(), c.getObjid());
					try {
						IUnmarshallingContext uctx = bfactTei.createUnmarshallingContext();
						teiSd = (TeiSd) uctx.unmarshalDocument(is, null);
					} finally {
						is.close();
					}
					*/
						
					DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
					fac.setNamespaceAware(true);
					HttpInputStream is = client.retrieveContent(vol.getItem().getObjid(), c.getObjid());
					try {
						teiSdXml = fac.newDocumentBuilder().parse(is, null);
					} finally {
						is.close();
					}
			
					long time = System.currentTimeMillis() - start;
					System.out.println("TIME TEI: " + time);
	
					
					
				}
			}
			//vol.setTeiSd(teiSd);
			vol.setTeiSdXml(teiSdXml);
			return teiSdXml;
		}
		return null;
	}
	
	public String loadPagedTei(Volume vol, String userHandle) throws Exception
	{
		if(!vol.getItem().getProperties().getPublicStatus().equals(PublicStatus.WITHDRAWN))
		{
			
		
			ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
	
			String tei = null;
			for(Component c : vol.getItem().getComponents())
			{
				if (c.getProperties().getContentCategory().equals("tei-paged"))
				{
					tei = convertStreamToString(client.retrieveContent(vol.getItem().getObjid(), c.getObjid()));
				}
			}
			vol.setPagedTei(tei);
			return tei;
		}
		return null;
	}
	
	public String loadCodicologicalMd(Volume vol, String userHandle) throws Exception
	{
		if(!vol.getItem().getProperties().getPublicStatus().equals(PublicStatus.WITHDRAWN))
		{
			
			
			ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
	
			String cdc = null;
			for(Component c : vol.getItem().getComponents())
			{
				if (c.getProperties().getContentCategory().equals("codicological"))
				{
					cdc = convertStreamToString(client.retrieveContent(vol.getItem().getObjid(), c.getObjid()));
				}
			}
			vol.setCodicological(cdc);
			return cdc;
		}
		return null;
	}
	
	
	private static String convertStreamToString(InputStream is)
            throws IOException {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. We iterate until the
         * Reader return -1 which means there's no more data to
         * read. We use the StringWriter class to produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {        
            return "";
        }
    }
	 
	public static ModsMetadata createModsMetadataFromXml(InputStream xml) throws Exception
	{
		
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { ModsMetadata.class });
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		ModsMetadata md = (ModsMetadata)unmarshaller.unmarshal(xml);

		return md;
		
		
	}
	
	
	
	public String getTeiForPage(Page p, String pagedTei) throws Exception
	{
		URL url = MabXmlTransformation.class.getClassLoader().getResource("xslt/teiToPagedTei/pagedTeiToSinglePage.xsl");
		
		SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
		
		ByteArrayInputStream bis = new ByteArrayInputStream(pagedTei.getBytes("UTF-8"));
		Source teiXmlSource = new StreamSource(bis);
		
		StringWriter wr = new StringWriter();
		StreamResult res = new StreamResult(wr);
		Transformer transformer = transfFact.newTransformer(xsltSource);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setParameter("pageId", p.getId());
		transformer.transform(teiXmlSource, res);

		return wr.toString();
	}
	
	public String getXhtmlForPage(Page p, String pagedTei) throws Exception
	{
		logger.info("get Xhtml for " + p.getId());
		String pagedTeiResult = getTeiForPage(p, pagedTei);
		//logger.info(pagedTeiResult);
		
		//System.out.println(pagedTeiResult);
		
		URL url = MabXmlTransformation.class.getClassLoader().getResource("xslt/dlc-tei-xsl-6.26/xml/tei/profiles/dlc/html/to.xsl");
		//URL url = MabXmlTransformation.class.getClassLoader().getResource("xslt/teiToXhtml/tei2html.xsl");
		
		Source xsltSource = new StreamSource(url.toExternalForm());
		//logger.info("Using xslt: " + url.toExternalForm());
		
		//File f = new File("C:/Projects/digi_lifecycle/digi_lifecycle_logic/src/main/resources/xslt/officialTei2/xhtml2/tei.xsl");
		//Source xsltSource = new StreamSource(new FileInputStream(f), "file:/C:/Projects/digi_lifecycle/digi_lifecycle_logic/src/main/resources/xslt/officialTei2/xhtml2/tei.xsl");

		
		ByteArrayInputStream bis = new ByteArrayInputStream(pagedTeiResult.getBytes("UTF-8"));
		Source teiXmlSource = new StreamSource(bis);
		

		
		DOMResult res = new DOMResult();
		logger.debug("Using URI RResolver" + transfFact.getURIResolver());
	
		//transfFact.setURIResolver(new StandardURIResolver());
		
		Transformer transformer = transfFact.newTransformer(xsltSource);
		
	/*
		transformer.setParameter("numberHeadings", "false");
		transformer.setParameter("numberFigures", "false");
		transformer.setParameter("numberTables", "false");
		transformer.setParameter("numberParagraphs", "false");
		  
		transformer.setParameter("autoToc", "false");
		transformer.setParameter("autoHead", "false");
		transformer.setParameter("institution", "");
		transformer.setParameter("copyrightStatement", "");
		transformer.setParameter("topNavigationPanel", "false");
		transformer.setParameter("showFigures", "false");
		transformer.setParameter("linkPanel", "false");
		
		
		*/
		//transformer.setParameter("pbid", p.getId());
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(teiXmlSource, res);
		

		
		//Get only the body part of the xhtml
		Processor proc = new Processor(false);
        net.sf.saxon.s9api.DocumentBuilder db = proc.newDocumentBuilder();
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("html", "http://www.w3.org/1999/xhtml");

        XPathExecutable xx = xpath.compile("/html:html/html:body/*");
        XPathSelector selector = xx.load();
        XdmNode xdmDoc = db.wrap(res.getNode());
        selector.setContextItem(xdmDoc);
       
        StringBuilder sb = new StringBuilder();
       
    	for(XdmItem item : selector) {
    		sb.append(item.toString());
    	}
            
		return sb.toString();
	}
	
	
	
	public static String transformTeiToMets(Source teiXmlSource)throws Exception
	{
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToMets/tei_to_mets.xslt");
			System.setProperty("javax.xml.transform.TransformerFactory",
					"net.sf.saxon.TransformerFactoryImpl");
			SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
			
			
			//Source teiXmlSource = new StreamSource(teiXml);

			StringWriter wr = new StringWriter();
			javax.xml.transform.Result result = new StreamResult(wr);
			
			
			Transformer transformer = transfFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(teiXmlSource, result);
			
			return wr.toString();
		
	}
	
	
	
	
	
	
	/*
	public static int validateTei(InputStream teiXml) throws Exception
	{
		
		 XQDataSource ds = new SaxonXQDataSource();
         XQConnection conn = ds.getConnection();
         XQExpression exp = conn.createExpression();
         exp.bindDocument(XQConstants.CONTEXT_ITEM, teiXml, null, null);
         XQResultSequence res = exp.executeQuery("declare namespace tei='http://www.tei-c.org/ns/1.0'; count(//tei:pb)");
         res.next();
         int pbNumber = Integer.parseInt(res.getItemAsString(null));
         conn.commit();
         conn.close();
         System.out.println("Found " + pbNumber + " <pb> elements");
         return pbNumber;
		
		/*
		
		
		
		
		
		
		/*
		
		
		InputSource is = new InputSource(teiXml);
		SAXSource ss = new SAXSource(is);
		Processor proc = new Processor(false);
		XdmNode node = proc.newDocumentBuilder().build(ss);
		XPathCompiler comp = proc.newXPathCompiler();
		comp.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
		XPathExecutable xpe = comp.compile("//tei:pb");
		XPathSelector sel = xpe.load();
		sel.setContextItem(node);
		
		XdmValue val = sel.evaluate();
		
		for(XdmItem item : val)
		{
			System.out.println(item.toString());
		}
		*/
		
		
		//Following is specific to Saxon: should be in a properties file
        
		/*
		System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON,"net.sf.saxon.xpath.XPathFactoryImpl");

        XPathFactory xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
        XPath xpe = xpf.newXPath();
        System.err.println("Loaded XPath Provider " + xpe.getClass().getName());
     */

        // Build the source document. This is outside the scope of the XPath API, and
        // is therefore Saxon-specific.
       
        
        //NodeInfo doc = ((XPathEvaluator)xpe).setSource(ss);

        // Declare a variable resolver to return the value of variables used in XPath expressions
        //xpe.setXPathVariableResolver(this);

        // Compile the XPath expressions used by the application

        //xpe.setNamespaceContext(this);

/*
        
        String xpath =  "declare namespace tei='http://www.tei-c.org/ns/1.0'\n" +
        		"//tei:pb";
        
        XPathExpression findPb = xpe.compile(xpath);
        
        
        List res = (List)findPb.evaluate(is, XPathConstants.NODESET);
        
        System.out.println(res);
        */

		/*
	}
	
	*/
	
	
	public static  XdmNode getPb(Source tei, String id) throws Exception
	{

		
		List<XdmNode> pagebreakList = new ArrayList<XdmNode>();
		Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
        XPathExecutable xx = xpath.compile("//tei:pb[@xml:id='"+id+"']");
        // Run the XPath Expression
        XPathSelector selector = xx.load();
        
        net.sf.saxon.s9api.DocumentBuilder db = proc.newDocumentBuilder();
        XdmNode xdmDoc = db.build(tei);
        selector.setContextItem(xdmDoc);
        
        if(selector.iterator().hasNext())
        {
        	return (XdmNode) selector.iterator().next();
        }
        
		
        
		return null;
	}
	
	public static  XdmNode getTitlePagePb(Source tei) throws Exception
	{

		
		String pageId = "";
		Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
        XPathExecutable xx = xpath.compile("//tei:titlePage[1]/preceding::tei:pb[1]|//tei:div[@type='titlepage'][1]/preceding::tei:pb[1]");
        // Run the XPath Expression
        XPathSelector selector = xx.load();
        
        net.sf.saxon.s9api.DocumentBuilder db = proc.newDocumentBuilder();
        XdmNode xdmDoc = db.wrap(tei);
        selector.setContextItem(xdmDoc);
        
        
		if(selector.iterator().hasNext())
		{
			return (XdmNode) selector.iterator().next();
		}
		
		return null;
	}
	
	
	public static  List<XdmNode> getAllPbs(Source tei) throws Exception
	{

		
		List<XdmNode> pagebreakList = new ArrayList<XdmNode>();
		Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
        XPathExecutable xx = xpath.compile("//tei:pb");
        // Run the XPath Expression
        XPathSelector selector = xx.load();
        
        net.sf.saxon.s9api.DocumentBuilder db = proc.newDocumentBuilder();
        XdmNode xdmDoc = db.build(tei);
        selector.setContextItem(xdmDoc);
        
        for(XdmItem item : selector) {
            XdmNode node = (XdmNode)item;
            pagebreakList.add(node);
           /*
            Node attribute = (org.w3c.dom.Node)node.getExternalNode();
            pageId = attribute.getTextContent();
            */
        }
		
        //tei.close();
		return pagebreakList;
		
		
		/*
		List<String> pbIds = new ArrayList<String>();
		
		XQDataSource ds = new SaxonXQDataSource();
        XQConnection conn = ds.getConnection();
        XQExpression exp = conn.createExpression();
        exp.bindDocument(XQConstants.CONTEXT_ITEM, tei, null, null);
        XQResultSequence res = exp.executeQuery("declare namespace tei='http://www.tei-c.org/ns/1.0'; //tei:pb/@xml:id");
        while(res.next())
        {
        	for(res.getNode().getAttributes())
        	
        	pbIds.add(res.getNode().getTextContent());
        }
       
        
        conn.commit();
        conn.close();
        
        return pbIds;
		*/
		
		/*
		Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
        XPathExecutable xx = xpath.compile("//tei:pb/@xml:id");
        // Run the XPath Expression
        XPathSelector selector = xx.load();
        
        net.sf.saxon.s9api.DocumentBuilder db = proc.newDocumentBuilder();
        XdmNode xdmDoc = db.wrap(tei);
        selector.setContextItem(xdmDoc);
        
        for(XdmItem item : selector) {
            XdmNode node = (XdmNode)item;
            Node attribute = (org.w3c.dom.Node)node.getExternalNode();
            pbIds.add(attribute.getTextContent());
        }
		
        return pbIds;
		
	*/
	}
	
	
	
	
	
	
	public  Page getPageForDiv(Volume v, String  divId) throws Exception
	{

		String pageId = "";
		Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
        XPathExecutable xx = xpath.compile("//tei:*[@xml:id='"+ divId+ "']/preceding::tei:pb[1]/@xml:id");
        // Run the XPath Expression
        XPathSelector selector = xx.load();
        
        net.sf.saxon.s9api.DocumentBuilder db = proc.newDocumentBuilder();
        XdmNode xdmDoc = db.wrap(v.getTeiSdXml());
        selector.setContextItem(xdmDoc);
        
        for(XdmItem item : selector) {
            XdmNode node = (XdmNode)item;
            Node attribute = (org.w3c.dom.Node)node.getExternalNode();
            pageId = attribute.getTextContent();
        }
		Page page = new Page();
		page.setId(pageId);
        int pageIndex = v.getMets().getPages().indexOf(page);
        return v.getMets().getPages().get(pageIndex);
		
	
	}
	
	
	public  Page getEndPageForDiv(Volume v, String  divId) throws Exception
	{

		String pageId = "";
		Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
        XPathExecutable xx = xpath.compile("//tei:*[@xml:id='"+ divId+ "']/descendant::tei:pb[last()]/@xml:id");
        // Run the XPath Expression
        XPathSelector selector = xx.load();
        
        net.sf.saxon.s9api.DocumentBuilder db = proc.newDocumentBuilder();
        XdmNode xdmDoc = db.wrap(v.getTeiSdXml());
        selector.setContextItem(xdmDoc);
        
        //if no pb is found as chil, use start pb
        if(!selector.iterator().hasNext())
        {
        	xx = xpath.compile("//tei:*[@xml:id='"+ divId+ "']/preceding::tei:pb[1]/@xml:id");
       	 	selector = xx.load();
            selector.setContextItem(xdmDoc);
        }
        

        for(XdmItem item : selector) {
            XdmNode node = (XdmNode)item;
            Node attribute = (org.w3c.dom.Node)node.getExternalNode();
            pageId = attribute.getTextContent();
        }
        
		Page page = new Page();
		page.setId(pageId);
        int pageIndex = v.getMets().getPages().indexOf(page);
        return v.getMets().getPages().get(pageIndex);
		
	
	}
	
	
	public String getDivForPage(Volume v, Page p) throws Exception
	{
		logger.debug("Finding structural element for pagebreak " + p.getId());
		long start = System.currentTimeMillis();
		String divId = "";
		Processor proc = new Processor(false);
        net.sf.saxon.s9api.DocumentBuilder db = proc.newDocumentBuilder();
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
        
        //Check if pagebreak is directly followed by an structural element like div, front, body...
        XPathExecutable xx = xpath.compile("//tei:pb[@xml:id='" + p.getId() + "']/(following::*|following::text()[normalize-space(.)!=''])[1]/(self::tei:front|self::tei:body|self::tei:back|self::tei:titlePage|self::tei:div|self::tei:figure)/@xml:id");
        XPathSelector selector = xx.load();
        XdmNode xdmDoc = db.wrap(v.getTeiSdXml());
        selector.setContextItem(xdmDoc);
        if(selector.iterator().hasNext())
        {
        	for(XdmItem item : selector) {
                XdmNode node = (XdmNode)item;
                Node attribute = (org.w3c.dom.Node)node.getExternalNode();
                divId = attribute.getTextContent();
            }
        }
        else
        {
        	//if not, take the first parent structural element of the pagebreak
        	xx = xpath.compile("//tei:pb[@xml:id='" + p.getId() + "']/(ancestor::tei:front|ancestor::tei:body|ancestor::tei:back|ancestor::tei:titlePage|ancestor::tei:div|ancestor::tei:figure)[last()]/@xml:id");
        	selector = xx.load();
            selector.setContextItem(xdmDoc);
            for(XdmItem item : selector) {
                XdmNode node = (XdmNode)item;
                Node attribute = (org.w3c.dom.Node)node.getExternalNode();
                divId = attribute.getTextContent();
            }
        }
        
        /*
        PbOrDiv div = (PbOrDiv)v.getTeiSd().getDivMap().get(divId);
        */
        long time = System.currentTimeMillis() - start;
       
        
        return divId;
	}  
	
	public Volume loadCompleteVolume(String volumeId, String userHandle) throws Exception
	{
		try { 

			
			if(volumeId!=null)
			{   
				logger.info("Load new book " + volumeId);
				Volume volume = retrieveVolume(volumeId, userHandle);
				if(volume.getItem().getProperties().getContentModel().getObjid().equals(monographContentModelId))
				{  
					loadTeiSd(volume, userHandle);
					loadTei(volume, userHandle);
					loadPagedTei(volume, userHandle);
					loadCodicologicalMd(volume, userHandle);
				}
				else if(volume.getItem().getProperties().getContentModel().getObjid().equals(multivolumeContentModelId))
				{
					List<Volume> volList = new ArrayList<Volume>();
					volList.add(volume);
					loadVolumesForMultivolume(volList, userHandle, true, null, null);
					
					/*
					volume.setRelatedChildVolumes(new ArrayList<Volume>());
					for(Relation rel : volume.getItem().getRelations())
					{
						Volume child = null;
						try {
							child= retrieveVolume(rel.getObjid(), userHandle);
						} catch (Exception e) {
							logger.error("cannot retrieve child Volume" + e.getMessage());
						}
						volume.getRelatedChildVolumes().add(child);
					}
					*/
				}
				else
				{
					try {
						Volume parent = null;
						Relation rel = volume.getItem().getRelations().get(0);
						parent = loadCompleteVolume(rel.getObjid(), userHandle);
						volume.setRelatedParentVolume(parent);

						
					} catch (Exception e) {
						logger.error("cannot retrieve parent Volume" + e.getMessage());
					}
					loadTeiSd(volume, userHandle);
					loadTei(volume, userHandle);
					loadPagedTei(volume, userHandle);
					loadCodicologicalMd(volume, userHandle);
				}
				return volume;
			}
			return null;
			
		} catch (Exception e) {
			logger.error("Problem while loading Volume", e);
			throw e;
		}
		
	}
	
	
	public void loadVolumesForMultivolume(List<Volume> volumeList, String userHandle, boolean filter, VolumeStatus[] versionStatus, VolumeStatus[] publicStatus) throws Exception
	{
		
		ItemHandlerClient ihc = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		if(userHandle!=null)
		{
			ihc.setHandle(userHandle);
		}
		  
		SearchHandlerClient shc = new SearchHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		if(userHandle!=null)
		{
			shc.setHandle(userHandle);
		}
		
		
		
//		List<String> volumeIds= new ArrayList<String>();
		Map<String, Volume> mvMap = new HashMap<String, Volume>();
		Map<String, Volume> vMap = new HashMap<String, Volume>();
//		StringBuffer volumeQuery = new StringBuffer();
		
		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
		
		for(Volume v : volumeList)
		{
		
			if(v.getRelatedVolumes() != null)
			{
				if(v.getItem().getProperties().getContentModel().getObjid().equals(VolumeServiceBean.multivolumeContentModelId))
				{
//					volumeIds.addAll(v.getRelatedVolumes());
					scList.add(new SearchCriterion(Operator.OR, SearchType.RELATION_ID, v.getItem().getOriginObjid()));
					mvMap.put(v.getItem().getOriginObjid(), v);				
				}
				else if(v.getItem().getProperties().getContentModel().getObjid().equals(VolumeServiceBean.volumeContentModelId))
				{
					scList.add(new SearchCriterion(Operator.OR, SearchType.OBJECT_ID, v.getRelatedVolumes().get(0)));
					vMap.put(v.getItem().getOriginObjid(), v);
				}
			}			
			
		}
		
		
//		for(int i=0; i<volumeIds.size(); i++)
//		{
//			
//			scList.add(new SearchCriterion(Operator.OR, SearchType.OBJECT_ID, volumeIds.get(i)));
//				/*
//				if(filter)
//				{
//					volumeQuery.append("\"/id\"=" + volumeIds.get(i));
//				}
//				else
//				{
//					volumeQuery.append("escidoc.objid=" + volumeIds.get(i));
//				}
//				
//				if(i<volumeIds.size() -1)
//				{
//					volumeQuery.append(" or ");
//				}
//				
//			*/
//			
//		}
		
		if(!scList.isEmpty())
		{
			SearchBean sb = new SearchBean();
			FilterBean fb = new FilterBean();
			
		
			
			
			/*
			SearchRetrieveRequestType sr= new SearchRetrieveRequestType();
			sr.setQuery(volumeQuery.toString());
			sr.setMaximumRecords(new NonNegativeInteger(String.valueOf(1000)));
			
			SearchRetrieveResponse result;
			*/
			VolumeSearchResult volumeResult;
			if(filter)
			{
				volumeResult = fb.itemFilter(new VolumeTypes[]{VolumeTypes.VOLUME, VolumeTypes.MULTIVOLUME}, versionStatus, publicStatus, scList, CombinedSortCriterion.VOLUME.getScList(), 5000, 0, userHandle);
			}
			else
			{
				volumeResult = fb.itemFilter(new VolumeTypes[]{VolumeTypes.VOLUME, VolumeTypes.MULTIVOLUME}, new VolumeStatus[]{VolumeStatus.released}, new VolumeStatus[]{VolumeStatus.released}, scList, CombinedSortCriterion.VOLUME.getScList(), 5000, 0, null);
				//volumeResult = sb.search(new VolumeTypes[]{VolumeTypes.VOLUME}, scList, CombinedSortCriterion.VOLUME.getScList(), 500, 0);
			}

			//VolumeSearchResult volumeResult = srwResponseToVolumeSearchResult(result);
			for(Volume v : volumeResult.getVolumes())
			{
				
				if(v.getRelatedVolumes()!=null && v.getRelatedVolumes().size()>0)
				{
					if(v.getItem().getProperties().getContentModel().getObjid().equals(VolumeServiceBean.volumeContentModelId))
					{
						String mvId = v.getRelatedVolumes().get(0);
						Volume mv = mvMap.get(mvId);

						if(mv.getRelatedChildVolumes()==null)
						{
							mv.setRelatedChildVolumes(new ArrayList<Volume>());
						}
						mv.getRelatedChildVolumes().add(v);
						v.setRelatedParentVolume(mv);

					}
					else if(v.getItem().getProperties().getContentModel().getObjid().equals(VolumeServiceBean.multivolumeContentModelId))
					{
						if (v.getRelatedChildVolumes() == null)
						{
							v.setRelatedChildVolumes(new ArrayList<Volume>());
						}
						for(String subVolId: v.getRelatedVolumes())
						{
							Volume subVol = vMap.get(subVolId);
							if(subVol !=null)
							{ 
								subVol.setRelatedParentVolume(v);
								v.getRelatedChildVolumes().add(subVol);
							}
							else
							{
								//Volume otherSubVol = retrieveVolume(subVolId, userHandle);
								//v.getRelatedChildVolumes().add(otherSubVol);
							}
							
							
						}

					}
						
					
				}
				
			}
		}
		
		
		
		
	}
	
	
	public static VolumeSearchResult srwResponseToVolumeSearchResult(SearchRetrieveResponse srwResp) throws Exception
	{
		List<Volume> volumeResult = new ArrayList<Volume>();		

		for(SearchResultRecord rec : srwResp.getRecords())
		{
			
			Item item = (Item)rec.getRecordData().getContent();

			Volume vol = VolumeServiceBean.createVolumeFromItem(item, null);
			vol.setSearchResultHighlight(rec.getRecordData().getHighlight());	
			volumeResult.add(vol);
		} 
	return new VolumeSearchResult(volumeResult, srwResp.getNumberOfRecords());
	}






	
	
	
	public static String transformCodicologicalToHtml(Source cdcXml, ExtensionFunction extFuncs)throws Exception
	{
		
		
		
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/codicologicalToHtml/codicologicalToHtml.xsl");
			System.setProperty("javax.xml.transform.TransformerFactory",
					"net.sf.saxon.TransformerFactoryImpl");
			
			Processor proc = new Processor(false);
			if(extFuncs!=null)
			{
				proc.registerExtensionFunction(extFuncs);
			}
			
			XsltCompiler xsltCompiler = proc.newXsltCompiler();
			XsltExecutable xsltExecutable = xsltCompiler.compile(new StreamSource(url.openStream()));
			
			XsltTransformer xsltTransformer = xsltExecutable.load();
			xsltTransformer.setSource(cdcXml);
			
			StringWriter sw = new StringWriter();
			xsltTransformer.setDestination(new Serializer(sw));
			
			xsltTransformer.transform();
			

			
			
			return sw.toString();
		
	}


	public static EntityManagerFactory getEmf() {
		return emf;
	}
	
	
	public String documentToString(Document node) throws Exception {
        
        Source source = new DOMSource(node);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(source, result);
        return stringWriter.getBuffer().toString();
 
}





	

}
