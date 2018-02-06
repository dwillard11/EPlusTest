package ca.manitoulin.mtd.web.servlet;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.operationconsole.TripDocument;
import ca.manitoulin.mtd.service.operationconsole.ITripService;
import ca.manitoulin.mtd.util.DocumentManager;
import ca.manitoulin.mtd.util.IDocumentManager;
import ca.manitoulin.mtd.util.json.JsonHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class UploadServlet
 */
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final Logger log = Logger.getLogger(UploadServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadServlet() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//We do not support GET
		log.warn("GET METHOD IS NOT SUPPORTED");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> map = new HashMap<String, String>();

		try{
			TripDocument document = doSave(request, response);
			map.put("docId", document.getId().toString());
			map.put(ContextConstants.KEY_RESULT, ContextConstants.RESPONSE_SUCCESS);
		}catch(Exception e){
			map.put(ContextConstants.KEY_RESULT, ContextConstants.RESPONSE_ERROR);
			map.put(ContextConstants.KEY_MESSAGE, e.getMessage());			
		}
		response.getWriter().write(JsonHelper.toJsonString(map));
	}
	
	private TripDocument doSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("--> begin uploading file with apache file upload tool");
		// Handle parameters from multipart form with appach-common
		DiskFileItemFactory diskFactory = new DiskFileItemFactory();

		diskFactory.setSizeThreshold(8192);

		ServletFileUpload upload = new ServletFileUpload(diskFactory);
		//1024*1024*10
		upload.setFileSizeMax(10485760L);
		upload.setHeaderEncoding("utf-8");

		List<FileItem> fileItems = new ArrayList<FileItem>();
		try {
			fileItems = upload.parseRequest(request);
		} catch (FileUploadException e) {
			log.error("Unable to parse file upload request", e);
			throw e;
		}

		HashMap requestParam = new HashMap();
		FileItem file = null;
		for (FileItem item : fileItems) {
			if (item.isFormField()) {
				String paramName = item.getFieldName();
				String paramValue = item.getString("utf-8");
				if (log.isDebugEnabled()) {
					log.debug("field in the multipart form: " + paramName + ":" + paramValue);
				}
				requestParam.put(paramName, paramValue);
			} else {
				file = item;
			}
		}

		if (file == null) {
			String msg = "NO FILE FOUND IN THE REQUEST. USE MULTI-FORM!";
			log.error(msg);
			throw new IOException(msg);
		}

		// TODO file type and size validation
		String fileExt = StringUtils.trimToEmpty(StringUtils.substringAfterLast(file.getName(), "."));
		if ("exe".equalsIgnoreCase(fileExt)) {
			String msg = "ILLEGAL FILE TYPE: " + fileExt;
			log.error(msg);
			throw new IOException(msg);
		}
		if (file.getSize() > 50 * 1024 * 1024) {
			String msg = "FILE SIZE SHOULD BE LESS THAN 50 MB, CURRENT FILE SIZE(BYTES) IS " + file.getSize();
			log.error(msg);
			throw new IOException(msg);
		}

		String id = StringUtils.trimToNull((String) requestParam.get("tripid"));
		if (id == null)
			throw new Exception("PLS SPECIFY 'tripid' IN THE REQUEST ");
		String fileType = StringUtils.trimToNull((String) requestParam.get("filetype"));

		if (fileType == null)
			throw new Exception("PLS SPECIFY 'filetype' IN THE REQUEST ");

		String originalFileName = file.getName();
		int filesize = (int) (file.getSize() / 1000);
		int tripId = Integer.parseInt(id);

		log.info("--> Prepare to upload file: " + originalFileName);

		// Get file path
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
		IDocumentManager docService = context.getBean(IDocumentManager.class);
		
		String filePath = docService.getFilePathToSave(tripId, originalFileName);

		log.info("--> Begin to upload file to " + filePath);
		// Save file
		File fileToSave = new File(filePath);
		file.write(fileToSave);
		log.info("--> Upload file complete: " + filePath);
		// Write to epDocument
		TripDocument doc = new TripDocument();
		doc.setFilesize(filesize);
		doc.setFileType(fileType);
		doc.setOriginalFileName(originalFileName);
		doc.setTripId(tripId);
		doc.setFilename(StringUtils.substringAfterLast(filePath, File.separator));

		
		ITripService tripService = context.getBean(ITripService.class);
		tripService.createDocument(doc, false);

		log.info("--> Document info saved, id= " + doc.getId());
		// return success
		return doc;
	}

}
