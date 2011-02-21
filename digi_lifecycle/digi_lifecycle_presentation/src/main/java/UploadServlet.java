import java.io.*;
import javax.servlet.http.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.util.*;

public class UploadServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		service(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		service(request, response);
	}

	public void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			// Check that we have a file upload request
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart) {
				// Create a factory for disk-based file items
				FileItemFactory factory = new DiskFileItemFactory();
				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);
				// Parse the request
				List<FileItem> items = upload.parseRequest(request);
				// Process the uploaded items
				Iterator iter = items.iterator();
				System.out.println(items.size() + " :::::size:::::");
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();
					if (item.isFormField()) {
						processFormField(item);
					} else {
						processUploadedFile(item);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processFormField(FileItem item) {
		String name = item.getFieldName();
		String value = item.getString();
		System.out.println("Item name: " + name + " value: " + value);
	}

	private void processUploadedFile(FileItem item) throws Exception {
		String fieldName = item.getFieldName();
		String fileName = item.getName();
		String contentType = item.getContentType();
		boolean isInMemory = item.isInMemory();
		long sizeInBytes = item.getSize();
		boolean writeToFile = true;
		if (sizeInBytes > (5 * 1024 * 1024)) {
			writeToFile = false;
		}
		// Process a file upload
		/*
		if (writeToFile) {
			File uploadedFile = new File("<directory on the server' file system>" + fileName);
			if (!uploadedFile.exists()) {
				uploadedFile.createNewFile();
			}
			item.write(uploadedFile);
		} else {
			System.out.println("Trying to write a large file."); 
		}
		*/
	}
}