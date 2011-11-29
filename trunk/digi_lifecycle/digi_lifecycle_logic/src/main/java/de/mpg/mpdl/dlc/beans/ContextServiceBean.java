package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.ContextProperties;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.MetsFile;
import de.mpg.mpdl.dlc.vo.Page;
import de.mpg.mpdl.dlc.vo.collection.Collection;
import de.mpg.mpdl.dlc.vo.collection.DLCAdminDescriptor;
import de.mpg.mpdl.dlc.vo.collection.PageDescriptor;

@Stateless
public class ContextServiceBean {
	private static Logger logger = Logger.getLogger(ContextServiceBean .class);
	
	public Context retrieveContext(String contextId, String userHandle)
	{
		logger.info("Retrieving Context " + contextId);
        Context context = new Context();
        try
        {
			ContextHandlerClient contextClient = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			contextClient.setHandle(userHandle);
			context = contextClient.retrieve(contextId);
        }catch(Exception e)
        {
        	logger.error("Error while retrieving Context", e);
        }
		return context;
	}
	
	public List<Context> retrieveOUContexts(OrganizationalUnit ou) 
	{
		logger.info("Retrieving OU contexts " + ou.getObjid());
        List<Context> contextList = new ArrayList<Context>();
        try
        {
	        SearchRetrieveResponse response = null;
			ContextHandlerClient contextClient = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			SearchRetrieveRequestType req = new SearchRetrieveRequestType();
			req.setQuery(" \"/properties/type\"=DLC and" +"\"/properties/organizational-units/organizational-unit/id\"="+ou.getObjid());
			response = contextClient.retrieveContexts(req);
			for(SearchResultRecord rec : response.getRecords())
			{
				Context context = (Context)rec.getRecordData().getContent();
				contextList.add(context);
			}
			logger.info("OU contexts retrieved " + ou.getObjid());
        }catch(Exception e)
        {
        	logger.error("Error while retrieving OU contexts", e);
        }
        return contextList;		
	}
	
	public List<Context> retrieveContextsCreatedBy(String userHandle, String id)
	{
		logger.info("Retrieving OU contexts created by" + id);
        List<Context> contextList = new ArrayList<Context>();
        try
        {
	        SearchRetrieveResponse response = null;
			ContextHandlerClient contextClient = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			contextClient.setHandle(userHandle);
			SearchRetrieveRequestType req = new SearchRetrieveRequestType();
			req.setQuery("\"/properties/public-status\" =opened or " + "\"/properties/public-status\" =created and "  + " \"/properties/type\"=DLC and" +"\"/properties/created-by/id\"="+ id);
			response = contextClient.retrieveContexts(req);
			for(SearchResultRecord rec : response.getRecords())
			{
				Context context = (Context)rec.getRecordData().getContent();
				contextList.add(context);
			}

        }catch(Exception e)
        {
        	logger.error("Error while retrieving OU contexts", e);
        }
        return contextList;	
	}
	
	public List<Context> retrieveAllcontexts()
	{
		logger.info("Retrieving all contexts");
        List<Context> contextList = new ArrayList<Context>();
        try
        {
	        SearchRetrieveResponse response = null;
			ContextHandlerClient contextClient = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			SearchRetrieveRequestType req = new SearchRetrieveRequestType();
			req.setQuery(" \"/properties/type\"=DLC");
			response = contextClient.retrieveContexts(req);
			for(SearchResultRecord rec : response.getRecords())
			{
				Context context = (Context)rec.getRecordData().getContent();
				contextList.add(context);
			}
        }catch(Exception e)
        {
        	logger.error("Error while retrieving all contexts", e);
        }
        return contextList;		
	}
	
	
	public Context createNewContext(Collection c, String userHandle)
	{
		logger.info("Creating new context");
		Context context =prepareContext(c,userHandle);
		try
		{
	    	ContextHandlerClient  client = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
	    	client.setHandle(userHandle);
	    	context = client.create(context);
	    	
	    	//open context
//			TaskParam taskParam=new TaskParam(); 
//		    taskParam.setComment("Open Context");
//		    taskParam.setLastModificationDate(context.getLastModificationDate());
//		    
//		    client.open(context, taskParam);
	    	
		}catch(Exception e)
		{	
			logger.error("Error while creating new context",e);
		}
      
		return context;
	}
	
	private Context prepareContext(Collection c, String userHandle)
	{
		logger.info("Preparing new Context");
		Context context = new Context();
        ContextProperties properties = new ContextProperties();
        properties.setName(c.getName());
        properties.setDescription(c.getDescription());
        properties.setType("DLC");
        OrganizationalUnitRefs ous = new OrganizationalUnitRefs();
        ous.add(new OrganizationalUnitRef(c.getOrgaId()));
        properties.setOrganizationalUnitRefs(ous);
        context.setProperties(properties);       
		return context;
	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println(createContextAdminDescriptor());
		
	}
	
	public static String createContextAdminDescriptor() throws Exception
	{
		
		MetsFile mets1 = new MetsFile();
		mets1.setHref("escidoc:8008/dt9bg18q+bd1=d0001.jpg");
		mets1.setId("img_0");
		mets1.setLocatorType("OTHER");
		mets1.setMimeType("image/jpeg");
		
		Page p1 = new Page();
		p1.setFile(mets1);
		p1.setId("page_0");
		p1.setOrder(0);
		p1.setType("page");
		
		MetsFile mets2 = new MetsFile();
		mets2.setHref("escidoc:8008/dt9bg18q+bd1=d0002.jpg");
		mets2.setId("img_1");
		mets2.setLocatorType("OTHER");
		mets2.setMimeType("image/jpeg");
		
		Page p2 = new Page();
		p2.setFile(mets1);
		p2.setId("page_1");
		p2.setOrder(1);
		p2.setType("page");
		
		MetsFile mets3 = new MetsFile();
		mets3.setHref("escidoc:8008/dt9bg18q+bd1=d0003.jpg");
		mets3.setId("img_2");
		mets3.setLocatorType("OTHER");
		mets3.setMimeType("image/jpeg");
		
		Page p3 = new Page();
		p3.setFile(mets1);
		p3.setId("page_2");
		p3.setOrder(2);
		p3.setType("page");
		
		PageDescriptor pd1 = new PageDescriptor();
		PageDescriptor pd2 = new PageDescriptor();
		PageDescriptor pd3 = new PageDescriptor();
		pd1.setMets(mets1);
		pd1.setPages(p1);
		
		pd2.setMets(mets2);
		pd2.setPages(p2);
		
		pd3.setMets(mets3);
		pd3.setPages(p3);
	
		List<PageDescriptor> pds = new ArrayList<PageDescriptor>();
		pds.add(pd1);
		pds.add(pd2);
		pds.add(pd3);
		
		DLCAdminDescriptor ad  = new DLCAdminDescriptor();
		ad.setPds(pds);

		
		JAXBContext jxbc = JAXBContext.newInstance(new Class[]{DLCAdminDescriptor.class});
		Marshaller m = jxbc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter sw = new StringWriter();
		m.marshal(ad, sw);
		
		return sw.toString();
	}
}
