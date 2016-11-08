package pl.johnny.gwtQuiz.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;

public class FileUploadServlet extends HttpServlet {

	private final long FILE_SIZE_LIMIT = 1 * 1024 * 1024; // 1 MiB
	private final Logger logger = Logger.getLogger("UploadServlet");
	/** Name of the directory where image will be saved */
	private String recivedParameter;
	private FileItem uploadedFile;
	private String base64EncodedImageString;
	private File repository;

	/**
	 * https://commons.apache.org/proper/commons-fileupload/using.html
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("multipart/form-data; charset=utf-8");
		
		//Check if a request has multipart content - e.g has image - and if not, return.
		if(!ServletFileUpload.isMultipartContent(req)) return;

		try {
			// Create a factory for disk-based file items
			DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();

			// Configure a repository (to ensure a secure temp location is used)
			ServletContext servletContext = this.getServletConfig().getServletContext();
			repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
			//			System.out.println("TMP folder " + repository.toString());
			fileItemFactory.setRepository(repository);

			// Disabling cleanup of temporary files
			fileItemFactory.setFileCleaningTracker(null);

			// Create a new file upload handler
			ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);

			//Set the maximum allowed size of a complete request , in bytes. 
			fileUpload.setSizeMax(FILE_SIZE_LIMIT);

			// Parse the request...
			List<FileItem> items = fileUpload.parseRequest(req);

			for(FileItem item : items) {
				if(item.isFormField()) {
					logger.log(Level.INFO, "Received form field:");
					logger.log(Level.INFO, "Name: " + item.getFieldName());
					logger.log(Level.INFO, "Value: " + item.getString());

					/*
					 * Receive currently highest id (+1) from client so we can 
					 * create folder with this value in 
					 * /GWTQuiz/src/main/webapp/quiz_resources/question_images/{recivedParameter}
					 */
//					recivedParameter = item.getString();

				} else {
					logger.log(Level.INFO, "Received file:");
					logger.log(Level.INFO, "Name: " + item.getName());
					logger.log(Level.INFO, "Size: " + item.getSize());
					logger.log(Level.INFO, item.getContentType());

					//Handle uploaded image to field so it can be used out of for loop 
					uploadedFile = item;

					//Get uploaded image as bytes representation
					byte[] imageAsBytes = item.get();
					//Convert uploaded image bytes representation to base64
					byte[] imageAsBase64 = Base64.encodeBase64(imageAsBytes);
					//Convert base64 image from bytes to string and handle it to field so it can be used out of for loop 
					base64EncodedImageString = new String(imageAsBase64);

				}

				//Set of ifs to validate uploaded image.
				if(!item.isFormField()) {
					//Check size of uploaded image.
					if(item.getSize() > FILE_SIZE_LIMIT) {
						resp.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
								"File size exceeds limit");
						return;
					}
					/*
					 * Check if uploaded image is of type image/png ,jpeg, img or jpg 
					 * and if not - send adequate error message.
					 */
					if(!item.getContentType().matches("image\\/(png|jpeg|jpg|img)")) {
						resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
								"Uploaded file is NOT in one of those formats: png ,jpeg, img ,jpg.");
						return;
					}
				}
			}

			//Create directory with name of actual id
			//			File directory = new File(recivedParameter);
			File directory = new File(repository.toString());
			directory.mkdirs();

			//After getting field with id and image from request, save it.
//			logger.log(Level.INFO, "recivedParameter out of for " + recivedParameter);
			//			File newFile = new File(directory.toString() + File.separatorChar + uploadedFile.getName());
			//			File newFile = new File(repository.toString() + File.separatorChar + directory.toString() + File.separatorChar + uploadedFile.getName());
			File newFile = new File(directory.toString() + File.separatorChar + uploadedFile.getName().toString());
			uploadedFile.write(newFile);

			if(!uploadedFile.isInMemory())
				uploadedFile.delete();

			//Send response to client with newly added image url.
			PrintWriter out = resp.getWriter();
			
			//Create JSON object to strore our response.
			JSONObject obj = new JSONObject();
			obj.put("base64EncodedString", base64EncodedImageString);
			obj.put("pathToFile", newFile.toString());
			
			System.out.println("Filename in Upload servlet: " + newFile);
			System.out.println("resp.getContentType();" + resp.getContentType());
			
			//Send JSON over the servlet response.
			out.println(obj);
			out.flush();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Throwing servlet exception for unhandled exception", e);
			throw new ServletException(e);
		}
	}
}