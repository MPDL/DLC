package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.OrganizationalUnitHandlerClient;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.oum.Parents;
import de.mpg.mpdl.dlc.util.PropertyReader;

@Stateless
public class OrganizationalUnitServiceBean {
	
	private static Logger logger = Logger.getLogger(OrganizationalUnitServiceBean.class);
	

	public List<OrganizationalUnit> retrieveOus() throws Exception
	{
		List<OrganizationalUnit> ous;
		OrganizationalUnitHandlerClient ouClient = new OrganizationalUnitHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		ouClient.setHandle(null);
		SearchRetrieveRequestType req = new SearchRetrieveRequestType();
		req.setQuery("\"/properties/public-status\"=opened and " + "\"/md-records/md-record/organizational-unit/organization-type\"=DLC");
		ous =  ouClient.retrieveOrganizationalUnitsAsList(req);
		return ous;
	}
	
	public OrganizationalUnit retrieveOu(String id) throws Exception
	
	{
		OrganizationalUnitHandlerClient ouClient = new OrganizationalUnitHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		return ouClient.retrieve(id);
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws Exception{
		String ouId = "escidoc:6001";
		//System.out.println(removeParents(ouId));
		addOU_DLCMDRecord(ouId);

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

}
