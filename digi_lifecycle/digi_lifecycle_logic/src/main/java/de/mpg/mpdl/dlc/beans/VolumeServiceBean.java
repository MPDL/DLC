package de.mpg.mpdl.dlc.beans;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


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
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;

import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.om.NamespaceConstant;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.xqj.SaxonXQDataSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ContentModelHandlerClient;
import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.OrganizationalUnitHandlerClient;
import de.escidoc.core.client.SearchHandlerClient;
import de.escidoc.core.client.StagingHandlerClient;
import de.escidoc.core.client.interfaces.StagingHandlerClientInterface;
import de.escidoc.core.resources.HttpInputStream;
import de.escidoc.core.resources.cmm.ContentModel;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.Result;
import de.escidoc.core.resources.common.TaskParam;
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
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.images.ImageController;
import de.mpg.mpdl.dlc.images.ImageHelper;
import de.mpg.mpdl.dlc.images.ImageHelper.Type;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.mets.Mets;
import de.mpg.mpdl.dlc.vo.mets.MetsFile;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.teisd.Div;
import de.mpg.mpdl.dlc.vo.teisd.PbOrDiv;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;



public class VolumeServiceBean {
	
	private static Logger logger = Logger.getLogger(VolumeServiceBean.class); 
	
	public static String monographContentModelId;
	public static String multivolumeContentModelId;
	public static String volumeContentModelId;
	
	//private static JAXBContext jaxbVolumeContext;
	private static JAXBContext jaxbTeiContext;
	private static JAXBContext jaxbModsContext;
	private static JAXBContext jaxbMetsContext;
	
	private static IBindingFactory bfactMets;
	private static IBindingFactory bfactTei;
	//private static IBindingFactory bfactMods;
	

	private static TransformerFactory transfFact;

	static
	{
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
			// TODO Auto-generated catch block
			logger.error("Error while creating JibX binding factory", e);
		}
		
		if(transfFact==null)
		{
			//System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
			transfFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
			//transfFact = TransformerFactory.newInstance();
					
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
	
	

	public VolumeServiceBean()
	{
		
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
		
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		if(userHandle!=null)
		{
			client.setHandle(userHandle);
		}
		
		Item item = client.retrieve(id);
		return createVolumeFromItem(item, userHandle);
		
	}
	

	
	public Item createNewEmptyItem(String contentModel, String contextId,String userHandle, ModsMetadata modsMetadata) 
	{
		logger.info("Trying to create a new volume");
		Item item = new Item();
		ItemHandlerClient client;
		try {
			client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			//Create a dummy Item with dummy md record to get id of item
			item.getProperties().setContext(new ContextRef(contextId));
			item.getProperties().setContentModel(new ContentModelRef(contentModel));
		//	item.getProperties().setContentModel(new ContentModelRef(PropertyReader.getProperty("dlc.content-model.id")));
			MetadataRecords mdRecs = new MetadataRecords();
			MetadataRecord mdRec = new MetadataRecord("escidoc");
			mdRecs.add(mdRec);
			item.setMetadataRecords(mdRecs);
				
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			JAXBContext ctx = JAXBContext.newInstance(new Class[] { ModsMetadata.class });		
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.marshal(modsMetadata, d);
			mdRec.setContent(d.getDocumentElement());
			item = client.create(item);
			logger.info("Empty item created: " + item.getObjid());

		} catch (Exception e) {
			logger.error("Error while creating Item", e);

		} 

		return item;
	}  
	
	public Volume createNewMultiVolume(String contentModel, String contextId, String userHandle, ModsMetadata modsMetadata) throws Exception
	{  
		Item item = createNewEmptyItem(contentModel, contextId, userHandle, modsMetadata);
		Volume vol= new Volume();
		try
		{
		vol.setProperties(item.getProperties());
		vol.setModsMetadata(modsMetadata);
		vol.setItem(item);
		vol = updateVolume(vol, null,null,userHandle, false);
		vol = releaseVolume(vol, userHandle);
		}
		catch(Exception e)
		{
			logger.error("Error while creating Volume. Trying to rollback", e);
			rollbackCreation(vol, userHandle);
			throw new Exception(e);
		}
		return vol;
	}
	
	
	public Volume createNewVolume(String contentModel, String contextId, String multiVolumeId,String userHandle, ModsMetadata modsMetadata, List<DiskFileItem> images, FileItem teiFile) throws Exception
	{
		Item item = createNewEmptyItem(contentModel,contextId, userHandle, modsMetadata);
		Volume parent = null;
		
		if(multiVolumeId != null)
		{
			parent = retrieveVolume(multiVolumeId, userHandle);
			parent = updateMultiVolume(parent, item.getObjid(), userHandle);
			parent = releaseVolume(parent, userHandle);
		}
		
		Volume vol = new Volume();
		File teiFileWithPbConvention = null;
		try 
		{
			JAXBContext ctx = JAXBContext.newInstance(new Class[] { Mets.class });			
			Mets metsData = new Mets();
			
			File teiFileWithIds = null;
			
			
			

			
			List<String> pbIds = new ArrayList<String>();
			
			if(teiFile!=null)	
			{    
				logger.info("TEI file found");
				teiFileWithPbConvention = applyPbConventionToTei(teiFile.getInputStream());
				teiFileWithIds = addIdsToTei(new FileInputStream(teiFileWithPbConvention));
				pbIds = getAllPbIds(new FileInputStream(teiFileWithIds));
				
				/*
				String mets = transformTeiToMets(new FileInputStream(teiFileWithIds));
				Unmarshaller unmarshaller = ctx.createUnmarshaller();
				metsData = (Mets)unmarshaller.unmarshal(new ByteArrayInputStream(mets.getBytes("UTF-8")));
				
				
				for(FileItem fileItem : images)
				{
	
					int pos = images.indexOf(fileItem);
					metsData.getFiles().get(pos).setHref(dirs.get(pos));
					}
					*/
			}
			
			
			
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
			System.out.println("Time to Upload images: " + time);
			
			
			/*
			for(FileItem fileItem : images)
			{

				String thumbnailDir = "thumbnails/" + dirs.get(pos);
				MetsFile thumbFile = new MetsFile();
				thumbFile.setId("img_thumb_" + pos);
				thumbFile.setMimeType(fileItem.getContentType());
				thumbFile.setLocatorType("OTHER");
				thumbFile.setHref(thumbnailDir);
				metsData.getThumbnailResFiles().add(thumbFile);
				
				String defaultDir = "web/" + dirs.get(pos);
				MetsFile defaultFile = new MetsFile();
				defaultFile.setId("img_default_" + pos);
				defaultFile.setMimeType(fileItem.getContentType());
				defaultFile.setLocatorType("OTHER");
				defaultFile.setHref(defaultDir);
				metsData.getDefaultResFiles().add(defaultFile);
				
				String maxDir = "original/" + dirs.get(pos);
				MetsFile maxFile = new MetsFile();
				maxFile.setId("img_max_" + pos);
				maxFile.setMimeType(fileItem.getContentType());
				maxFile.setLocatorType("OTHER");
				maxFile.setHref(maxDir);
				metsData.getMaxResFiles().add(maxFile);
				
				String digilibDir = dirs.get(pos);
				MetsFile digilibFile = new MetsFile();
				digilibFile.setId("img_digilib_" + pos);
				digilibFile.setMimeType(fileItem.getContentType());
				digilibFile.setLocatorType("OTHER");
				digilibFile.setHref(digilibDir);
				metsData.getDigilibFiles().add(digilibFile);
	
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
				p.setThumbnailFile(thumbFile);
				p.setDefaultFile(defaultFile);
				p.setMaxFile(maxFile);
				p.setDigilibFile(digilibFile);
				
				
			}
			*/
			vol.setMets(metsData);
			vol.setProperties(item.getProperties());
			vol.setModsMetadata(modsMetadata);
			vol.setItem(item);

			vol = updateVolume(vol, parent, teiFileWithIds, userHandle, true);
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
	
	private static String getJPEGFilename(String filename)
	{
		
		if(filename.matches("\\.(jpg|JPEG|jpeg|JPG|Jpeg)$"))
		{
			return filename;
		}
		else
		{
		
			return filename + ".jpg";
		}
	}


	private void rollbackCreation(Volume vol, String userHandle) {
		logger.info("Trying to delete item" + vol.getItem().getObjid());
		try 
		{
			
			ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			client.delete(vol.getItem().getObjid());
			logger.info("Item successfully deleted");
		} 
		catch (Exception e) 
		{
			logger.error("Could not delete item" + vol.getItem().getObjid(), e);
			
		}
		
	}


	public Volume releaseVolume(Volume vol, String userHandle) throws Exception
	{
		String id = vol.getItem().getObjid();
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		logger.info("Releasing Volume " + id);
		TaskParam taskParam=new TaskParam(); 
	    taskParam.setComment("Submit Volume");
		taskParam.setLastModificationDate(vol.getItem().getLastModificationDate());
		
		Result res = client.submit(id, taskParam);
		
		taskParam=new TaskParam(); 
	    taskParam.setComment("Release Volume");
		taskParam.setLastModificationDate(res.getLastModificationDate());
		res = client.release(id, taskParam);
		
		
		return retrieveVolume(id, userHandle);
	}
	
	public Volume updateMultiVolume(Volume vol, String relationId, String userHandle) throws Exception
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
		TaskParam taskParam=new TaskParam(); 
	    taskParam.setComment("Update Volume");
		client.update(item);

		logger.info("Item updated: " + item.getObjid());
		
		return retrieveVolume(item.getObjid(), userHandle);
	}
	
	
	/**
	 * Adds a relation to the given item, means adding a volume to a multivolume
	 * @param vol
	 * @param relationId
	 * @param userHandle
	 * @return
	 * @throws Exception
	 */
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
	
	
	public Volume updateVolume(Volume vol, Volume parent, File teiFile, String userHandle, boolean initial) throws Exception
	{
		    
		logger.info("Trying to update item " +vol.getProperties().getVersion().getObjid());
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
		
		
		if(parent != null)
		{
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
		/*
		MetsDocument metsDoc = MetsDocument.Factory.newInstance();
		Mets mets = metsDoc.addNewMets();
		
		MdSecType dmdSec = mets.addNewDmdSec();
		dmdSec.setID("dmd_0");
		MdWrap mdWrap = dmdSec.addNewMdWrap();
		mdWrap.setMIMETYPE("text/xml");
		mdWrap.setMDTYPE(MDTYPE.MODS);
		XmlData xmlData = mdWrap.addNewXmlData();
		xmlData.set(vol.getModsMetadata());
		
		
		
		//Add METS xml to mdRecord of Item

		FileSec fileSec = mets.addNewFileSec();
		FileGrp imageFileGrp = fileSec.addNewFileGrp();
		imageFileGrp.setUSE("scans");
		
		StructMapType physicalStructMap = mets.addNewStructMap();
		physicalStructMap.setTYPE("physical");
		DivType physicalMainDiv = physicalStructMap.addNewDiv();
		physicalMainDiv.setTYPE("physical_structure");
		List<String> dmdIds = new ArrayList<String>();
		dmdIds.add("dmd_0");
		physicalMainDiv.setDMDID(dmdIds);
		
		
		int i = 0;
		
		for(Page page : vol.getPages())
		{
			
			
			FileType f = imageFileGrp.addNewFile();
			f.setMIMETYPE("image/jpg");
			String fileId = "img_" + i;
			f.setID(fileId);
			
			FLocat loc = f.addNewFLocat();
			loc.setLOCTYPE(FLocat.LOCTYPE.OTHER);
			loc.setHref(page.getPath());
			
			DivType pageDiv = physicalMainDiv.addNewDiv();
			pageDiv.setTYPE("page");
			pageDiv.setORDER(BigInteger.valueOf(i));
			pageDiv.setID("page_"+ i);
			if(page.getPagination()!=null)
			{
				pageDiv.setORDERLABEL(page.getPagination());
			}
			Fptr fileptr = pageDiv.addNewFptr();
			fileptr.setFILEID(fileId);
			i++;
		}
*/
		
		//Workaround for eSciDoc: Change prefix "xlin" to "xlink", otherwise bug with eSciDoc
		/*
		HashMap suggestedPrefixes = new HashMap();
		suggestedPrefixes.put("http://www.w3.org/1999/xlink", "xlink");
		XmlOptions opts = new XmlOptions();
		opts.setSaveSuggestedPrefixes(suggestedPrefixes);
		opts.setSavePrettyPrint();
		String xml = metsDoc.xmlText(opts);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document d = builder.parse( new InputSource(new StringReader(xml)) );
		*/
		//---
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document modsDoc = builder.newDocument();
        
        
		
        
		Marshaller m = jaxbModsContext.createMarshaller();
		
		//Set MODS in md-record
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(vol.getModsMetadata(), modsDoc);
		mdRec.setContent(modsDoc.getDocumentElement());
		
		
		if(vol.getMets() != null)
		{
			IMarshallingContext mCont = bfactMets.createMarshallingContext();
			StringWriter sw = new StringWriter();
			mCont.marshalDocument(vol.getMets(), "UTF-8", null, sw);
			sw.close();
			Document metsDoc = builder.parse(new InputSource(new StringReader(sw.toString())));
			metsMdRec.setContent(metsDoc.getDocumentElement());
		}

		
		
		StagingHandlerClientInterface sthc = new StagingHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		sthc.setHandle(userHandle);
		
		//upload METS
		
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
		if (initial)
		{
			if(vol.getMets()!=null)
			{
				Component metsComponent = new Component();
				ComponentProperties props = new ComponentProperties();
				metsComponent.setProperties(props);
				
				metsComponent.getProperties().setMimeType("text/xml");
				metsComponent.getProperties().setContentCategory("mets");
				metsComponent.getProperties().setVisibility("public");
				ComponentContent content = new ComponentContent();
				metsComponent.setContent(content);
				metsComponent.getContent().setStorage(StorageType.INTERNAL_MANAGED);
				metsComponent.getContent().setXLinkHref(uploadedMets.toExternalForm());
				item.getComponents().add(metsComponent);
				
			}
			
			
			if(teiFile!=null)
			{
				//Transform TEI to Tei-SD and add to component
				
				String teiSd = transformTeiToTeiSd(new FileInputStream(teiFile));
				URL uploadedTeiSd = sthc.upload(new ByteArrayInputStream(teiSd.getBytes("UTF-8")));
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
				
				
				//Add paged TEI as component
				String pagedTei = transformTeiToPagedTei(new FileInputStream(teiFile));
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
				URL uploadedTei = sthc.upload(new FileInputStream(teiFile));
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
			
			
			
			
		}
		
		else
		{
			for(Component comp : item.getComponents())
			{
				if(comp.getProperties().getContentCategory().equals("mets"))
				{
					comp.getContent().setXLinkHref(uploadedMets.toExternalForm());
					
				}
			}
		}
		
		
		
		
		
		

		item = client.update(item);
		logger.info("Item updated: " + item.getObjid());
		return createVolumeFromItem(item, userHandle);
		
	}
	
	
	
	public static void main(String[] args) throws Exception
	{
		
		
		String url = "http://dlc.mpdl.mpg.de:8080";
		Authentication auth = new Authentication(new URL(url), "sysadmin", "dlcadmin");
		
		ItemHandlerClient client = new ItemHandlerClient(auth.getServiceAddress());
		client.setHandle(auth.getHandle()); 
//		Item item = client.retrieve("escidoc:8034");
//		
//		
//		TaskParam taskParam=new TaskParam(); 
//	    taskParam.setComment("Submit Volume");
//		taskParam.setLastModificationDate(item.getLastModificationDate());
//		
//		Result res = client.submit(item.getObjid(), taskParam);
//		taskParam=new TaskParam(); 
//	    taskParam.setComment("Release Volume");
//		taskParam.setLastModificationDate(res.getLastModificationDate());
//		client.release("escidoc:8034", taskParam);

		/*
		
		
		ModsDocument modsDoc = ModsDocument.Factory.newInstance();
		modsDoc.addNewMods().addNewTitleInfo().addNewTitle().set("Test Title");
		
		HashMap suggestedPrefixes = new HashMap();
		suggestedPrefixes.put("http://www.w3.org/1999/xlink", "xlink");
		XmlOptions opts = new XmlOptions();
		opts.setSaveSuggestedPrefixes(suggestedPrefixes);
		opts.setSavePrettyPrint();
		MetsDocument doc = MetsDocument.Factory.newInstance(opts);
	
		
		
		ItemHandlerClient client = new ItemHandlerClient(new URL(url));
		client.setHandle(auth.getHandle());
		client.delete("escidoc:5030");
		*/
		
		/*
		 * 
		Item item = new Item();
		item.getProperties().setContext(new ContextRef("escidoc:5002"));
		item.getProperties().setContentModel(new ContentModelRef("escidoc:4001"));
		
		
		MetadataRecords mdRecs = new MetadataRecords();
		MetadataRecord mdRec = new MetadataRecord("escidoc");
		mdRecs.add(mdRec);
		item.setMetadataRecords(mdRecs);
		
		MetsDocument metsDoc = MetsDocument.Factory.newInstance();
		Mets mets = metsDoc.addNewMets();
		
		MdSecType dmdSec = mets.addNewDmdSec();
		dmdSec.setID("dmd_0");
		MdWrap mdWrap = dmdSec.addNewMdWrap();
		mdWrap.setMIMETYPE("text/xml");
		mdWrap.setMDTYPE(MDTYPE.MODS);
		XmlData xmlData = mdWrap.addNewXmlData();
		
		XmlCursor dataCursor = xmlData.newCursor();
		XmlCursor modsCursor = modsDoc.getMods().newCursor();
		modsCursor.copyXml(dataCursor);
		modsCursor.dispose();
		dataCursor.dispose();
		
		
		//Add METS xml to mdRecord of Item
		mdRec.setContent((Element)metsDoc.getMets().getDomNode());
		
		
		
		item = client.create(item);
		
		System.out.println(item.getObjid());
		
		
		*/
		
		
		
		ContentModelHandlerClient cmh = new ContentModelHandlerClient(new URL(url));
		cmh.setHandle(auth.getHandle());
		
		ContentModel cm = new ContentModel();
		cm.getProperties().setName("Content_Model_DLC_ITEM_NEW");
		cm.getProperties().setDescription("Content Model for Digitization Lifecycle Items");
		
		cm = cmh.create(cm);
		System.out.println(cm.getObjid());
		 
		
		
		/*
		OrganizationalUnitHandlerClient ouc = new OrganizationalUnitHandlerClient(new URL(url));
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
		
		
		
		
		ContextHandlerClient chc = new ContextHandlerClient(new URL(url));
		chc.setHandle(auth.getHandle());
		
		Context context = new Context();
		ContextProperties properties = new ContextProperties();

        properties.setName("DLC_Example_Context");


        properties.setDescription("Example context for Digitization Lifecycle Project");

        properties.setType("dlc");


        OrganizationalUnitRefs ous = new OrganizationalUnitRefs();

        ous.add(new OrganizationalUnitRef(ou.getObjid()));
        properties.setOrganizationalUnitRefs(ous);

        context.setProperties(properties);

        context = chc.create(context);
        System.out.println("Context: " + context.getObjid());
        taskParam = new TaskParam();
        taskParam.setComment("Open Context");
		taskParam.setLastModificationDate(context.getLastModificationDate());

		Result result = chc.open(context.getObjid(), taskParam);
		System.out.println("Context: " + result.toString());^
		
		
		
		UserAccountHandlerClient uac = new UserAccountHandlerClient(new URL(url));
			UserAccount ua = new UserAccount();

		
        UserAccountProperties properties = new UserAccountProperties();
        properties.setLoginName("dlc_user");
        properties.setName("DLC Test User");

        ua.setProperties(properties);

        ua = uac.create(ua);
        TaskParam taskParam=new TaskParam(); 
        taskParam.setComment("Activate User");
	    taskParam.setLastModificationDate(ua.getLastModificationDate());
        uac.activate(ua.getObjid(), taskParam);
      
        
		UserAccount ua = uac.retrieve("dlc_user");
		
        TaskParam taskParam=new TaskParam(); 
        taskParam.setPassword("dlc");
        taskParam.setLastModificationDate(ua.getLastModificationDate());
        uac.updatePassword(ua.getObjid(), taskParam);
        
        
        Grant grant = new Grant();
        GrantProperties grantProperties = new GrantProperties();
        grantProperties.setGrantRemark("new context grant");
        grantProperties.setAssignedOn(new ContextRef("escidoc:5002"));
        RoleRef roleRef = new RoleRef("escidoc:role-depositor");
        grantProperties.setRole(roleRef);
        grant.setGrantProperties(grantProperties);
        
        uac.createGrant(ua.getObjid(), grant);
        System.out.println("Granted Depositor");
        
        grant = new Grant();
       grantProperties = new GrantProperties();
        grantProperties.setGrantRemark("new context grant");
        grantProperties.setAssignedOn(new ContextRef("escidoc:5002"));
        roleRef = new RoleRef("escidoc:role-moderator");
        grantProperties.setRole(roleRef);
        grant.setGrantProperties(grantProperties);
        
        uac.createGrant(ua.getObjid(), grant);
        System.out.println("Granted Moderator");
        
        */
        
        
		
	}
	
	public static Volume createVolumeFromItem(Item item, String userHandle) throws Exception
	{
		//MetadataRecord mdRec = item.getMetadataRecords().get("escidoc");
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle); 
		System.err.println("item id = " + item.getObjid());
		TeiSd teiSd = null;
		Document teiSdXml = null;
		Volume vol = null;
		String tei = null;
		String pagedTei = null;
		
		
		//Unbmarshall mods from md-record and set in item
		long startMods = System.currentTimeMillis();
		Unmarshaller modsUnmarshaller = jaxbModsContext.createUnmarshaller();

		
		//Unmarshaller unmarshaller = JaxBWrapper.getInstance("", schemaLocation)
		ModsMetadata md = (ModsMetadata)modsUnmarshaller.unmarshal(item.getMetadataRecords().get("escidoc").getContent());
		
		vol = new Volume();
		vol.setModsMetadata(md);
		long timeMods = System.currentTimeMillis()-startMods;
		System.out.println("Time MODS: " + timeMods);
		
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
			System.out.println("Time METS: " + time);
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
		vol.setTeiSd(teiSd);
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
	
	public TeiSd loadTeiSd(Volume vol, String userHandle) throws Exception
	{
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		Document teiSdXml = null;
		
		TeiSd teiSd = null;
		for(Component c : vol.getItem().getComponents())
		{
			
			
			if (c.getProperties().getContentCategory().equals("tei-sd"))
			{

				long start = System.currentTimeMillis();

				IUnmarshallingContext uctx = bfactTei.createUnmarshallingContext();
				teiSd = (TeiSd) uctx.unmarshalDocument(client.retrieveContent(vol.getItem().getObjid(), c.getObjid()), null);



				DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
				fac.setNamespaceAware(true);
				teiSdXml = fac.newDocumentBuilder().parse(client.retrieveContent(vol.getItem().getObjid(), c.getObjid()));
	/*
				Unmarshaller unmarshaller = jaxbTeiContext.createUnmarshaller();
				
				teiSd = (TeiSd)unmarshaller.unmarshal(teiSdXml);
	*/
				
				
				long time = System.currentTimeMillis() - start;
				System.out.println("TIME TEI: " + time);

				
				
			}
		}
		vol.setTeiSd(teiSd);
		vol.setTeiSdXml(teiSdXml);
		return teiSd;
	}
	
	public String loadPagedTei(Volume vol, String userHandle) throws Exception
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
		
		URL url = MabXmlTransformation.class.getClassLoader().getResource("xslt/officialTei/xhtml2/tei.xsl");
		
		SAXSource xsltSource = new SAXSource(new InputSource(url.toExternalForm()));
		
		ByteArrayInputStream bis = new ByteArrayInputStream(pagedTeiResult.getBytes("UTF-8"));
		Source teiXmlSource = new StreamSource(bis);
		
		StringWriter wr = new StringWriter();
		StreamResult res = new StreamResult(wr);
		Transformer transformer = transfFact.newTransformer(xsltSource);
		
		System.out.println(transformer.getClass().getClassLoader());
		
		
		transformer.setParameter("autoToc", "false");
		transformer.setParameter("autoHead", "false");
		transformer.setParameter("institution", "");
		transformer.setParameter("copyrightStatement", "");
		transformer.setParameter("topNavigationPanel", "false");
		transformer.setParameter("showFigures", "false");
		transformer.setParameter("linkPanel", "false");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setParameter("pbid", p.getId());
		transformer.transform(teiXmlSource, res);

		return wr.toString();
	}
	
	public static File addIdsToTei(InputStream teiXml)throws Exception
	{
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToMets/tei_add_ids.xslt");
			
			SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
			
			
			Source teiXmlSource = new StreamSource(teiXml);

			StringWriter wr = new StringWriter();
			File temp = File.createTempFile("tei_with_ids", "xml");
			javax.xml.transform.Result result = new StreamResult(temp);
			

			Transformer transformer = transfFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(teiXmlSource, result);
			
			return temp;
		
	}
	
	public static File applyPbConventionToTei(InputStream teiXml)throws Exception
	{
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToMets/tei_apply_pb_convention.xslt");
			
			SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
			
			
			Source teiXmlSource = new StreamSource(teiXml);

			StringWriter wr = new StringWriter();
			File temp = File.createTempFile("tei_with_pb_convention", "xml");
			javax.xml.transform.Result result = new StreamResult(temp);
			

			Transformer transformer = transfFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(teiXmlSource, result);
			
			return temp;
		
	}
	
	public static String transformTeiToMets(InputStream teiXml)throws Exception
	{
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToMets/tei_to_mets.xslt");
			System.setProperty("javax.xml.transform.TransformerFactory",
					"net.sf.saxon.TransformerFactoryImpl");
			SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
			
			
			Source teiXmlSource = new StreamSource(teiXml);

			StringWriter wr = new StringWriter();
			javax.xml.transform.Result result = new StreamResult(wr);
			
			
			Transformer transformer = transfFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(teiXmlSource, result);
			
			return wr.toString();
		
	}
	
	
	
	
	public String transformTeiToPagedTei(InputStream teiXml)throws Exception
	{
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToPagedTei/teiToPagedTei.xsl");
			System.setProperty("javax.xml.transform.TransformerFactory",
					"net.sf.saxon.TransformerFactoryImpl");
			SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
			Source teiXmlSource = new StreamSource(teiXml);

			StringWriter wr = new StringWriter();
			javax.xml.transform.Result result = new StreamResult(wr);
			
			Transformer transformer = transfFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(teiXmlSource, result);
			
			return wr.toString();
		
	}
	
	public String transformTeiToTeiSd(InputStream teiXml)throws Exception
	{
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToTeiSd/teiToTeiSd.xsl");
			System.setProperty("javax.xml.transform.TransformerFactory",
					"net.sf.saxon.TransformerFactoryImpl");
			SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
			Source teiXmlSource = new StreamSource(teiXml);

			StringWriter wr = new StringWriter();
			javax.xml.transform.Result result = new StreamResult(wr);
			
			Transformer transformer = transfFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(teiXmlSource, result);
			
			return wr.toString();
		
	}
	
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

		
	}
	
	
	
	
	
	private  List<String> getAllPbIds(InputStream tei) throws Exception
	{

		List<String> pbIds = new ArrayList<String>();
		
		XQDataSource ds = new SaxonXQDataSource();
        XQConnection conn = ds.getConnection();
        XQExpression exp = conn.createExpression();
        exp.bindDocument(XQConstants.CONTEXT_ITEM, tei, null, null);
        XQResultSequence res = exp.executeQuery("declare namespace tei='http://www.tei-c.org/ns/1.0'; //tei:pb/@xml:id");
        while(res.next())
        {
        	pbIds.add(res.getNode().getTextContent());
        }
       
        
        conn.commit();
        conn.close();
        
        return pbIds;
		
		
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
	
	
	public  Page getPageForDiv(Volume v, PbOrDiv div) throws Exception
	{

		String pageId = "";
		Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
        XPathExecutable xx = xpath.compile("//tei:*[@xml:id='"+ div.getId()+ "']/preceding::tei:pb[1]/@xml:id");
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
	
	
	public Div getDivForPage(Volume v, Page p) throws Exception
	{
		long start = System.currentTimeMillis();
		String divId = "";
		Processor proc = new Processor(false);
        net.sf.saxon.s9api.DocumentBuilder db = proc.newDocumentBuilder();
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("tei", "http://www.tei-c.org/ns/1.0");
        
        //Check if pagebreak is directly followed by an structural element like div, front, body...
        XPathExecutable xx = xpath.compile("//tei:pb[@xml:id='" + p.getId() + "']/(following::*|following::text()[normalize-space(.)!=''])[1]/(self::tei:front|self::tei:body|self::tei:back|self::tei:titlePage|self::tei:div)/@xml:id");
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
        	xx = xpath.compile("//tei:pb[@xml:id='" + p.getId() + "']/(ancestor::tei:front|ancestor::tei:body|ancestor::tei:back|ancestor::tei:titlePage|ancestor::tei:div)[last()]/@xml:id");
        	selector = xx.load();
            selector.setContextItem(xdmDoc);
            for(XdmItem item : selector) {
                XdmNode node = (XdmNode)item;
                Node attribute = (org.w3c.dom.Node)node.getExternalNode();
                divId = attribute.getTextContent();
            }
        }
        
        Div div = (Div)v.getTeiSd().getDivMap().get(divId);
        long time = System.currentTimeMillis() - start;
        System.out.println("Time for finding div: " + time);
        
        return div;

	
	}
	
	

	

}
