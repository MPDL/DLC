package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;

import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.exceptions.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.ContextProperties;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.util.PropertyReader;
import de.mpg.mpdl.dlc.vo.collection.Collection;


public class ContextServiceBean {
	private static Logger logger = Logger.getLogger(ContextServiceBean.class);
	


	
	public Collection retrieveCollection(String id, String userHandle)
	{
		Collection collection = null;
		Context context = retrieveContext(id, userHandle);
		if(context != null)
		{
			collection = new Collection();
			collection.setId(id);
			collection.setName(context.getProperties().getName());
			collection.setDescription(context.getProperties().getDescription());
			collection.setOuId(context.getProperties().getOrganizationalUnitRefs().get(0).getObjid());
			collection.setOuTitle(context.getProperties().getOrganizationalUnitRefs().get(0).getXLinkTitle());
			collection.setType(context.getProperties().getType());
		}
		return collection;
	}
	
	public Context retrieveContext(String contextId, String userHandle)
	{
		logger.info("Retrieving Context " + contextId);
        Context context = new Context();
        try
        {
			ContextHandlerClient contextClient = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			if(userHandle!=null)
				contextClient.setHandle(userHandle);
			context = contextClient.retrieve(contextId);
			if("CLOSED".equals(context.getProperties().getPublicStatus().name()))
				return null;
        }catch(Exception e)
        {
        	logger.error("Error while retrieving Context", e);
        }
		return context;
	}
	
	public List<Context> retrieveOUContexts(String id, boolean sortByName) 
	{
		logger.info("Retrieving OU contexts " + id);
        List<Context> contextList = new ArrayList<Context>();
        try
        {
	        SearchRetrieveResponse response = null;
			ContextHandlerClient contextClient = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			SearchRetrieveRequestType req = new SearchRetrieveRequestType();
			//TODO
			req.setQuery("\"/properties/public-status\"=opened and " + "\"/properties/type\"=DLC and" +"\"/properties/organizational-units/organizational-unit/id\"="+id);
			req.setMaximumRecords(new NonNegativeInteger("10000"));
			if(sortByName)
			{
				req.setQuery(req.getQuery() + " sortby \"/sort/properties/name\"");
			}
			else
			{
				req.setQuery(req.getQuery() + " sortby \"/sort/properties/creation-date\" /sort.descending");
			}
			response = contextClient.retrieveContexts(req);
			for(SearchResultRecord rec : response.getRecords())
			{
				Context context = (Context)rec.getRecordData().getContent();
				contextList.add(context);
			}
			logger.info("OU contexts retrieved " + id);
        }catch(Exception e)
        {
        	logger.error("Error while retrieving OU contexts", e);
        }
        return contextList;		
	}
	
	public List<Collection> retrieveCollectionsCreatedBy(String userHandle, String id)
	{  
		logger.info("Retrieving DLC Collections Created by" + id);
		List<Collection> collections = new ArrayList<Collection>();
		for(Context c : retrieveContextsCreatedBy(userHandle, id))
			collections.add(retrieveCollection(c.getObjid(), userHandle));
		return collections;
	}
	
	public List<Collection> retrieveOUCollections(String userHandle, String ouId)
	{  
		logger.info("Retrieving OU Collections" + ouId);
		List<Collection> collections = new ArrayList<Collection>();
		for(Context c : retrieveOUContexts(ouId, false))
			collections.add(retrieveCollection(c.getObjid(), userHandle));
		return collections;
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
			//TODO
			req.setQuery("\"/properties/public-status\"=opened and " + "\"/properties/type\"=DLC and" +"\"/properties/created-by/id\"="+ id + "  sortby \"/sort/properties/name\"");
			req.setMaximumRecords(new NonNegativeInteger("10000"));
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
			req.setQuery("\"/properties/public-status\"=opened and " + " \"/properties/type\"=DLC sortby \"/sort/properties/name\"");
			//req.setSortKeys("\"/sort/properties/name\"");
			req.setMaximumRecords(new NonNegativeInteger("10000"));
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
	
	
	public Context updateContext(Collection collection, String userHandle) throws ContextNameNotUniqueException 
	{
		logger.info("Updating context " + collection.getId() );
		Context context = new Context();
		try
		{
	    	ContextHandlerClient  client = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
	    	client.setHandle(userHandle);
	    	context = client.retrieve(collection.getId());
	    	
	    	context = prepareContext(context, collection);
	    	
	    	context = client.update(context);
			
		}catch(ContextNameNotUniqueException e)
		{	
			logger.error("Error while updating new context",e);
			throw new ContextNameNotUniqueException(e.getMessage(), e.getCause());
			
		} catch (EscidocException e) {
			e.printStackTrace();
		} catch (InternalClientException e) {

			e.printStackTrace();
		} catch (TransportException e) {
			
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return context;
	}

	
	public Context createNewContext(Collection c, String userHandle) throws ContextNameNotUniqueException
	{ 
		logger.info("Creating new context");
		Context context = new Context();
		context = prepareContext(context, c);
		try
		{
	    	ContextHandlerClient  client = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
	    	client.setHandle(userHandle);
	    	context = client.create(context);
	    	
	    	//open context
			TaskParam taskParam=new TaskParam(); 
		    taskParam.setComment("Open Context");
		    taskParam.setLastModificationDate(context.getLastModificationDate());
		    
		    client.open(context, taskParam);
	    	
		}catch(ContextNameNotUniqueException e)
		{	
			logger.error("Error while creating new context",e);
			throw new ContextNameNotUniqueException(e.getMessage(), e.getCause());
			
		} catch (EscidocException e) {
			e.printStackTrace();
		} catch (InternalClientException e) {

			e.printStackTrace();
		} catch (TransportException e) {
			
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
      
		return context;
	}
	
	private Context prepareContext(Context context, Collection collection)
	{  
		logger.info("Preparing new Context");
        ContextProperties properties = new ContextProperties();
        properties.setName(collection.getName());
        properties.setDescription(collection.getDescription());
        properties.setType("DLC");
        OrganizationalUnitRefs ous = new OrganizationalUnitRefs();
        ous.add(new OrganizationalUnitRef(collection.getOuId()));
        properties.setOrganizationalUnitRefs(ous);
        context.setProperties(properties);       
		return context;
	}
	
	public Context closeContext(String id, String userHandle) 
	{
		logger.info("Closing new context");
		Context c;
		try
		{
	    	ContextHandlerClient  client = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
	    	client.setHandle(userHandle);
	    	c = client.retrieve(id);
	    	
	    	TaskParam taskParam = new TaskParam();
	    	taskParam.setComment("Close Context");
	    	c = client.retrieve(c.getObjid());
	    	taskParam.setLastModificationDate(c.getLastModificationDate());
	    	
	    	client.close(c.getObjid(), taskParam);

		}catch(Exception e)
		{	
			c= null;
			logger.error("Error while closing new context",e);
		}
		
		return c;
	}
	
	
	public Context deleteContext(String id, String userHandle) 
	{
		logger.info("Closing new context");
		Context c;
		try
		{
	    	ContextHandlerClient  client = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
	    	client.setHandle(userHandle);
	    	c = client.retrieve(id);
	
	    	client.delete(c.getObjid());

		}catch(Exception e)
		{	
			c= null;
			logger.error("Error while closing new context",e);
		}
		
		return c;
	}
	
	
	public static void main(String[] args) throws Exception {
//		System.out.println(createContextAdminDescriptor());
		

		
	}
	
	/*
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

*/

}
