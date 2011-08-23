package de.mpg.mpdl.dlc.beans;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;

import net.sf.saxon.xqj.SaxonXQDataSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ContentModelHandlerClient;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.client.SearchHandlerClient;
import de.escidoc.core.client.StagingHandlerClient;
import de.escidoc.core.client.interfaces.StagingHandlerClientInterface;
import de.escidoc.core.resources.cmm.ContentModel;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Result;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.StorageType;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.ComponentContent;
import de.escidoc.core.resources.om.item.component.ComponentProperties;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.images.ImageController;
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.MetsFile;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.VolumeSearchResult;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;


@Stateless
public class VolumeServiceBean {
	
	private static Logger logger = Logger.getLogger(VolumeServiceBean.class); 
	


	static{
		
	}
	
	private static TransformerFactory transfFact;

	public VolumeServiceBean()
	{
		if(transfFact==null)
		{
			System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
			transfFact = TransformerFactory.newInstance();
		}
	}
	

	
	public VolumeSearchResult retrieveVolumes(int limit, int offset, String userHandle) throws Exception
	{
		SearchHandlerClient shc = new SearchHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		String contentModelId = PropertyReader.getProperty("dlc.content-model.id");
		SearchRetrieveResponse resp = shc.search("escidoc.content-model.objid=\"" + contentModelId + "\"", offset, limit, null, "escidoc_all");
		List<Volume> volumeList = new ArrayList<Volume>();

		for(SearchResultRecord rec : resp.getRecords())
		{
			Item item = (Item)rec.getRecordData().getContent();
			volumeList.add(createVolumeFromItem(item, userHandle));
		}
		return new VolumeSearchResult(volumeList, resp.getNumberOfRecords());
		
	}

	
	
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
	
	
	
	public Volume createNewVolume(String contextId, String userHandle, ModsMetadata modsMetadata, List<FileItem> images, FileItem teiFile) throws Exception
	{
		logger.info("Trying to create a new volume");
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		//Create a dummy Item with dummy md record to get id of item
		Item item = new Item();
		item.getProperties().setContext(new ContextRef(contextId));
		item.getProperties().setContentModel(new ContentModelRef(PropertyReader.getProperty("dlc.content-model.id")));
		MetadataRecords mdRecs = new MetadataRecords();
		MetadataRecord mdRec = new MetadataRecord("escidoc");
		mdRecs.add(mdRec);
		item.setMetadataRecords(mdRecs);
		
		
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { Volume.class });
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.marshal(modsMetadata, d);
		mdRec.setContent(d.getDocumentElement());
		item = client.create(item);
		logger.info("Empty item created: " + item.getObjid());
		
		
		Volume vol = new Volume();
		File teiFileWithIds = null;
		try {
			
			List<String> dirs = ImageController.uploadFilesToImageServer(images, item.getObjid());

			if(teiFile==null)
			{

				for(FileItem fileItem : images)
					{
						int pos = images.indexOf(fileItem);
						MetsFile f = new MetsFile();
						f.setId("img_" + pos);
						f.setMimeType(fileItem.getContentType());
						f.setLocatorType("OTHER");
						f.setHref(dirs.get(pos));
						vol.getFiles().add(f);
						
						Page p = new Page();
						p.setId("page_" + pos);
						p.setOrder(pos);
						p.setOrderLabel("");
						p.setType("page");
						p.setFile(f);
						vol.getPages().add(p);
					}
			}
			
			else
			{
				logger.info("TEI file found");
				teiFileWithIds = addIdsToTei(teiFile.getInputStream());
				String mets = transformTeiToMets(new FileInputStream(teiFileWithIds));
				Unmarshaller unmarshaller = ctx.createUnmarshaller();
				vol = (Volume)unmarshaller.unmarshal(new ByteArrayInputStream(mets.getBytes("UTF-8")));
				
				for(FileItem fileItem : images)
				{

					int pos = images.indexOf(fileItem);
					vol.getFiles().get(pos).setHref(dirs.get(pos));
				}
				
			}
		
		
			vol.setProperties(item.getProperties());
			vol.setModsMetadata(modsMetadata);
			vol.setItem(item);
			vol = updateVolume(vol, teiFileWithIds, userHandle, true);
			vol = releaseVolume(vol, userHandle);
		
			
		} catch (Exception e) {
			logger.error("Error while creating Volume. Trying to rollback", e);
			rollbackCreation(vol, userHandle);
			throw new Exception(e);
		}
		return vol;
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
	
	
	
	public Volume updateVolume(Volume vol, File teiFile, String userHandle, boolean initial) throws Exception
	{
		
		logger.info("Trying to update item " +vol.getProperties().getVersion().getObjid());
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		Item item = vol.getItem();
		
		
		MetadataRecords mdRecs = new MetadataRecords();
		MetadataRecord mdRec = new MetadataRecord("escidoc");
		mdRecs.add(mdRec);
		item.setMetadataRecords(mdRecs);
		
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
        
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { Volume.class });
		Marshaller m = ctx.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(vol.getModsMetadata(), modsDoc);
		mdRec.setContent(modsDoc.getDocumentElement());
		
		
		StringWriter sw = new StringWriter();
		m.marshal(vol, sw);
		sw.flush();
		StagingHandlerClientInterface sthc = new StagingHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		sthc.setHandle(userHandle);
		
		URL uploadedMets = sthc.upload(new ByteArrayInputStream(sw.toString().getBytes("UTF-8")));

		//Check if component already exists
		if (initial)
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
			
			
			if(teiFile!=null)
			{


				
				//Transform TEI to Tei-SD and add to component
				
				String teiSd = transformTeiToPagedTei(new FileInputStream(teiFile));
				URL uploadedTeiSd = sthc.upload(new ByteArrayInputStream(teiSd.getBytes("UTF-8")));
				Component teiSdComponent = new Component();
				ComponentProperties teiSdCompProps = new ComponentProperties();
				teiSdComponent.setProperties(teiSdCompProps);
				
				teiSdComponent.getProperties().setMimeType("text/xml");
				teiSdComponent.getProperties().setContentCategory("tei-paged");
				teiSdComponent.getProperties().setVisibility("public");
				ComponentContent teiSdContent = new ComponentContent();
				teiSdComponent.setContent(teiSdContent);
				teiSdComponent.getContent().setStorage(StorageType.INTERNAL_MANAGED);
				teiSdComponent.getContent().setXLinkHref(uploadedTeiSd.toExternalForm());
				item.getComponents().add(teiSdComponent);
				
				
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
		
		
		String url = "http://latest-coreservice.mpdl.mpg.de";
		Authentication auth = new Authentication(new URL(url), "sysadmin", "sysadmin");
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
	
	private static Volume createVolumeFromItem(Item item, String userHandle) throws Exception
	{
		//MetadataRecord mdRec = item.getMetadataRecords().get("escidoc");
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		//TeiSd teiSd = null;
		Volume vol = null;
		String tei = null;
		String pagedTei = null;
		for(Component c : item.getComponents())
		{
			
			if (c.getProperties().getContentCategory().equals("mets"))
			{
				
				long start = System.currentTimeMillis();
				JAXBContext ctx = JAXBContext.newInstance(new Class[] { Volume.class });
				Unmarshaller unmarshaller = ctx.createUnmarshaller();
				vol = (Volume)unmarshaller.unmarshal(client.retrieveContent(item.getObjid(), c.getObjid()));
				long time = System.currentTimeMillis()-start;
				System.out.println("Time METS: " + time);
				

			}
			
			
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
			
			
			
		}
		
		vol.setItem(item);
		vol.setProperties(item.getProperties());
		vol.setTei(tei);
		vol.setPagedTei(pagedTei);
		
		
		return vol;
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
		
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { Volume.class });
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
		transformer.setParameter("autoToc", "false");
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
	
	public static int validateTei(InputStream teiXml) throws Exception
	{
		
		 XQDataSource ds = new SaxonXQDataSource();
         XQConnection conn = ds.getConnection();
         XQExpression exp = conn.createExpression();
         exp.bindDocument(XQConstants.CONTEXT_ITEM, teiXml, null, null);
         XQResultSequence res = exp.executeQuery("declare namespace tei='http://www.tei-c.org/ns/1.0'; count(//tei:pb)");
         res.next();
         int pbNumber = Integer.parseInt(res.getItemAsString(null));
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
	
	public VolumeSearchResult quickSearchVolumes(String query, int limit, int offset) throws Exception
	{
		SearchHandlerClient shc = new SearchHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		String cqlQuery ="escidoc.content-model.objid=\"" + PropertyReader.getProperty("dlc.content-model.id") + "\" and escidoc.metadata=\"" + query + "\"";
		logger.info(cqlQuery);
		SearchRetrieveResponse resp = shc.search(cqlQuery, offset, limit, null, "escidoc_all");
		
		List<Volume> volumeResult = new ArrayList<Volume>();
		
		for(SearchResultRecord rec : resp.getRecords())
		{
			Item item = (Item)rec.getRecordData().getContent();
			volumeResult.add(createVolumeFromItem(item, null));
			
		}
		return new VolumeSearchResult(volumeResult, resp.getNumberOfRecords());
		
		
		
	}
}
