package ca.manitoulin.mtd.web.interceptor;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.util.ApplicationSession;

public class ControllerGlobalInterceptor implements WebRequestInterceptor {
	
	private static final Logger log = Logger.getLogger(ControllerGlobalInterceptor.class);

	@Override
	public void afterCompletion(WebRequest request, Exception ex) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(WebRequest request, ModelMap model) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void preHandle(WebRequest request) throws Exception {
		
		SecureUser userSession = (SecureUser) request.getAttribute(ContextConstants.SESSION_USERPROFILE, WebRequest.SCOPE_SESSION);
		if(userSession != null){
			log.debug("--> put user profile in ThreadLocal");
			ApplicationSession.set(userSession);
		}
	}

}
