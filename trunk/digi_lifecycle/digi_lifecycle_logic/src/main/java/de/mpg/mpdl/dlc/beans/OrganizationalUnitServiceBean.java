package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.OrganizationalUnitHandlerClient;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.oum.OrganizationalUnitProperties;
import de.escidoc.core.resources.oum.Parents;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.organization.DLCMetadata;
import de.mpg.mpdl.dlc.vo.organization.EscidocMetadata;
import de.mpg.mpdl.dlc.vo.organization.FoafOrganization;
import de.mpg.mpdl.dlc.vo.organization.Organization;

@Stateless
public class OrganizationalUnitServiceBean {
	
	private static Logger logger = Logger.getLogger(OrganizationalUnitServiceBean.class);
	

	public List<OrganizationalUnit> retrieveOUs()
	{
		logger.info("Retrieving OUs");
		List<OrganizationalUnit> ous = new ArrayList<OrganizationalUnit>();
		try
		{
			OrganizationalUnitHandlerClient client = new OrganizationalUnitHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(null);
			SearchRetrieveRequestType req = new SearchRetrieveRequestType();
			req.setQuery("\"/properties/public-status\"=opened and " + "\"/md-records/md-record/organizational-unit/organization-type\"=DLC");
			ous =  client.retrieveOrganizationalUnitsAsList(req);
		}catch(Exception e)
		{
			logger.error("Error while Retrieving OUs", e);
		}
		return ous;
	}
	 
	public List<OrganizationalUnit> retrieveOUsCreatedBy(String userHandle, String id)
	{
		logger.info("Retrieving OU created by " + id);
		List<OrganizationalUnit> ous = new ArrayList<OrganizationalUnit>();
		try
		{
			OrganizationalUnitHandlerClient client = new OrganizationalUnitHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			SearchRetrieveRequestType req = new SearchRetrieveRequestType();
			req.setQuery("\"/properties/public-status\" =opened or " + "\"/properties/public-status\" =created and " + "\"/properties/created-by/id\"=" + id+" and " + "\"/md-records/md-record/organizational-unit/organization-type\"=DLC");
			ous =  client.retrieveOrganizationalUnitsAsList(req);
		}catch(Exception e)
		{
			logger.error("Error while Retrieving OU created by", e);
		}
		return ous;
	}
	
	public OrganizationalUnit retrieveOU(String id) throws Exception
	{
		logger.info("Retrieving OU " + id);
		OrganizationalUnit ou = new OrganizationalUnit();
		try
		{
			OrganizationalUnitHandlerClient client = new OrganizationalUnitHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			ou = client.retrieve(id);
		}catch(Exception e)
		{
			logger.error("Error while Retrieving OU", e);
		}
		return ou;
	}
	
	public Organization retrieveOrganization(String id)
	{  
		logger.info("Retrieving Organization object " + id);
		Organization o = new Organization();
		try
		{	
			o.setId(id);
		  	
			OrganizationalUnitHandlerClient client = new OrganizationalUnitHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
	
			MetadataRecord escidocMD = client.retrieveMdRecord(id, "escidoc");
			MetadataRecord dlcMD = client.retrieveMdRecord(id, "dlc");
			
			JAXBContext jxbc;
			Unmarshaller unmarshaller;
			jxbc = JAXBContext.newInstance(new Class[]{ DLCMetadata.class });
			unmarshaller = jxbc.createUnmarshaller();
			DLCMetadata dlcMd = (DLCMetadata) unmarshaller.unmarshal(dlcMD.getContent());
			o.setDlcMd(dlcMd);
			
			jxbc = JAXBContext.newInstance(new Class[]{ EscidocMetadata.class });
			unmarshaller = jxbc.createUnmarshaller();
			
			EscidocMetadata escidocMd = (EscidocMetadata) unmarshaller.unmarshal(escidocMD.getContent());
			o.setEscidocMd(escidocMd);
		}catch(Exception e)
		{
			logger.error("Error while Retrieving Organization object", e);
		}		
		return o;
	}
	
	
	public OrganizationalUnit updateOU(Organization orga, String userHandle)
	{
		logger.info("updating OU "+orga.getId());
		OrganizationalUnit ou = new OrganizationalUnit();
		try
		{
			OrganizationalUnitHandlerClient client = new OrganizationalUnitHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
			ou = client.retrieve(orga.getId());
			
			JAXBContext jxbc;
			Marshaller marschaller;
			Document d;
			d= DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			jxbc = JAXBContext.newInstance(new Class[]{ DLCMetadata.class });
			marschaller = jxbc.createMarshaller();
			marschaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			DLCMetadata dlc = orga.getDlcMd();
			marschaller.marshal(dlc, d);
			MetadataRecord dlcMd = client.retrieveMdRecord(orga.getId(), "dlc");
			dlcMd.setContent(d.getDocumentElement());
	
	 
			jxbc = JAXBContext.newInstance(new Class[]{ EscidocMetadata.class});
			marschaller = jxbc.createMarshaller();
			marschaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			EscidocMetadata esciDoc = orga.getEscidocMd();
			d= DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			marschaller.marshal(esciDoc, d);	
			MetadataRecord escidocMd = client.retrieveMdRecord(orga.getId(), "escidoc");
			escidocMd.setContent(d.getDocumentElement());
			MetadataRecords mdRecords = ou.getMetadataRecords();
			mdRecords.setLastModificationDate(ou.getLastModificationDate());
			mdRecords.clear();
			mdRecords.add(dlcMd); 
			mdRecords.add(escidocMd);
			ou.setMetadataRecords(mdRecords);
			ou = client.update(ou);
		}catch(Exception e)
		{
		logger.error("Error while Updating OU", e);
		}	
		return ou;
		 
	}
	
	public OrganizationalUnit createOU(Organization orga, String userHandle)
	{   
		logger.info("Trying to create a new organization");
		OrganizationalUnit ou = new OrganizationalUnit();
		try{
			OrganizationalUnitHandlerClient client = new OrganizationalUnitHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			client.setHandle(userHandle);
	
			JAXBContext jxbc;
			Marshaller marschaller;
			Document d;
			d= DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			jxbc = JAXBContext.newInstance(new Class[]{ DLCMetadata.class });
			marschaller = jxbc.createMarshaller();
			marschaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			DLCMetadata dlc = orga.getDlcMd();
			marschaller.marshal(dlc, d);
	        MetadataRecord dlcMd = new MetadataRecord("dlc");
			dlcMd.setContent(d.getDocumentElement());
			System.out.println(dlcMd.getContent().getTextContent());
	
	 
			jxbc = JAXBContext.newInstance(new Class[]{ EscidocMetadata.class});
			marschaller = jxbc.createMarshaller();
			marschaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			EscidocMetadata esciDoc = orga.getEscidocMd();
			esciDoc.setType("DLC");
			d= DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			marschaller.marshal(esciDoc, d);	
	        MetadataRecord escidocMd = new MetadataRecord("escidoc");
			escidocMd.setContent(d.getDocumentElement());
	
			MetadataRecords mdRecords = new MetadataRecords();
	
			mdRecords.add(dlcMd); 
			mdRecords.add(escidocMd);
			
	
	
			OrganizationalUnitProperties properties = new OrganizationalUnitProperties();
			properties.setName(orga.getEscidocMd().getTitle());
			
			ou.setProperties(properties);
			ou.setMetadataRecords(mdRecords);
	  
			ou = client.create(ou);
			//TODO open new ou
			logger.info("new Organization created: " + ou.getObjid());
		
		}catch(Exception e){
			logger.error("Error while creating OrganizationalUnit Object", e);
		}
		
		return ou;
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws Exception
	{
		String ouId = "escidoc:6002";

		
//        System.out.println(removeParents(ouId));
//		addOU_DLCMDRecord(ouId);
		createNew();
	}
	
	
	public static Organization createNew() throws Exception
	{
		String url = "http://latest-coreservice.mpdl.mpg.de";
		Authentication auth = new Authentication(new URL(url), "sysadmin", "dlc");
		Organization ou = new Organization();
		OrganizationalUnitHandlerClient client = new OrganizationalUnitHandlerClient(auth.getServiceAddress());
        client.setHandle(auth.getHandle());



		JAXBContext jxbc;
		jxbc = JAXBContext.newInstance(new Class[]{ DLCMetadata.class });
		FoafOrganization foafOU= new FoafOrganization();
		foafOU.setEmail("test@test.de");
		foafOU.setHomePageURL("http://test.de");
		foafOU.setImgURL("http://google.com");
		
		DLCMetadata dlc = new DLCMetadata();
		dlc.setFoafOrganization(foafOU);
		
		Marshaller m;
		m = jxbc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter sw = new StringWriter();
		
		m.marshal(dlc, sw);
		System.out.println(sw.toString());
		
		
		ou.setDlcMd(dlc);		
		
		EscidocMetadata escidoc = new EscidocMetadata();
		escidoc.setAlternativeTitle("test");
		escidoc.setCity("test");
		jxbc = JAXBContext.newInstance(new Class[]{ EscidocMetadata.class });
		m = jxbc.createMarshaller();
		m.marshal(escidoc, sw);
		System.out.println(sw.toString());
		return ou;
	}
	
	public static String addOU_DLCMDRecord(String ouId) throws Exception
	{
		String url = "http://latest-coreservice.mpdl.mpg.de";
		Authentication auth = new Authentication(new URL(url), "sysadmin", "dlc");
		OrganizationalUnitHandlerClient client = new OrganizationalUnitHandlerClient(auth.getServiceAddress());
        client.setHandle(auth.getHandle());
        OrganizationalUnit ou = client.retrieve(ouId);
        
        MetadataRecord mdRecord = new MetadataRecord("dlc");

        String str =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    			+"<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
    				+"<foaf:Organization xmlns:foaf=\"http://xmlns.com/foaf/0.1/\">\n"
    					+"<foaf:img rdf:resource=\"/logo.gif\"/>"
    					+"<foaf:homepage rdf:resource=\"http://test.mpg.de/\"/>"
    					+"<foaf:mbox rdf:resource=\"test.test@test.mpg.de/\"/>"
    				+"</foaf:Organization>\n"
    			+"</rdf:RDF>";
        
        InputStream in = new ByteArrayInputStream(str.getBytes());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(in);
        mdRecord.setContent(doc.getDocumentElement());

        // add mdRecord to set
        MetadataRecords mdRecords = ou.getMetadataRecords();
        mdRecords.add(mdRecord);

        // add metadata-records to OU
        ou.setMetadataRecords(mdRecords);
        client.update(ou);

		return "";
	}
	
	public static String removeParents(String ouId) throws Exception
	{
		String url = "http://latest-coreservice.mpdl.mpg.de";
		Authentication auth = new Authentication(new URL(url), "sysadmin", "dlc");
		OrganizationalUnitHandlerClient ouc = new OrganizationalUnitHandlerClient(auth.getServiceAddress());
        ouc.setHandle(auth.getHandle());
        OrganizationalUnit ou = ouc.retrieve(ouId);
        
        Parents parents = new Parents();
//        parents.add(new Parent("escidoc:6001"));
//        parents.add(new Parent("escidoc:6002"));
        parents.setLastModificationDate(ou.getLastModificationDate());
        ouc.updateParents(ouId, parents );
        

        return  "";
	}
	
	public static String convertContentToString(Element content) throws Exception
	{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(content);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
        
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try
        {
	        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        int n;
	        while((n=reader.read(buffer)) != -1)
	        {
	        	writer.write(buffer,0,n);
	        }
        }finally{
        	is.close();
        }
        return writer.toString();
	}

}
