package ca.manitoulin.mtd.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.security.SecureUser;

/**
 * To validate user authentication when requesting a action
 * 
 * @author Bob Yu
 *
 */
public class PageAuthenticationInterceptor extends HandlerInterceptorAdapter {

	private static final Logger log = Logger.getLogger(PageAuthenticationInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Do some validation
		SecureUser user = (SecureUser) request.getSession().getAttribute(ContextConstants.SESSION_USERPROFILE);
		
		return super.preHandle(request, response, handler);
	}

	
}
