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

import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
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
import org.w3c.dom.Element;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.ContentModelHandlerClient;
import de.escidoc.core.client.ItemHandlerClient;
import de.escidoc.core.resources.cmm.ContentModel;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.item.Item;
import de.mpg.mpdl.dlc.util.PropertyReader;

@Stateless
public class IngestServiceBean {
	
	private static Logger logger = Logger.getLogger(IngestServiceBean.class); 

	public void createNewVolume(String contextId, String userHandle, ModsDocument modsDoc, List<FileItem> images) throws Exception
	{
		logger.info("CREATE NEW VOLUME");
		ItemHandlerClient client = new ItemHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		client.setHandle(userHandle);
		
		Item item = new Item();
		item.getProperties().setContext(new ContextRef(contextId));
		item.getProperties().setContentModel(new ContentModelRef(PropertyReader.getProperty("dlc.content-model.id")));
		
		
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
		dataCursor.toNextToken();
		XmlCursor modsCursor = modsDoc.getMods().newCursor();
		modsCursor.copyXml(dataCursor);
		modsCursor.dispose();
		dataCursor.dispose();
		
		
		//Add METS xml to mdRecord of Item
		mdRec.setContent((Element)metsDoc.getMets().getDomNode());
		
		
		
		item = client.create(item);
		logger.info("Item created: " + item.getObjid());
		
		
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
		
		for(FileItem fileItem : images)
		{
			
			String dir = uploadFileToImageServer(fileItem, item.getObjid());
			logger.info("File uploaded to " + dir);
			FileType f = imageFileGrp.addNewFile();
			f.setMIMETYPE("image/jpg");
			String fileId = "img_" + i;
			f.setID(fileId);
			
			FLocat loc = f.addNewFLocat();
			loc.setLOCTYPE(FLocat.LOCTYPE.OTHER);
			loc.setHref(dir);
			
			DivType pageDiv = physicalMainDiv.addNewDiv();
			pageDiv.setTYPE("page");
			pageDiv.setORDER(BigInteger.valueOf(i));
			pageDiv.setID("page_"+ i);
			Fptr fileptr = pageDiv.addNewFptr();
			fileptr.setFILEID(fileId);
			i++;
		}
		
		
		
		
		mdRec.setContent((Element)metsDoc.getMets().getDomNode());
		item.getMetadataRecords().clear();
		item.getMetadataRecords().add(mdRec);
		item = client.update(item);
		logger.info("Item updated: " + item.getObjid());
		
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
    	method.releaseConnection( );
    	if(!(method.getStatusCode()==201 || method.getStatusCode()==200))
    	{
    		throw new RuntimeException("File Upload Servlet responded with: " + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
    	}

    	return response;
	}
	
	public static void main(String[] args) throws Exception
	{
		String url = "http://latest-coreservice.mpdl.mpg.de";
		Authentication auth = new Authentication(new URL(url), "dlc_user", "dlc");
		ModsDocument modsDoc = ModsDocument.Factory.newInstance();
		modsDoc.addNewMods().addNewTitleInfo().addNewTitle().set("Test Title");
		
		
		
		
		ItemHandlerClient client = new ItemHandlerClient(new URL(url));
		client.setHandle(auth.getHandle());
		
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
		
		
		
		
		
		/*
		ContentModelHandlerClient cmh = new ContentModelHandlerClient(new URL(url));
		cmh.setHandle(auth.getHandle());
		
		ContentModel cm = new ContentModel();
		cm.getProperties().setName("Content_Model_DLC_ITEMs");
		cm.getProperties().setDescription("Content Model for Digitization Lifecycle Items");
		
		cm = cmh.create(cm);
		System.out.println(cm.getObjid());
		 */
		
		
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
}
