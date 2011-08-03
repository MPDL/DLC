package de.mpg.mpdl.dlc.ingest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class UploadedFile implements FileItem{
	
	private static Logger logger = Logger.getLogger(UploadedFile.class);
	private InputStream inputStream;
	private String name;
	private String contentType;
	private byte[] imageStream;
	
	public UploadedFile(String name, String contentType, byte[] imageStream)
	{
		this.name = name;
		this.contentType = contentType;
		this.imageStream = imageStream;
	}

	public  void setInputStream(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType; 
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public boolean isInMemory() {
		return false;
	}

	public long getSize() {
		return imageStream.length;
	}

	public byte[] get() 
	{ 
		return imageStream;

	}

	public String getString(String encoding)
			throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(File file) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	}

	public String getFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFieldName(String name) {
		// TODO Auto-generated method stub
		
	}

	public boolean isFormField() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setFormField(boolean state) {
		// TODO Auto-generated method stub
		
	}

	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	


}
