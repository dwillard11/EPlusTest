package ca.manitoulin.mtd.web.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.misc.CommunicationEmail;
import ca.manitoulin.mtd.dto.operationconsole.TripDocument;
import ca.manitoulin.mtd.service.operationconsole.ITripService;
import ca.manitoulin.mtd.util.IDocumentManager;
import ca.manitoulin.mtd.util.json.JsonHelper;

/**
 * Servlet implementation class DownloadServlet
 */
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(DownloadServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadServlet() {
        super();
       
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//We do not support GET
        doPost(request, response);
        log.warn("GET METHOD IS NOT SUPPORTED");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		try{
			doDownload(request, response);
		}catch(Exception e){
			Map<String, String> map = new HashMap<String, String>();
			map.put(ContextConstants.KEY_RESULT, ContextConstants.RESPONSE_ERROR);
			map.put(ContextConstants.KEY_MESSAGE, e.getMessage());	
			response.getWriter().write(JsonHelper.toJsonString(map));
		}
		
		
	}
	
	private void doDownload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
//		String id = StringUtils.trimToNull(request.getParameter("tripid"));
//		if(id == null) throw new Exception("PLS SPECIFY 'tripid' IN THE REQUEST ");
//		String originalFilename = StringUtils.trimToNull(request.getParameter("filename"));
//		if(originalFilename == null) throw new Exception("PLS SPECIFY 'filename' IN THE REQUEST ");
//		int tripId = Integer.parseInt(id);
		
		String id = StringUtils.trimToNull(request.getParameter("id"));
		if(id == null) throw new Exception("PLS SPECIFY 'id' IN THE REQUEST ");
		
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
		ITripService tripService = context.getBean(ITripService.class);		
		IDocumentManager docService = context.getBean(IDocumentManager.class);
		
		TripDocument doc = tripService.retrieveDocumentById(Integer.parseInt(id));		
		if(doc == null)
			throw new Exception("Document is not exists: " + request.getParameter("id"));
		
		String originalFilename = doc.getOriginalFileName();
		String fileName = doc.getFilename();
		Integer tripId = doc.getTripId();
		File file = null;
		
		// in case of Email attachment, go to another folder.
		if(doc.getCommunicationId() != null){
			CommunicationEmail email = tripService.retrieveEmail(doc.getCommunicationId());
			if(email != null && "In".equalsIgnoreCase(email.getType())){
				log.info("--> This is an attachment of an Email in. Try to retrieve file from attachment folder!");
				//file = new File( doc.getOriginalFileName());
				//updated on 25-Jul-2017, system saves only file name of incoming email attachments
				file = new File(docService.getEmailAttachmentFolderPath() + File.separator + doc.getFilename());
			}else{
				file = docService.getDocument(tripId, fileName);
			}
		}else{
			file = docService.getDocument(tripId, fileName);
		}

		if(file.exists() == false){
			throw new IOException("File not exist:" + file.getAbsolutePath());
		}
		
		InputStream inStream = null;
		ServletOutputStream servletOS = null;
		BufferedOutputStream bos = null;
		
		try {
			
			//Retrieve file
			log.info("--> Begin retrieving file from :" + file.getAbsolutePath());

			//Output to response.
			response.reset();
			response.setContentType("application/octet-stream;");
			response.addHeader("Content-Disposition", "attachment; filename=\""
					+ originalFilename + "\"");

			int fileLength = (int) file.length();
			response.setContentLength(fileLength);
			log.info("--> start download file " + originalFilename + " from " + file.getPath());
			if (fileLength != 0) {
				inStream = new FileInputStream(file);
				int buffer = 4096;
				byte[] buf = new byte[buffer];

				servletOS = response.getOutputStream();
				bos = new BufferedOutputStream(servletOS);
				int readLength;
				while ((readLength = inStream.read(buf)) != -1) {
					bos.write(buf, 0, readLength);
				}
			}
			log.info("--> Download complete:" + originalFilename);
		} catch (Exception e) {			
			log.error(e);
			throw e;
		} finally {
			if (inStream != null)
				inStream.close();
			if (bos != null) {
				bos.close();
			}
			if (servletOS != null)
				servletOS.close();
		}
	}
	
	private TripDocument retrieveDocument(HttpServletRequest request) throws Exception{
		
		String id = StringUtils.trimToNull(request.getParameter("id"));
		if(id == null) throw new Exception("PLS SPECIFY 'id' IN THE REQUEST ");
		
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
		ITripService tripService = context.getBean(ITripService.class);
		return tripService.retrieveDocumentById(Integer.parseInt(id));
	}

}
