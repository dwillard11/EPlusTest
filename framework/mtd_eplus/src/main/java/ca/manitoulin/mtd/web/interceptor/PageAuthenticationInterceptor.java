package ca.manitoulin.mtd.web.interceptor;

import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.security.RoleAccessRight;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.service.security.ISecurityProfileService;

/**
 * To validate user authentication when requesting a action
 * 
 * @author Bob Yu
 *
 */
public class PageAuthenticationInterceptor extends HandlerInterceptorAdapter {

	private static final Logger log = Logger.getLogger(PageAuthenticationInterceptor.class);
	
	@Autowired
	private ISecurityProfileService securityProfileService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        String url = requestUri.substring(contextPath.length());  
        
        log.debug("\n== PageAuthenticationInterceptor triggered == \n" +
        		"requestUri=" + requestUri + "\n" +
        		"contextPath=" + contextPath + "\n" +
        		"url=" + url + "\n" +
        		"============================================");
        
        
		SecureUser user = (SecureUser) request.getSession().getAttribute(ContextConstants.SESSION_USERPROFILE);

        //login,
        if ("/login.do".equals(url) || "/logout.do".equals(url) || "/relogin.do".equals(url)) {
            log.debug("skip futher checking");
			return true;
		}
		
		//If session expired, go to login page
		if(user == null ){
			 request.getRequestDispatcher("/relogin.do").forward(request, response);
            return true;
        }
		
		//Check user access right. Start with some parttern
		List<RoleAccessRight> permission = user.getPermission();
		boolean isPermitted = false;
		for(RoleAccessRight menu : permission){
			String pattern = StringUtils.replace(menu.getUrl(), "*", "\\S+");
			pattern = StringUtils.replace(pattern, ".", "\\.");
			if(Pattern.matches(pattern, url)){
				isPermitted = true;
				break;
			}
		}

		/*
		if( !isPermitted){
			log.info("User " + user.getUid() + "is not permitted to access " + url);
			//TODO throw new AuthorizationException(user.getUid(), "");
		}
		*/
		//Update user active time stamp
		securityProfileService.saveUserAction(user.getUid());
		
		//TODO save user operation log
		
		return super.preHandle(request, response, handler);
	}

	
}
