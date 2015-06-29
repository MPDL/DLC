package de.mpg.mpdl.dlc.export;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Date;
import java.util.Arrays;
import java.util.Comparator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;

import de.escidoc.core.client.SearchHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.mpg.mpdl.dlc.oai.DLCOAIProvider;
import de.mpg.mpdl.dlc.oai.DLCOAIUtils;
import de.mpg.mpdl.dlc.util.PropertyReader;

@WebServlet("/oai_admin")
public class OaiAdminServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(OaiAdminServlet.class);
	private final String REGEX = "((20|21)\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";
	DLCOAIProvider provider = new DLCOAIProvider();

	/**
	 * Http get method for OAI Admin interface.
	 * 
	 * @param request
	 * @param response
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		this.doPost(request, response);
	}

	/**
	 * Http post method for OAI Admin interface.
	 * 
	 * @param request
	 * @param response
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String content = "";
		String verb = request.getParameter("verb");
		String identifier = request.getParameter("identifier");
		try {
			if (verb != null && verb.equalsIgnoreCase("update") && identifier != null) {
				if (identifier.startsWith("ctx:")) {
					String searchQuery = "escidoc.public-status=released and escidoc.context.objid=escidoc:" + identifier.substring(4);
					content = "Trying to update OAI data store with all items for context: " + identifier.replace("ctx", "escidoc");
					searchAndUpdate(searchQuery);

				} else if (identifier.startsWith("escidoc:")) {
					DLCOAIUtils utils = new DLCOAIUtils();
					content = "Trying to update OAI data store for item: "
							+ identifier;
					Item i = utils.getItem(identifier);
					provider.addOrUpdateOAIDataStore(i);

				} else if (identifier.matches(REGEX)) {
					String searchQuery = "escidoc.latest-release.date>"
							+ identifier;
					content = "Trying to update OAI data store with all items newer than "
							+ identifier;
					searchAndUpdate(searchQuery);

				} else {
					content = "Missing or incorrect URL parameters<br>"
							+ "required parameters: verb, identifier<br>"
							+ "identifier must be: ctx | id | date<br>"
							+ "id must start with escidoc:<br>"
							+ "date must be in format YYYY-MM-dd";
				}
			} else {
				if (verb != null && verb.equalsIgnoreCase("status") && identifier == null) {
					content = checkOAIDataStore(null);
				} else if (verb != null && verb.equalsIgnoreCase("status") && identifier != null) {
					content = checkOAIDataStore(identifier);
				} else {
				
					try {
						response.sendError(400,
								"ERROR!!! verb and / or identifier parameter is missing or invalid");
						
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
						e.printStackTrace();
					}
				}
				
			}
			
			response.setCharacterEncoding("UTF-8");			
			OutputStream outStream = response.getOutputStream();
			response.setStatus(200);	
			response.setContentType("text/html");
            outStream.write(content.getBytes());
            outStream.flush();
            outStream.close();
            
		} catch (IOException | URISyntaxException | EscidocClientException e) {
			try {
				response.sendError(400, "ERROR!!!" + e.getMessage());
			} catch (IOException e1) {
				logger.error(e.getMessage(), e);
				e1.printStackTrace();
			}
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

	}

	private void searchAndUpdate(String queryString)
			throws MalformedURLException, IOException, URISyntaxException,
			InternalClientException, TransportException, EscidocClientException {

		SearchHandlerClient sh = new SearchHandlerClient(new URL(
				PropertyReader.getProperty("escidoc.common.framework.url")));
		SearchRetrieveRequestType srrt = new SearchRetrieveRequestType();
		srrt.setMaximumRecords(new NonNegativeInteger("10000"));
		srrt.setQuery(queryString);

		SearchRetrieveResponse response = sh.search(srrt, "dlc_index");
		Item item = null;
		for (SearchResultRecord record : response.getRecords()) {
			item = (Item) record.getRecordData().getContent();
			provider.addOrUpdateOAIDataStore(item);
		}
	}
	
	private String checkOAIDataStore(String identifier) {
		StringBuilder sb = null;
		if (identifier != null) {
			final String filename = identifier.replace("escidoc:", "dlc_") + ".xml";
			try {
				File data_store = new File(PropertyReader.getProperty("oai.provider.data.store"));
				FilenameFilter dlc_filter = new FilenameFilter()
		        {
		            public boolean accept(File file, String name)
		            {
		                return name.equalsIgnoreCase(filename);
		            }
		        };
	        	File[] mets_files = data_store.listFiles(dlc_filter);
	        	if (mets_files.length > 0) {
	        		String result = mets_files[0].getName() + " was added on " + new Date(mets_files[0].lastModified()) + "<br>";
	        		return result;
	        	} else {
	        		String result = "There is no file for " + identifier + " in the OAI data store<br>";
	        		return result;
	        	}
			} catch (IOException | URISyntaxException e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}

		} else {
			try {
				File data_store = new File(PropertyReader.getProperty("oai.provider.data.store"));
				FilenameFilter dlc_filter = new FilenameFilter()
		        {
		            public boolean accept(File file, String name)
		            {
		                return name.startsWith("dlc_");
		            }
		        };
		        if (data_store.isDirectory()) {
		        	sb = new StringBuilder();
		        	File[] mets_files = data_store.listFiles(dlc_filter);
		        	sb.append("OAI data store currently contains " + mets_files.length + " Files.<br><br>");
		        	Arrays.sort(mets_files, new Comparator<File>() {
		        	    public int compare(File f1, File f2) {
		        	        return Long.compare(f1.lastModified(), f2.lastModified());
		        	    }
		        	});
		        	for (File f : mets_files) {
		        		sb.append(f.getName() + " was added on " + new Date(f.lastModified()) + "<br>");
		        	}
		        	return sb.toString();
		        }
			} catch (IOException | URISyntaxException e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
