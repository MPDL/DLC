package de.mpg.mpdl.dlc.export;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.remote.application.notfound.ResourceNotFoundException;
import de.mpg.mpdl.dlc.util.PropertyReader;




/**
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public class OaiServlet extends HttpServlet 
{
	private static Logger logger = Logger.getLogger(OaiServlet.class);
	private static final long serialVersionUID = 1L;
	private String contentType = "";
	private int status = 404;
	private byte[] content = null;
	private OutputStream outStream = null;
	private String OAI_URL= null; 

	/**
     * Http get method for export interface.
     * @param request
     * @param response
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        this.doPost(request, response);
    }

    /**
     * Http post method for export interface.
     * @param request
     * @param response
     */
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
    {
        String query = request.getQueryString();
        String format = request.getParameter("metadataPrefix");
        String id = request.getParameter("identifier");
        String verb = request.getParameter("verb");    
		
		try 
		{
	    	
	        if (format != null && format.equalsIgnoreCase("zvdd"))
	        {
	        	//Simply make a request for oai_dc format, as all records can be retrieved in zvdd and 
	        	//oai_dc format, but escidoc oai provider only knows oai_dc format.
	        	if (verb != null && verb.equals("ListRecords"))
	        	{
	        		query = query.replace("zvdd", "oai_dc");
			        this.content = getFromEscidocOaiProvider(query).getBytes("UTF-8");
			        this.contentType ="application/xml";
	        	}
	        	
	        	else
	        	{
	        		Export export = new Export();
	        		this.content = export.metsModsExport(id.replace("oai:escidoc.org:", ""));
	        		this.status = 200;
	        		this.contentType ="application/xml";
	        	}
	        }
	        
	        else
	        {
		        this.content = getFromEscidocOaiProvider(query).getBytes("UTF-8");
		        this.contentType ="application/xml";
	        }

			response.setContentType(this.contentType);
			response.setCharacterEncoding("UTF-8");			
			outStream = response.getOutputStream();
			response.setStatus(this.status);			
            outStream.write(this.content);
            outStream.flush();
            outStream.close();
		} 
		catch (ResourceNotFoundException re) 
		{
			try {
				response.sendError(400, "Resource with identifier " + id + " not found in the repository.");
			} catch (IOException ie) {
				System.err.println(ie.getStackTrace());
			}
		}
		catch (HttpException he) 
		{
			try {
				response.sendError(400, he.getMessage());
			} catch (IOException ie) {
				System.err.println(ie.getStackTrace());
			}
		}
		catch (Exception e) 
		{
			try {
				response.sendError(404, "Internal server error during export: " + e.getMessage());
				System.out.println(e.getLocalizedMessage());
			} catch (IOException ie) {
				System.err.println(ie.getStackTrace());
			}
		}
    }
    
    /**
     * This methods calls the escidoc oai provider and forwards the response.
     * @param query
     * @return the response from the escidoc oai provider
     * @throws MalformedURLException
     * @throws IOException
     * @throws URISyntaxException
     */
	private String getFromEscidocOaiProvider(String query) throws MalformedURLException, IOException, URISyntaxException 
	{
		String resultXml = "";
		InputStreamReader isReader;
		BufferedReader bReader;
        URLConnection conn = null;
        
        //Set url of escidoc oai provider
		String context = "/oai/?";
		String base ="";
		try {
			base = PropertyReader.getProperty("dlc.instance.url");
			//base = "http://dlc.mpdl.mpg.de:8080";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
		this.OAI_URL = base + context;

    	URL url = new URL(OAI_URL + query);    	
    	conn = url.openConnection();

        HttpURLConnection httpConn = (HttpURLConnection) conn;
        this.status = httpConn.getResponseCode();
		
        switch (status)
        {
        	case 200:
			logger.info("Source responded with 200.");			
            isReader = new InputStreamReader(httpConn.getInputStream(),"UTF-8");
            bReader = new BufferedReader(isReader);
            String line = "";
            while ((line = bReader.readLine()) != null)
            {
            	resultXml += line + "\n";
            }
            httpConn.disconnect(); 			
            break;
        }    
		return resultXml;
	}
}
