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
public class ApplicationServiceBean {
	private static Logger logger = Logger.getLogger(ApplicationServiceBean.class);
	
	public Context retrieveContext(String contextId, String userHandle) throws Exception
	{
		ContextHandlerClient contextClient = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		contextClient.setHandle(userHandle);
		return contextClient.retrieve(contextId);
	}
	public List<OrganizationalUnit> retrieveOus() throws Exception
	{
		List<OrganizationalUnit> ous;
		OrganizationalUnitHandlerClient ouClient = new OrganizationalUnitHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		ouClient.setHandle(null);
		SearchRetrieveRequestType req = new SearchRetrieveRequestType();
		req.setQuery("\"/md-records/md-record/organizational-unit/organization-type\"=dlc");
		ous =  ouClient.retrieveOrganizationalUnitsAsList(req);
		return ous;
	}
	
	
	public List<Context> retrieveOUContexts(OrganizationalUnit ou) throws Exception
	{
        List<Context> contextList = new ArrayList<Context>();
        SearchRetrieveResponse response = null;
		ContextHandlerClient contextClient = new ContextHandlerClient(new URL(PropertyReader.getProperty("escidoc.common.framework.url")));
		SearchRetrieveRequestType req = new SearchRetrieveRequestType();
		req.setQuery("\"/properties/organizational-units/organizational-unit/id\"="+ou.getObjid());
		response = contextClient.retrieveContexts(req);
		for(SearchResultRecord rec : response.getRecords())
		{
			Context context = (Context)rec.getRecordData().getContent();
			contextList.add(context);
		}
        return contextList;		
	}
	
}
