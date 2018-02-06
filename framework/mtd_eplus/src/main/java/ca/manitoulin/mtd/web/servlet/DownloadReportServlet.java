package ca.manitoulin.mtd.web.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.security.SecureUser;

/**
 * Servlet implementation class DownloadReportServlet
 */
public class DownloadReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(DownloadReportServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadReportServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//report id, can be null if only request a PDF.
		String reportId = request.getParameter("rpt");
		//file name, can be null when newly generate report
		String filename = request.getParameter("f");
		//force update? when Y, ignore existing PDF file, re-generate one.
		String forceUpdate = request.getParameter("u");
		//Report parameters
		String params = request.getParameter("p");
		
		//TODO do some validation
		SecureUser user = (SecureUser) request.getSession().getAttribute(ContextConstants.SESSION_USERPROFILE);
		if(user == null){
			// prompt message: You are forbidden to visit this page!
			response.getWriter().append("You are forbidden to visit this page!");
			return ;
		}
		
		//forceUpdate = Y
		if("Y".equals(forceUpdate)){
			//generate report
			
		}else{
			//Check PDF file exists?
			File pdfFile = null;
			
			if(pdfFile.exists()){
				//return pdfFile
			}else{
				//generate report
			}
		}
		
		//return pdf file as an output stream.
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}
	
	
	private File getPDF(String version){
		return null;
	}
}
