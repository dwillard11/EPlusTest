package ca.manitoulin.mtd.web.controller.security;

import ca.manitoulin.mtd.dto.security.SecureMenu;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.security.SecureUserGroup;
import ca.manitoulin.mtd.exception.security.AuthenticationException;
import ca.manitoulin.mtd.service.misc.impl.LocalizedMessageService;
import ca.manitoulin.mtd.service.security.IAuthenticationService;
import ca.manitoulin.mtd.service.security.impl.SecurityProfileService;
import ca.manitoulin.mtd.service.share.IShareURLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ca.manitoulin.mtd.constant.ContextConstants.*;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Controller
public class SecurityProfileController {


	@Autowired
	private IAuthenticationService authenticationService;

	@Autowired
	private SecurityProfileService securityProfileService;
	
	@Autowired
	private LocalizedMessageService messageService;
	
	@Autowired
	private IShareURLService shareURLService;

	@RequestMapping(value = "/groups", method = RequestMethod.PATCH)
	@ResponseBody
	public List<SecureUserGroup> retrieveGroups(
			@RequestParam(value = "organizationCode", required = false, defaultValue = "") String organizationCode) {
		return null;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public Object login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> map = newHashMap();
        String userId = trimToEmpty(request.getParameter("userId"));
        String password = trimToEmpty(request.getParameter("password"));
        String lang = lowerCase(trimToEmpty(request.getParameter("lang")));
        String ipAddress = request.getRemoteAddr();
		//TODO
		String host = "";
		
		SecureUser user = authenticationService.signIn(userId, password, ipAddress, host, lang);

        HttpSession session = request.getSession();
		session.setAttribute(SESSION_USERPROFILE, user);
        map.put(PARAM_USER, user);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
		
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		session.invalidate();
        response.sendRedirect(request.getContextPath() + "/#/access/signin");
    }
	
	@RequestMapping(value = "/relogin", method = RequestMethod.GET)
	@ResponseBody
    public Object relogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        HttpSession session = request.getSession();
        SecureUser sessionUser = (SecureUser) session.getAttribute(SESSION_USERPROFILE);

        if (null == sessionUser) {
            throw new AuthenticationException("Please sign-in");
        }
        map.put(PARAM_USER, sessionUser);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }


    @RequestMapping(value = "/switchLanguage", method = RequestMethod.GET)
    @ResponseBody
	public Object switchLanguage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String language = lowerCase(request.getParameter("language"));
		
		// String userId = request.getParameter("userId");
		HttpSession session = request.getSession();
		SecureUser sessionUser = (SecureUser) session.getAttribute(SESSION_USERPROFILE);
		String userId = sessionUser.getId();

		//Update database
		authenticationService.switchLanguage(userId, language);
		
		// Save user profile to session		
		sessionUser.setReferLanguage(language);
		session.setAttribute(SESSION_USERPROFILE, sessionUser);

		List<String> args = new ArrayList<String>();
		args.add(userId);
		// String infoMessage = messageService.mergeMessage("info.saved", args, secureUser.getLocale());

		// map.put(KEY_MESSAGE, infoMessage);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);

		return map;
	}


    @RequestMapping(value = "/getTopMenus", method = RequestMethod.GET)
	@ResponseBody
	public Object getTopMenus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		HttpSession session = request.getSession();
		SecureUser sessionUser = (SecureUser) session.getAttribute(SESSION_USERPROFILE);
		
		//get side menus
		List<SecureMenu> topMenus = null;
		Map< String , Object > param = new HashMap< String , Object >();
		String groupName = request.getParameter("groupName");
		param.put("company", sessionUser.getCompany());
		param.put("groupName", groupName);
		topMenus = securityProfileService.retrieveTopMenus(sessionUser, param);
		map.put(PARAM_USER_TOP_MENU, topMenus);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}

	@RequestMapping(value = "/getSideMenus", method = RequestMethod.GET)
	@ResponseBody
	public Object getSideMenus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		HttpSession session = request.getSession();
		SecureUser sessionUser = (SecureUser) session.getAttribute(SESSION_USERPROFILE);
		
		//get side menus
		List<SecureMenu> firstLevelSideMenus = null;
		List<SecureMenu> secondLevelSideMenus = null;
		Map< String , Object > param = new HashMap< String , Object >();
		String level = request.getParameter("level");
		String sortNum = request.getParameter("sortNum");
		String parentMenuId = request.getParameter("parentMenuId");
		if("firstLevel".equals(level)){
			param.put("parentMenuId", parentMenuId);
			firstLevelSideMenus = securityProfileService.retrieveFirstLevelSideMenus(sessionUser, param);
		}
		else if("secondLevel".equals(level)){
			param.put("parentMenuId", parentMenuId);
			param.put("sortNum", sortNum);
			secondLevelSideMenus = securityProfileService.retrieveSecondLevelSideMenus(sessionUser, param);
		}
		else{
			param.put("parentMenuId", parentMenuId);
			firstLevelSideMenus = securityProfileService.retrieveFirstLevelSideMenus(sessionUser, param);
			
			//param.put("sortNum", sortNum);
			//secondLevelSideMenus = securityProfileService.retrieveSecondLevelSideMenus(sessionUser, param);
			param.clear();
			param.put("parentMenuId", parentMenuId);
			param.put("type", "L");
			secondLevelSideMenus = securityProfileService.retrieveSideMenus(sessionUser, param);
		}
		
		// user side menus
		map.put(PARAM_USER_FIRST_LEVEL_SIDE_MENU, firstLevelSideMenus);
		map.put(PARAM_USER_SECOND_LEVEL_SIDE_MENU, secondLevelSideMenus);
		//map.put(ContextConstants.PARAM_USER_SIDE_MENUS, sideMenus);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}
	
	/*@RequestMapping(value = "/updateUserRowHeightCode", method = RequestMethod.GET)
	@ResponseBody
	public Object updateUserRowHeightCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String userId = request.getParameter("userId");
		String rowHeightCode = request.getParameter("rowHeightCode");
		
		
		String rowHeight = authenticationService.updateUserRowHeightCode(userId, rowHeightCode);

		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		map.put(PARAM_RECORDS,rowHeight);
		return map;
	}*/
}
