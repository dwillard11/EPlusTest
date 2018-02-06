package ca.manitoulin.mtd.web.filter;

import static ca.manitoulin.mtd.constant.ContextConstants.SESSION_USERPROFILE;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class SessionExpireFilter implements Filter {
	
	private static final Logger log = Logger.getLogger(SessionExpireFilter.class);
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		// session.setMaxInactiveInterval(10);
		HttpServletRequest req = (HttpServletRequest) request;
		StringBuffer reqUrl = req.getRequestURL();
		String path = req.getContextPath();
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
		if(session.getAttribute(SESSION_USERPROFILE) != null || reqUrl.indexOf("login.html")!=-1){
			chain.doFilter(request, response);
		} else {
			log.info("## SESSION IS NULL, IT MAYBE EXPIRED!##");
			String url = "/login.html";
			((HttpServletResponse) response).sendRedirect(basePath + url);
		} 
	}

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}
}
