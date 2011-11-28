package de.mpg.mpdl.dlc.beans;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import de.escidoc.core.client.ContextHandlerClient;
import de.escidoc.core.client.OrganizationalUnitHandlerClient;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.util.PropertyReader;

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
	
	public List<Context> retrieveContextsCreatedBy(String id)
	{
		logger.info("Retrieving OU contexts created by" + id);
        List<Context> contextList = new ArrayList<Context>();
        try
        {
	        SearchRetrieveResponse response = null;
			ContextHandlerClient contextClient = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
			SearchRetrieveRequestType req = new SearchRetrieveRequestType();
			req.setQuery(" \"/properties/type\"=DLC and" +"\"/properties/created-by/id\"="+ id);
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
}
