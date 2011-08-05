package de.mpg.mpdl.dlc.beans;

import gov.loc.mets.DivType;
import gov.loc.mets.DivType.Fptr;
import gov.loc.mets.FileType;
import gov.loc.mets.FileType.FLocat;
import gov.loc.mets.MdSecType;
import gov.loc.mets.MdSecType.MdWrap;
import gov.loc.mets.MdSecType.MdWrap.MDTYPE;
import gov.loc.mets.MdSecType.MdWrap.XmlData;
import gov.loc.mets.MetsDocument;
import gov.loc.mets.MetsDocument.Mets;
import gov.loc.mets.MetsType.FileSec;
import gov.loc.mets.MetsType.FileSec.FileGrp;
import gov.loc.mets.StructMapType;
import gov.loc.mods.v3.ModsDocument;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.view.facelets.ComponentConfig;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;


import net.sf.saxon.om.NamespaceConstant;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.xpath.XPathEvaluator;
import net.sf.saxon.xqj.SaxonXQDataSource;


import org.apache.axis.transport.http.AdminServlet;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.sun.tools.doclets.internal.toolkit.util.DocFinder.Input;

import de.ddb.application.StandaloneConverter;
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
import de.mpg.mpdl.dlc.mods.MabXmlTransformation;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.MetsFile;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.dlc.vo.Volume;
import de.mpg.mpdl.dlc.vo.mods.ModsMetadata;
import de.mpg.mpdl.dlc.vo.teisd.TeiSd;

@Stateless
public class VolumeServiceBean {
	
	private static Logger logger = Logger.getLogger(VolumeServiceBean.class); 

	
	
	
	public List<Volume> retrieveVolumes(int limit, int offset) throws Exception
	{
		SearchHandlerClient shc = new SearchHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		
		
		String contentModelId = PropertyReader.getProperty("dlc.content-model.id");
		SearchRetrieveResponse resp = shc.search("escidoc.content-model.objid=\"" + contentModelId + "\"", offset, limit, null, "escidoc_all");
		
		List<Volume> volumeList = new ArrayList<Volume>();
		for(SearchResultRecord rec : resp.getRecords())
		{
			Item item = (Item)rec.getRecordData().getContent();
			volumeList.add(createVolumeFromItem(item, null));
		}
		
		return volumeList;
		
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
		
		
		
		//Create a volume
		Volume vol = new Volume();
		vol.setProperties(item.getProperties());
		vol.setModsMetadata(modsMetadata);
		vol.setItem(item);
		

		int i = 0;
		try {
			
			for(FileItem fileItem : images)
			{
				
				String dir = uploadFileToImageServer(fileItem, item.getObjid());
				logger.info("File uploaded to " + dir);
				
				MetsFile f = new MetsFile();
				f.setID("img_" + i);
				f.setMimeType(fileItem.getContentType());
				f.setLocatorType("OTHER");
				f.setHref(dir);
				vol.getFiles().add(f);
				
				Page p = new Page();
				p.setID("page_" + i);
				p.setOrder(i);
				p.setOrderLabel("");
				p.setType("page");
				p.setFile(f);
				vol.getPages().add(p);
				i++;
			}
			
			vol = updateVolume(vol, teiFile, userHandle, true);
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
	
	
	
	public Volume updateVolume(Volume vol, FileItem teiFile, String userHandle, boolean initial) throws Exception
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

				DiskFileItem diskTeiFile = (DiskFileItem)teiFile;
				
				//Transform TEI to Tei-SD and add to component
				String teiSd = transformTeiToTeiSd(teiFile.getInputStream());
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
				
				
				//Add original TEI as component
				URL uploadedTei = sthc.upload(diskTeiFile.getInputStream());
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
	
	private String uploadFileToImageServer(FileItem item, String directory) throws Exception
	{
		/*
		File tmpFile = File.createTempFile(item.getName(), "tmp");
		item.write(tmpFile);
		*/
		HttpClient client = new HttpClient( );

		String weblintURL = PropertyReader.getProperty("image-upload.url");
    	PostMethod method = new PostMethod(weblintURL);
    	Part[] parts = new Part[2];
    	parts[0] = new StringPart("directory", directory);
    	parts[1] = new FilePart(item.getName(), new ByteArrayPartSource(item.getName(), item.get()));
    	
    	//parts[1] = new FilePart( item.getName(), tmpFile );
    	HttpMethodParams params = new HttpMethodParams();
    	method.setRequestEntity(new MultipartRequestEntity(parts, params));
    	String username = PropertyReader.getProperty("image-upload.username");
    	String password = PropertyReader.getProperty("image-upload.password");
    	String handle = "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes()));
    	method.addRequestHeader("authorization", handle);
    	// Execute and print response
    	client.executeMethod( method );
    	String response = method.getResponseBodyAsString( );
    	logger.info("Image Upload servlet responded with: " + method.getStatusCode() + "\n" + response);
    	method.releaseConnection( );
    	if(response == null || !(method.getStatusCode()==201 || method.getStatusCode()==200))
    	{
    		throw new RuntimeException("File Upload Servlet responded with: " + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
    	}

    	return response;
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
		
		TeiSd teiSd = null;
		Volume vol = null;
		for(Component c : item.getComponents())
		{
			if (c.getProperties().getContentCategory().equals("mets"))
			{
				
				JAXBContext ctx = JAXBContext.newInstance(new Class[] { Volume.class });
				Unmarshaller unmarshaller = ctx.createUnmarshaller();
				vol = (Volume)unmarshaller.unmarshal(client.retrieveContent(item.getObjid(), c.getObjid()));
				

			}
			
			else if (c.getProperties().getContentCategory().equals("tei-sd"))
			{
				
				JAXBContext ctx = JAXBContext.newInstance(new Class[] { TeiSd.class });
				Unmarshaller unmarshaller = ctx.createUnmarshaller();
				teiSd = (TeiSd)unmarshaller.unmarshal(client.retrieveContent(item.getObjid(), c.getObjid()));
			}
		}
		
		vol.setItem(item);
		vol.setProperties(item.getProperties());
		vol.setTeiSd(teiSd);
		
		
		return null;
	}
	
	
	public static ModsMetadata createModsMetadataFromXml(InputStream xml) throws Exception
	{
		
		JAXBContext ctx = JAXBContext.newInstance(new Class[] { Volume.class });
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		ModsMetadata md = (ModsMetadata)unmarshaller.unmarshal(xml);

		return md;
		
		
	}
	
	
	public String transformTeiToTeiSd(InputStream teiXml)throws Exception
	{
		
			URL url = VolumeServiceBean.class.getClassLoader().getResource("xslt/teiToTeiSd/teiToTeiSd.xsl");
			System.setProperty("javax.xml.transform.TransformerFactory",
					"net.sf.saxon.TransformerFactoryImpl");
			File resultingXhtml = null;
			SAXSource xsltSource = new SAXSource(new InputSource(url.openStream()));
			Source teiXmlSource = new StreamSource(teiXml);
			
		
			resultingXhtml = File.createTempFile("transformed", "xhtml");
			StringWriter wr = new StringWriter();
			javax.xml.transform.Result result = new StreamResult(wr);
			
			TransformerFactory transfFactory = TransformerFactory.newInstance();
			Transformer transformer = transfFactory.newTransformer(xsltSource);
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
}
