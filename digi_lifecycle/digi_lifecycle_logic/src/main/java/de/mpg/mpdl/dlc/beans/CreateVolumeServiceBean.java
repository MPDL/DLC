package de.mpg.mpdl.dlc.beans;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.util.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.StagingHandlerClient;
import de.escidoc.core.client.interfaces.StagingHandlerClientInterface;
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
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.StorageType;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.ComponentContent;
import de.escidoc.core.resources.om.item.component.ComponentProperties;
import de.mpg.mpdl.dlc.images.ImageController;
import de.mpg.mpdl.dlc.images.ImageHelper;
import de.mpg.mpdl.dlc.images.ImageHelper.Type;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem;
import de.mpg.mpdl.dlc.persistence.entities.IngestLogMessage;
import de.mpg.mpdl.dlc.persistence.entities.DatabaseItem.IngestStatus;
import de.mpg.mpdl.dlc.persistence.entities.IngestLogMessage.ActivityType;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.IngestImage;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mets.Mets;
import de.mpg.mpdl.dlc.vo.mets.Page;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;

public class CreateVolumeServiceBean {

	private static Logger logger = Logger.getLogger(CreateVolumeServiceBean.class); 
	
	public VolumeServiceBean volumeServiceBean = new VolumeServiceBean();
	
	private Tika tika = new Tika();
	
	private DatabaseItem dbItem;
	
	private EntityManager em;


	public CreateVolumeServiceBean(DatabaseItem dbItem, EntityManager em) {
		this.dbItem = dbItem;
		this.em = em;
	}
	
	public CreateVolumeServiceBean() {

	}
	
	private void addAndPersistMessage(IngestLogMessage msg)
	{
		if(dbItem!=null)
		{
			
			em.getTransaction().begin();
			em.merge(dbItem);
			dbItem.addMessage(msg);
			//em.persist(msg);
			//em.flush();
			
			em.getTransaction().commit();
		}
		
	}
	
	
	public Item createNewEmptyItem(String contentModel, String contextId,String userHandle, ModsMetadata modsMetadata) 
	{
		
		IngestLogMessage msg = new IngestLogMessage(ActivityType.CREATE_ITEM, "");
		
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
			
			dbItem.setItemId(item.getOriginObjid());
			msg.setIngestStatus(IngestStatus.READY);
			msg.setMessage("Empty item created: " + item.getObjid());
			
		} catch (Exception e) {
			logger.error("Error while creating Item" + e.getMessage());
			msg.setIngestStatus(IngestStatus.ERROR);
			msg.setError(e);
			msg.setMessage("Error while creating Item" + e.getMessage());
			
		} 
		finally{
			addAndPersistMessage(msg);
		}

		return item;
	}  
	
	public Volume createNewMultiVolume(String operation, String contentModel, String contextId, String userHandle, ModsMetadata modsMetadata) throws Exception
	{  
		IngestLogMessage msg = new IngestLogMessage(ActivityType.CREATE_ITEM, "");
		
		Item item = createNewEmptyItem(contentModel, contextId, userHandle, modsMetadata);
		Volume vol= new Volume();
		try
		{
		vol.setProperties(item.getProperties());
		vol.setModsMetadata(modsMetadata);
		vol.setItem(item);
		vol = updateVolume(vol, userHandle, null, null, false);
		if(operation.equalsIgnoreCase("release"))
			vol = releaseVolume(vol.getItem().getObjid(), userHandle);
		
		msg.setIngestStatus(IngestStatus.READY);
		msg.setMessage("Multivolume created: " + vol.getItem().getObjid());
		addAndPersistMessage(msg);
		}
		catch(Exception e)
		{
			logger.error("Error while creating Volume. Trying to rollback", e);
			msg.setMessage("Error while creating Multivolume. Trying to rollback" + e.getMessage());
			msg.setIngestStatus(IngestStatus.ERROR);
			msg.setError(e);
			addAndPersistMessage(msg);
			rollbackCreation(vol, userHandle);
			throw e;
		}
		
		
		
		return vol;
	}
	
	public void uploadImagesAndCreateMets(List<IngestImage> images, IngestImage footer, String itemId, Volume vol) throws Exception
	{ 
	
		
		Mets metsData = new Mets();
		vol.setMets(metsData);
		String itemIdWithoutColon = itemId.replaceAll(":", "_");
		
		
		File jpegFooter = null;
		if(footer != null)
		{
			IngestLogMessage msg = new IngestLogMessage(ActivityType.PROCESS_IMAGE, "Footer: " + footer.getName());
			try {
				String footerMimetype = tika.detect(footer.getFile());

				if("image/tiff".equals(footerMimetype))
				{
					jpegFooter = ImageHelper.tiffToJpeg(footer.getFile(), VolumeServiceBean.getJPEGFilename(footer.getName()));
				}
				else if("image/png".equals(footerMimetype))
					jpegFooter = ImageHelper.pngToJpeg(footer.getFile(), VolumeServiceBean.getJPEGFilename(footer.getName()));
				else if("image/jpeg".equals(footerMimetype))
					jpegFooter = footer.getFile();
				else
				{
					throw new Exception("Invalid image mimetype " + footerMimetype + " for image " + footer.getName());
				}
				msg.setIngestStatus(IngestStatus.READY);
			} catch (Exception e) {
				msg.setIngestStatus(IngestStatus.ERROR);
				msg.setMessage(msg.getMessage() + " " + e.getMessage());
				msg.setError(e);
				throw e;
			}
			finally
			{
				addAndPersistMessage(msg);
			}
		}
		
		
		for(IngestImage imageItem : images)
		{
			IngestLogMessage msg = new IngestLogMessage(ActivityType.PROCESS_IMAGE, "Image: " + imageItem.getName());
			try {
				String filename = VolumeServiceBean.getJPEGFilename(imageItem.getName());
				
				//If image is added on disk, upload it
				if(IngestImage.Type.DISK.equals(imageItem.getType()))	
				{   

					File jpegImage;
					
					String mimetype = tika.detect(imageItem.getFile());
					logger.info("Detected image " + imageItem.getName() + " as " + mimetype);
					if("image/tiff".equals(mimetype))
					{
						jpegImage = ImageHelper.tiffToJpeg(imageItem.getFile(), VolumeServiceBean.getJPEGFilename(imageItem.getName()));
					}
					else if("image/png".equals(mimetype))
					{
						jpegImage = ImageHelper.pngToJpeg(imageItem.getFile(), VolumeServiceBean.getJPEGFilename(imageItem.getName()));
					}
					else if("image/jpeg".equals(mimetype))
						jpegImage = imageItem.getFile();
					else
					{
						throw new Exception("Invalid image mimetype " + mimetype + " for image " + imageItem.getName());
					}

					
					if(jpegFooter!=null)
					{
						jpegImage= ImageHelper.mergeImages(jpegImage, jpegFooter);
					}
					
					msg.setIngestStatus(IngestStatus.READY);
					
					

					String thumbnailsDir =  ImageHelper.THUMBNAILS_DIR + itemIdWithoutColon;
					File thumbnailFile = ImageHelper.scaleImage(jpegImage, filename, Type.THUMBNAIL);
					String thumbnailsResultDir = ImageController.uploadFileToImageServer(thumbnailFile, thumbnailsDir, filename);
					
					String webDir = ImageHelper.WEB_DIR + itemIdWithoutColon;;
					File webFile = ImageHelper.scaleImage(jpegImage, filename, Type.WEB);
					String webResultDir = ImageController.uploadFileToImageServer(webFile, webDir, filename);

					String originalDir = ImageHelper.ORIGINAL_DIR + itemIdWithoutColon;
					File originalFile = ImageHelper.scaleImage(jpegImage, filename, Type.ORIGINAL);
					String originalResultDir = ImageController.uploadFileToImageServer(originalFile, originalDir, filename);
					
					jpegImage.delete();
					thumbnailFile.delete();
					webFile.delete();
					originalFile.delete();

					
				}
					int pos = images.indexOf(imageItem);
					Page p = new Page();
					p.setId("page_" + pos);
					p.setOrder(pos);
					p.setOrderLabel("");

					//p.setType("page");
					p.setContentIds(itemIdWithoutColon + "/"+ filename);
					p.setLabel(imageItem.getName());
					metsData.getPages().add(p);
					
					
			} catch (Exception e) {
				msg.setIngestStatus(IngestStatus.ERROR);
				msg.setMessage(msg.getMessage() + " " + e.getMessage());
				msg.setError(e);
				throw e;
			}
			finally{
				addAndPersistMessage(msg);
			}
			
			
		}		
		if(jpegFooter != null)
			jpegFooter.delete();

	}
	
	public Volume update(Volume volume, String userHandle, String operation,  DiskFileItem teiFile, ModsMetadata modsMetadata, List<IngestImage> images, DiskFileItem cdcFile)
	{
		
		ItemHandlerClient client;
		try
		{
			
			client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);				

			if(images.size() >0)
			{
				uploadImagesAndCreateMets(images, null, volume.getItem().getObjid(), volume);
			}
		
			/*
			if(modsMetadata != null)
			{
				volume.setModsMetadata(modsMetadata);
				MetadataRecord mdRec = item.getMetadataRecords().get("escidoc");
				Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				JAXBContext ctx = JAXBContext.newInstance(new Class[] { ModsMetadata.class });		
				Marshaller marshaller = ctx.createMarshaller();
				marshaller.marshal(modsMetadata, d);
				mdRec.setContent(d.getDocumentElement());
				if(teiFile == null)
					client.update(item);
			}
			*/
			
			InputStream teiInputStream = null;
			if(modsMetadata!=null)
			{
				volume.setModsMetadata(modsMetadata);
			}
			
			
			
			if(teiFile != null || modsMetadata != null || cdcFile!=null)
			{
			
				volume = updateVolume(volume, userHandle, teiFile, cdcFile, true);
			}
			
			if(operation.equalsIgnoreCase("release"))
				volume = releaseVolume(volume.getItem().getObjid(), userHandle);
		
			
		}catch (Exception e) {
			logger.error("Error while updating Item", e);

		} 
		
		
		return volume;
	}
	
	public Volume updateVolume(Volume volume, String userHandle, DiskFileItem teiFile, DiskFileItem cdcFile, boolean updateTeiSd) throws Exception
	{  
		
		IngestLogMessage msg = new IngestLogMessage(ActivityType.UPDATE_ITEM);
		
		Component pagedTeiComponent = null ;
		Component teiComponent = null;
		Component teiSdComponent = null;
		Component cdcComponent = null;

		try 
		{
		
			for(Component c : volume.getItem().getComponents())
			{
				if(c.getProperties().getContentCategory().equals("tei"))
				{
					teiComponent = c;
				}
				else if (c.getProperties().getContentCategory().equals("tei-paged"))
				{
					pagedTeiComponent = c;
				}
				else if (c.getProperties().getContentCategory().equals("tei-sd"))
				{
					teiSdComponent = c;
				}
				else if (c.getProperties().getContentCategory().equals("codicological"))
				{
					cdcComponent = c;
				}
			}

		StagingHandlerClientInterface sthc = new StagingHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		sthc.setHandle(userHandle);
			
		if(teiFile!=null)	
			{
				logger.info("TEI file found");
				File teiFileWithPbConvention = applyPbConventionToTei(new StreamSource(teiFile.getStoreLocation()));
				File teiFileWithIds = addIdsToTei(new StreamSource(teiFileWithPbConvention));

				//Transform TEI to Tei-SD and add to volume
				String teiSdString = transformTeiToTeiSd(new StreamSource(teiFileWithIds));
				IUnmarshallingContext unmCtx = VolumeServiceBean.bfactTei.createUnmarshallingContext();
				TeiSd teiSd = (TeiSd)unmCtx.unmarshalDocument(new StringReader(teiSdString));
				volume.setTeiSd(teiSd);				

				//Add paged TEI as component
				String pagedTei = transformTeiToPagedTei(new StreamSource(teiFileWithIds));
				URL uploadedPagedTei = sthc.upload(new ByteArrayInputStream(pagedTei.getBytes("UTF-8")));
				
				
				
				if(pagedTeiComponent!=null)
				{
					volume.getItem().getComponents().remove(pagedTeiComponent);
				}

				pagedTeiComponent = new Component();
				ComponentProperties pagedTeiCompProps = new ComponentProperties();
				pagedTeiComponent.setProperties(pagedTeiCompProps);
				
				pagedTeiComponent.getProperties().setMimeType("text/xml");
				pagedTeiComponent.getProperties().setContentCategory("tei-paged");
				pagedTeiComponent.getProperties().setVisibility("public");
				ComponentContent pagedTeiContent = new ComponentContent();
				pagedTeiComponent.setContent(pagedTeiContent);
				pagedTeiComponent.getContent().setStorage(StorageType.INTERNAL_MANAGED);
				volume.getItem().getComponents().add(pagedTeiComponent);
				pagedTeiComponent.getContent().setXLinkHref(uploadedPagedTei.toExternalForm());

				//Add original TEI as component
				URL uploadedTei = sthc.upload(new FileInputStream(teiFileWithIds));
				
				if(teiComponent!=null)
				{
					volume.getItem().getComponents().remove(teiComponent);
				}
				
				teiComponent = new Component();
				ComponentProperties teiCompProps = new ComponentProperties();
				teiComponent.setProperties(teiCompProps);
				
				teiComponent.getProperties().setMimeType("text/xml");
				teiComponent.getProperties().setContentCategory("tei");
				teiComponent.getProperties().setVisibility("public");
				teiComponent.getProperties().setFileName(teiFile.getName());
				ComponentContent teiContent = new ComponentContent();
				teiComponent.setContent(teiContent);
				teiComponent.getContent().setStorage(StorageType.INTERNAL_MANAGED);
				volume.getItem().getComponents().add(teiComponent);
				teiComponent.getContent().setXLinkHref(uploadedTei.toExternalForm());
				
				
				teiFileWithPbConvention.delete();
				teiFileWithIds.delete();
			}

			//if vol has a teiSd and it should be updated
			if(volume.getTeiSd()!=null && updateTeiSd)
			{

				IMarshallingContext marshContext = VolumeServiceBean.bfactTei.createMarshallingContext();
				StringWriter sw = new StringWriter();
				marshContext.marshalDocument(volume.getTeiSd(), "utf-8", null, sw);

				//Add Ids to teiSd, if none are there
				File teiSdWithIds = addIdsToTei(new StreamSource(new StringReader(sw.toString())));
				
				//Set ids in mets
				List<XdmNode> pbs = VolumeServiceBean.getAllPbs(new StreamSource(teiSdWithIds));
				XdmNode titlePagePb = VolumeServiceBean.getTitlePagePb(new StreamSource(teiSdWithIds));
				
				String titlePageId = null;
				if(titlePagePb!=null)
				{
					 titlePageId = titlePagePb.getAttributeValue(new QName("http://www.w3.org/XML/1998/namespace", "id"));
				}				
				
				for(int i=0; i< volume.getMets().getPages().size(); i++)
				{
					String pbId = pbs.get(i).getAttributeValue(new QName("http://www.w3.org/XML/1998/namespace","id"));
					String numeration = pbs.get(i).getAttributeValue(new QName("n"));
					volume.getMets().getPages().get(i).setId(pbId);
					volume.getMets().getPages().get(i).setOrderLabel(numeration);
									
					if(pbId.equals(titlePageId))
					{
						volume.getMets().getPages().get(i).setType("titlePage");
					}	
					else
					{
						volume.getMets().getPages().get(i).setType(null);
					}
				}
				
				
				if(teiSdComponent!=null)
				{
					volume.getItem().getComponents().remove(teiSdComponent);
				}
				
				URL uploadedTeiSd = sthc.upload(new FileInputStream(teiSdWithIds));
				teiSdComponent = new Component();
				ComponentProperties teiSdCompProps = new ComponentProperties();
				teiSdComponent.setProperties(teiSdCompProps);
				
				teiSdComponent.getProperties().setMimeType("text/xml");
				teiSdComponent.getProperties().setContentCategory("tei-sd");
				teiSdComponent.getProperties().setVisibility("public");
				ComponentContent teiSdContent = new ComponentContent();
				teiSdComponent.setContent(teiSdContent);
				teiSdComponent.getContent().setStorage(StorageType.INTERNAL_MANAGED);
				volume.getItem().getComponents().add(teiSdComponent);
				teiSdComponent.getContent().setXLinkHref(uploadedTeiSd.toExternalForm());
				
				//If there's no fulltext TEI, use TEI-SD as paged component for search and fulltext display
				if(teiComponent == null)
				{
					//Add paged TEI as component
					String pagedTei = transformTeiToPagedTei(new StreamSource(teiSdWithIds));
					URL uploadedPagedTei = sthc.upload(new ByteArrayInputStream(pagedTei.getBytes("UTF-8")));
					
					if(pagedTeiComponent!=null)
					{
						volume.getItem().getComponents().remove(pagedTeiComponent);
					}

					pagedTeiComponent = new Component();
					ComponentProperties pagedTeiCompProps = new ComponentProperties();
					pagedTeiComponent.setProperties(pagedTeiCompProps);
					
					pagedTeiComponent.getProperties().setMimeType("text/xml");
					pagedTeiComponent.getProperties().setContentCategory("tei-paged");
					pagedTeiComponent.getProperties().setVisibility("public");
					ComponentContent pagedTeiContent = new ComponentContent();
					pagedTeiComponent.setContent(pagedTeiContent);
					pagedTeiComponent.getContent().setStorage(StorageType.INTERNAL_MANAGED);
					
					volume.getItem().getComponents().add(pagedTeiComponent);
									
					pagedTeiComponent.getContent().setXLinkHref(uploadedPagedTei.toExternalForm());
				}
				
				teiSdWithIds.delete();
			}
			
			//Add codicological metadata as component
			if(cdcFile!=null)	
			{
				logger.info("Codicological file found");
				
				URL uploadedCdc = sthc.upload(cdcFile.getStoreLocation());
				
				if(cdcComponent!=null)
				{
					volume.getItem().getComponents().remove(cdcComponent);
				}

				cdcComponent = new Component();
				ComponentProperties cdcCompProps = new ComponentProperties();
				cdcComponent.setProperties(cdcCompProps);
				
				cdcComponent.getProperties().setMimeType("text/xml");
				cdcComponent.getProperties().setContentCategory("codicological");
				cdcComponent.getProperties().setVisibility("public");
				cdcComponent.getProperties().setFileName(cdcFile.getName());
				ComponentContent cdcContent = new ComponentContent();
				cdcComponent.setContent(cdcContent);
				cdcComponent.getContent().setStorage(StorageType.INTERNAL_MANAGED);
				volume.getItem().getComponents().add(cdcComponent);
				cdcComponent.getContent().setXLinkHref(uploadedCdc.toExternalForm());
			}

			//Clear all mdRecords
			volume.getItem().getMetadataRecords().clear();		
			
			//Set escidoc md-record with mods metadata
			MetadataRecord eSciDocMdRec = new MetadataRecord("escidoc");
			volume.getItem().getMetadataRecords().add(eSciDocMdRec);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document modsDoc = builder.newDocument();
			Marshaller m = VolumeServiceBean.jaxbModsContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(volume.getModsMetadata(), modsDoc);
			eSciDocMdRec.setContent(modsDoc.getDocumentElement());
			
			//Set METS in md-record
			if(volume.getMets() != null)
			{
				MetadataRecord metsMdRec = new MetadataRecord("mets");
				volume.getItem().getMetadataRecords().add(metsMdRec);
				IMarshallingContext mCont = VolumeServiceBean.bfactMets.createMarshallingContext();
				StringWriter sw = new StringWriter();
				mCont.marshalDocument(volume.getMets(), "UTF-8", null, sw);
				sw.close();
				Document metsDoc = builder.parse(new InputSource(new StringReader(sw.toString())));
				metsMdRec.setContent(metsDoc.getDocumentElement());
			}
			
			
			
			ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			Item updatedItem = client.update(volume.getItem());
			logger.info("Item updated: " + updatedItem.getObjid());
			
			//Commented out as tei info is no longer needed in md-record
			//updatedItem = updateMd(createVolumeFromItem(updatedItem, userHandle), userHandle);
			
			String currentStatus = updatedItem.getProperties().getVersion().getStatus();
			if(currentStatus.equals("pending") || currentStatus.equals("in-revision"))
			{
				TaskParam taskParam=new TaskParam(); 
			    taskParam.setComment("Submit Volume");
				taskParam.setLastModificationDate(updatedItem.getLastModificationDate());
				
				Result res = client.submit(updatedItem.getObjid(), taskParam);
				logger.info("Item submitted: " + updatedItem.getObjid());
			}
			
		
			
			
			volume = volumeServiceBean.retrieveVolume(updatedItem.getObjid(), userHandle);
			
			msg.setIngestStatus(IngestStatus.READY);
			msg.setMessage("Item " + volume.getItem().getOriginObjid() + " updated successfully");
			
			
			return volume;
			
		} 
		catch (Exception e) 
		{
			msg.setIngestStatus(IngestStatus.READY);
			msg.setMessage("Error while updating Item " + volume.getItem().getOriginObjid());
			msg.setError(e);
			logger.error("Error while updating volume " + volume.getItem().getObjid(), e);
			throw e;
		}	
		finally
		{
			addAndPersistMessage(msg);
		}
	}
	
	/*
	public static Item updateItem(String id) throws Exception
	{
		String url = "http://dlc.mpdl.mpg.de:8080";
		Authentication auth = new Authentication(new URL(url), "sysadmin", "dlcadmin");
		
		ItemHandlerClient client = new ItemHandlerClient(auth.getServiceAddress());
		client.setHandle(auth.getHandle()); 

		Item item = client.retrieve(id);
		
		TaskParam taskParam=new TaskParam(); 
	    taskParam.setComment("Submit Volume");
		taskParam.setLastModificationDate(item.getLastModificationDate());
		
		Result res = client.submit(id, taskParam);
		
		taskParam=new TaskParam(); 
	    taskParam.setComment("Release Volume");
		taskParam.setLastModificationDate(res.getLastModificationDate());
		res = client.release(id, taskParam);
		
		return item;
	}
	*/
	
	public static File addIdsToTei(Source teiXmlSource)throws Exception
	{
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToMets/tei_add_ids.xslt");
			
			SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
			
			
			//Source teiXmlSource = new StreamSource(teiXml);

			StringWriter wr = new StringWriter();
			File temp = File.createTempFile("tei_with_ids", "xml");
			javax.xml.transform.Result result = new StreamResult(temp);
			

			Transformer transformer = VolumeServiceBean.transfFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(teiXmlSource, result);
			
			
			return temp;
		
	}
	
	public static File applyPbConventionToTei(Source teiXmlSource)throws Exception
	{
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToMets/tei_apply_pb_convention.xslt");
			
			SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
			
			
			//Source teiXmlSource = new StreamSource(teiXml);

			StringWriter wr = new StringWriter();
			File temp = File.createTempFile("tei_with_pb_convention", "xml");
			javax.xml.transform.Result result = new StreamResult(temp);
			

			Transformer transformer = VolumeServiceBean.transfFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(teiXmlSource, result);
			
			return temp;
		
	}
	
	public String transformTeiToPagedTei(Source teiXmlSource)throws Exception
	{
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToPagedTei/teiToPagedTei.xsl");
			System.setProperty("javax.xml.transform.TransformerFactory",
					"net.sf.saxon.TransformerFactoryImpl");
			SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
			//Source teiXmlSource = new StreamSource(teiXml);

			StringWriter wr = new StringWriter();
			javax.xml.transform.Result result = new StreamResult(wr);
			
			Transformer transformer = VolumeServiceBean.transfFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(teiXmlSource, result);
			
			return wr.toString();
		
	}
	
	public static String transformTeiToTeiSd(Source teiXmlSource)throws Exception
	{
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToTeiSd/teiToTeiSd.xsl");
			System.setProperty("javax.xml.transform.TransformerFactory",
					"net.sf.saxon.TransformerFactoryImpl");
			SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
			//Source teiXmlSource = new StreamSource(teiXml);

			StringWriter wr = new StringWriter();
			javax.xml.transform.Result result = new StreamResult(wr);
			
			Transformer transformer = VolumeServiceBean.transfFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(teiXmlSource, result);
			
			return wr.toString();
		
	}
	
	public static void validateTei(Source tei) throws Exception
	{
		String teiSd = transformTeiToTeiSd(tei);
		
		List<XdmNode> pbsInTei = VolumeServiceBean.getAllPbs(tei);
		List<XdmNode> pbsInTeiSd = VolumeServiceBean.getAllPbs(new StreamSource(new StringReader(teiSd)));
		
		if(pbsInTei.size()!=pbsInTeiSd.size())
		{
			throw new Exception("Could not correctly convert TEI to TEI SD! (" + pbsInTei.size() + " pagebreaks vs. " + pbsInTeiSd.size() + " pagebreaks)");
		}
		
        SchemaFactory factory = SchemaFactory.newInstance("http://relaxng.org/ns/structure/1.0", "com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory", null);       
        URL url = VolumeServiceBean.class.getClassLoader().getResource("schemas/DLC-TEI.rng");        
        Schema schema = factory.newSchema(new File(url.toURI()));      
        Validator validator = schema.newValidator();       
        validator.validate(tei);    
	}
	
	public static void validateCodicologicalMd(Source cdc) throws Exception
	{
        SchemaFactory factory = SchemaFactory.newInstance("http://relaxng.org/ns/structure/1.0", "com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory", null);       
        URL url = VolumeServiceBean.class.getClassLoader().getResource("schemas/DLC-CDC.rng");        
        Schema schema = factory.newSchema(new File(url.toURI()));      
        Validator validator = schema.newValidator();       
        validator.validate(cdc);    
	}
	
	public String createNewItem(String operation, String contentModel, String contextId, String multiVolumeId, String userHandle, ModsMetadata modsMetadata, List<File> images, File footer, DiskFileItem teiFile, DiskFileItem cdcFile) throws Exception {
		logger.info("Creating new volume/monograph");
		
		IngestLogMessage msg = new IngestLogMessage(ActivityType.CREATE_ITEM);
	
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
				parent = volumeServiceBean.retrieveVolume(multiVolumeId, userHandle);

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
			/*	
			long start = System.currentTimeMillis();		
				for(File image : images)
				{
					String filename = getJPEGFilename(image.getName());
					String itemIdWithoutColon = item.getObjid().replaceAll(":", "_");
					File jpegImage;
					if(image.getName().endsWith("tif"))
					{
						jpegImage = ImageHelper.tiffToJpeg(image, getJPEGFilename(image.getName()));
					}
					else
						jpegImage = image;
					if(footer != null)
					{
						File jpegFooter;
						if(footer.getName().endsWith("tif"))
						{
							jpegFooter = ImageHelper.tiffToJpeg(footer, getJPEGFilename(footer.getName()));
						}	
						else
							jpegFooter = footer;
						jpegImage= ImageHelper.mergeImages(jpegImage, jpegFooter);
							
					}
					String thumbnailsDir =  ImageHelper.THUMBNAILS_DIR + itemIdWithoutColon;
					File thumbnailFile = ImageHelper.scaleImage(jpegImage, filename, Type.THUMBNAIL);
					String thumbnailsResultDir = ImageController.uploadFileToImageServer(thumbnailFile, thumbnailsDir, filename);
									
					String webDir = ImageHelper.WEB_DIR + itemIdWithoutColon;;
					File webFile = ImageHelper.scaleImage(jpegImage, filename, Type.WEB);
					String webResultDir = ImageController.uploadFileToImageServer(webFile, webDir, filename);
					
					String originalDir = ImageHelper.ORIGINAL_DIR + itemIdWithoutColon;
					File originalFile = ImageHelper.scaleImage(jpegImage, filename, Type.ORIGINAL);
					String originalResultDir = ImageController.uploadFileToImageServer(originalFile, originalDir, filename);
					
					
					int pos = images.indexOf(image);
					Page p = new Page();
					
					
					p.setId("page_" + pos);
					

					p.setOrder(pos);
					p.setOrderLabel("");
					p.setType("page");
					p.setContentIds(itemIdWithoutColon + "/"+ filename);
					p.setLabel(image.getName());
					metsData.getPages().add(p);
					
				}				
				long time = System.currentTimeMillis()-start;
				logger.info("Time to upload images: " + time);
			*/
			  
			 	List<IngestImage> ingestImageList = new ArrayList<IngestImage>();
				for(File img : images)
				{
					ingestImageList.add(new IngestImage(img));
				}
				IngestImage iifooter = null;
				if(footer!=null)
				{
					iifooter = new IngestImage(footer);
				}
				uploadImagesAndCreateMets(ingestImageList, iifooter, item.getOriginObjid(), volume);
			
				volume = updateVolume(volume, userHandle, teiFile, cdcFile, true);
			
				if(operation.equalsIgnoreCase("release"))
					volume = releaseVolume(volume.getItem().getObjid(), userHandle);
					
				msg.setIngestStatus(IngestStatus.READY);
				msg.setMessage("Item " + volume.getItem().getOriginObjid() + " created successfully");
				return volume.getItem().getObjid();

			}
		
			catch (Exception e) 
			{
				logger.error("Error while creating Volume. Trying to rollback", e);
				msg.setIngestStatus(IngestStatus.ERROR);
				msg.setMessage("Error while creating Item " + volume.getItem().getOriginObjid()  + " " + e.getMessage());
				msg.setError(e);
				rollbackCreation(volume, userHandle);
				//throw new Exception(e);
				return null;
			}
		finally
		{
			addAndPersistMessage(msg);
		}
			
			
	}
	
	public static DiskFileItem fileToDiskFileItem(File f) throws IOException
	{ 
		DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem(f.getName(), "text/plain", false, f.getName());
		InputStream input = new FileInputStream(f);
		OutputStream os = fileItem.getOutputStream();
		int r = input.read();
		while(r != -1)
		{
			os.write(r);
			r = input.read();
		}
		os.flush();
		return fileItem;
	}
	
	public void rollbackCreation(Volume vol, String userHandle) {
		logger.info("Trying to delete item" + vol.getItem().getObjid());
		try 
		{
			
			//ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			//client.setHandle(userHandle);
			//client.delete(vol.getItem().getObjid());
			deleteVolume(vol, userHandle);
			logger.info("Item and images successfully deleted");
		} 
		catch (Exception e) 
		{
			logger.error("Could not delete item or images" + vol.getItem().getObjid(), e);
			
		}
		
	}
	
	public Volume releaseVolume(String id, String userHandle) throws Exception
	{ 
			IngestLogMessage ilm = new IngestLogMessage(ActivityType.RELEASE_ITEM, "Releasing item " +id);
		try {
			
			
			ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			
			Item item = client.retrieve(id);

			logger.info("Releasing Volume " + id);
			
			//TaskParam taskParam=new TaskParam(); 
			//taskParam.setComment("Submit Volume");
			//taskParam.setLastModificationDate(item.getLastModificationDate());
			
			//Result res = client.submit(id, taskParam);
			
			TaskParam taskParam=new TaskParam(); 
			taskParam.setComment("Release Volume");
			taskParam.setLastModificationDate(item.getLastModificationDate());
			Result res = client.release(id, taskParam);
			ilm.setIngestStatus(IngestStatus.READY);
		} catch (Exception e) {
			ilm.setError(e);
			ilm.setIngestStatus(IngestStatus.ERROR);
			ilm.setMessage(ilm.getMessage() + " " +  e.getMessage());
			throw e;
		}
		finally
		{
			addAndPersistMessage(ilm);
		}
		
		
		return volumeServiceBean.retrieveVolume(id, userHandle);
	}
	
	public void deleteVolume(Volume vol, String userHandle) throws Exception
	{
		IngestLogMessage ilm = new IngestLogMessage(ActivityType.DELETE_ITEM, "Deleting item " + vol.getItem().getOriginObjid());
		
		try {
			ItemHandlerClient itemHandler = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			itemHandler.setHandle(userHandle);
			String versionStatus = vol.getItem().getProperties().getVersion().getStatus();
			
			
			
			if(VolumeServiceBean.volumeContentModelId.equals(vol.getItem().getProperties().getContentModel().getObjid()))
			{
				try {
					removeRelationFromMultiVolume(vol.getRelatedParentVolume(), vol.getItem().getOriginObjid(), userHandle);
				} catch (Exception e) {
					logger.error("Could not remove relation from multivolume or submit/release multivolume "+ vol.getRelatedParentVolume().getItem().getObjid() +"while deleting volume " + vol.getItem().getObjid() + ". Go on with deleting volume.", e);
				}
			}
			
			
			
			//if already submitted, revise first
			if("submitted".equals(versionStatus))
			{
				TaskParam taskParam=new TaskParam(); 
			    taskParam.setComment("Revise Volume for deletion");
				taskParam.setLastModificationDate(vol.getItem().getLastModificationDate());
				Result res = itemHandler.revise(vol.getItem().getObjid(), taskParam);
			}
			
			itemHandler.delete(vol.getItem().getObjid());
			
			if(!VolumeServiceBean.multivolumeContentModelId.equals(vol.getItem().getProperties().getContentModel().getObjid()))
			{
				String dirName = vol.getItem().getObjid().replaceAll(":", "_");
				ImageController.deleteFilesFromImageServer(dirName);
			}
			
			ilm.setIngestStatus(IngestStatus.READY);
		} catch (Exception e) {
			ilm.setError(e);
			ilm.setIngestStatus(IngestStatus.ERROR);
			ilm.setMessage(ilm.getMessage() + " " +  e.getMessage());
			throw e;
		}
		finally
		{
			addAndPersistMessage(ilm);
		}
		
	}
	
	public void withdrawVolume(Volume vol, String userHandle) throws Exception
	{
		IngestLogMessage ilm = new IngestLogMessage(ActivityType.WITHDRAW_ITEM, "Withdraweing item " + vol.getItem().getOriginObjid());
		try {
			ItemHandlerClient itemHandler = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			itemHandler.setHandle(userHandle);
			String versionStatus = vol.getItem().getProperties().getVersion().getStatus();
			
			if(VolumeServiceBean.volumeContentModelId.equals(vol.getItem().getProperties().getContentModel().getObjid()))
			{
				removeRelationFromMultiVolume(vol.getRelatedParentVolume(), vol.getItem().getOriginObjid(), userHandle);
			}
			
			

			TaskParam taskParam=new TaskParam(); 
			taskParam.setComment("Withdraw Volume");
			taskParam.setLastModificationDate(vol.getItem().getLastModificationDate());
			itemHandler.withdraw(vol.getItem().getObjid(), taskParam);
			
			
			if(!VolumeServiceBean.multivolumeContentModelId.equals(vol.getItem().getProperties().getContentModel().getObjid()))
			{
				String dirName = vol.getItem().getObjid().replaceAll(":", "_");
				ImageController.deleteFilesFromImageServer(dirName);
			}
			ilm.setIngestStatus(IngestStatus.READY);
		} catch (Exception e) {
			ilm.setError(e);
			ilm.setIngestStatus(IngestStatus.ERROR);
			ilm.setMessage(ilm.getMessage() + " " +  e.getMessage());
			throw e;
		}
		finally
		{
			addAndPersistMessage(ilm);
		}
		
	}
	
	public Volume removeRelationFromMultiVolume(Volume multiVol, String relationId, String userHandle) throws Exception
	{
		logger.info("Trying to remove relation from Multivolume item" + multiVol.getProperties().getVersion().getObjid());
		
		String oldStatus = multiVol.getItem().getProperties().getVersion().getStatus();
		
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		for(Relation rel : multiVol.getItem().getRelations())
		{
			if(rel.getObjid().equals(relationId))
			{
				multiVol.getItem().getRelations().remove(rel);
				break;
			}
		}
		
		/*
		String param = "<param last-modification-date=\"" + multiVol.getItem().getLastModificationDate() + "\">"
		 + "<relation>"
		 + "<targetId>" + relationId + "</targetId>"
		 + "<predicate>http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasPart</predicate>"
		 + "</relation>"
		 + "</param>";
		
		*/
		Item updatedItem = client.update(multiVol.getItem().getOriginObjid(), multiVol.getItem());
		logger.info("Removed relation from multivolume " + multiVol.getItem().getOriginObjid());
		
		
		
		Volume updatedVol = VolumeServiceBean.createVolumeFromItem(updatedItem, userHandle);
		String currentStatus = updatedVol.getItem().getProperties().getVersion().getStatus();

		DateTime lmd = updatedItem.getLastModificationDate();
		if("pending".equals(currentStatus) || "in-revision".equals(currentStatus))
		{
			TaskParam taskParam=new TaskParam(); 
		    taskParam.setComment("Submit Volume");
			taskParam.setLastModificationDate(updatedVol.getItem().getLastModificationDate());
			Result res2 = client.submit(updatedVol.getItem().getOriginObjid(), taskParam);
			lmd = res2.getLastModificationDate();
			logger.info("Multivolume submitted: " + updatedVol.getItem().getObjid());
		}
		
		
		if(oldStatus.equals("released"))
		{
			
			TaskParam taskParam=new TaskParam(); 
		    taskParam.setComment("Release Volume");
			taskParam.setLastModificationDate(lmd);
			Result res3 = client.release(updatedVol.getItem().getOriginObjid(), taskParam);
			logger.info("Multivolume released: " + updatedVol.getItem().getObjid());
		}

		
		return volumeServiceBean.retrieveVolume(multiVol.getItem().getOriginObjid(), userHandle);
	}
	
	
	
}
