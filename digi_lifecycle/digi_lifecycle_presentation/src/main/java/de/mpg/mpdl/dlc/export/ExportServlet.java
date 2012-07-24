package de.mpg.mpdl.dlc.export;

import java.io.IOException;
import java.io.OutputStream;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;




/**
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public class ExportServlet extends HttpServlet 
{
	private static Logger logger = Logger.getLogger(ExportServlet.class);
	private static final long serialVersionUID = 1L;
	private String contentType = "";
	private int status = 404;
	private String name = "";
	private byte[] content = null;
	private OutputStream outStream = null;

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
    	
		try 
		{
			response.reset();
			response.setContentType(this.contentType);
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-disposition", "attachment; filename="+name+".pdf");
			response.setStatus(this.status);			
			//outStream = response.getOutputStream();
            outStream.write(this.content);
            outStream.flush();
            outStream.close();
		} 
		catch (Exception e) 
		{
			try {
				response.sendError(404, "Internal server error during pdf export: " + e.getMessage());
			} catch (IOException ie) {
				this.logger.error(ie.getStackTrace());
			}
		}
    }
    
    public void createPdfResponse(byte[] pdf, String id) throws IOException
    {
    	this.contentType ="application/pdf";
    	this.status = 200;
    	this.content = pdf;
    	this.name = id;
    	
    	HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();   
    	outStream = FacesContext.getCurrentInstance().getExternalContext().getResponseOutputStream();
    	this.doPost(null, response);
    }
    
}
