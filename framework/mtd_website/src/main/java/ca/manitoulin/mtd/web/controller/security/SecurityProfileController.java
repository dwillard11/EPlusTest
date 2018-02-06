package ca.manitoulin.mtd.web.controller.security;

import static ca.manitoulin.mtd.constant.ContextConstants.KEY_MESSAGE;
import static ca.manitoulin.mtd.constant.ContextConstants.KEY_RESULT;
import static ca.manitoulin.mtd.constant.ContextConstants.KEY_SHARE_DESTINATION;
import static ca.manitoulin.mtd.constant.ContextConstants.LOCALE_CANADA_FRENCH;
import static ca.manitoulin.mtd.constant.ContextConstants.LOCALE_DEFAULT;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_RECORDS;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_USER;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_USERNAME;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_USER_FIRST_LEVEL_SIDE_MENU;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_USER_GROUS;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_USER_SECOND_LEVEL_SIDE_MENU;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_USER_SELECTED_GROUS;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_USER_TOP_MENU;
import static ca.manitoulin.mtd.constant.ContextConstants.RESPONSE_SHARE_SUCCESS;
import static ca.manitoulin.mtd.constant.ContextConstants.RESPONSE_SUCCESS;
import static ca.manitoulin.mtd.constant.ContextConstants.SESSION_USERPROFILE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.manitoulin.mtd.dto.security.SecureMenu;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.security.SecureUserGroup;
import ca.manitoulin.mtd.service.misc.impl.LocalizedMessageService;
import ca.manitoulin.mtd.service.security.impl.AuthenticationService;
import ca.manitoulin.mtd.service.security.impl.SecurityProfileService;
import ca.manitoulin.mtd.service.share.IShareURLService;

@Controller
public class SecurityProfileController {


	@Autowired
	private AuthenticationService authenticationService;

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
		Map<String, String> map = new HashMap<String, String>();
		String userId = request.getParameter("userId");
		String password = request.getParameter("password");
		String requestURL = request.getParameter("requestURL");
		
		String destinationAndParams = null;
		if (isNotBlank(requestURL) && requestURL.indexOf("isShare=Y") != -1) {
			// share page
			destinationAndParams = shareURLService.obtainURLDestinationAndParams(requestURL);
		}
		
		String ipAddress = request.getRemoteAddr();
		String host = request.getRemoteHost();

		final String LAN_FRENCH = "French";
		SecureUser secureUser = authenticationService.signIn(userId, password, ipAddress, host);
		HttpSession session = request.getSession();

		// set user language
		if (LAN_FRENCH.equalsIgnoreCase(secureUser.getReferLanguage())) {
			secureUser.setLocale(LOCALE_CANADA_FRENCH);
		} else {
			secureUser.setLocale(LOCALE_DEFAULT);
		}
		// Save user profile to session
		session.setAttribute(SESSION_USERPROFILE, secureUser);

		String infoMessage = messageService.mergeMessage("info.welcome", null, secureUser.getLocale());

		map.put(KEY_MESSAGE, infoMessage);
		if (isBlank(destinationAndParams)) {
			map.put(KEY_RESULT, RESPONSE_SUCCESS);
		} else {
			map.put(KEY_RESULT, RESPONSE_SHARE_SUCCESS);
			map.put(KEY_SHARE_DESTINATION, destinationAndParams);
		}
		return map;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		session.invalidate();
		response.sendRedirect(request.getContextPath() + "/login.html");
	}

	@RequestMapping(value = "/switchLanguage", method = RequestMethod.GET)
	@ResponseBody
	public Object switchLanguage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String language = request.getParameter("language");

		final String LAN_FRENCH = "French";
		// String userId = request.getParameter("userId");
		HttpSession session = request.getSession();
		SecureUser sessionUser = (SecureUser) session.getAttribute(SESSION_USERPROFILE);
		String userId = sessionUser.getId();

		SecureUser secureUser = authenticationService.switchLanguage(userId, language);
		// set user language
		if (LAN_FRENCH.equalsIgnoreCase(secureUser.getReferLanguage())) {
			sessionUser.setLocale(LOCALE_CANADA_FRENCH);
		} else {
			sessionUser.setLocale(LOCALE_DEFAULT);
		}
		// Save user profile to session
		sessionUser.setReferLanguage(secureUser.getReferLanguage());
		session.setAttribute(SESSION_USERPROFILE, sessionUser);

		List<String> args = new ArrayList<String>();
		args.add(userId);
		String infoMessage = messageService.mergeMessage("info.saved", args, secureUser.getLocale());

		map.put(KEY_MESSAGE, infoMessage);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);

		return map;
	}

	@RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
	@ResponseBody
	public Object getUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		// String userId = request.getParameter("userId");
		HttpSession session = request.getSession();
		SecureUser sessionUser = (SecureUser) session.getAttribute(SESSION_USERPROFILE);
		String userId = sessionUser.getId();
		List<String> args = new ArrayList<String>();
		args.add(userId);
		messageService.mergeMessage("info.saved", args, sessionUser.getLocale());

		List<SecureUserGroup> groups = securityProfileService.retrieveLoginUserGroups(sessionUser);
		List<SecureMenu> topMenus = null;
		SecureUserGroup selectedGroup = null;
		
		if(groups.size()!=0){
			for (SecureUserGroup group : groups) {
				if(group.getName().equals(sessionUser.getGroup())){
					selectedGroup = group;
					break;
				}
			}
			if(selectedGroup == null){
				selectedGroup = groups.get(0);
			}

			Map< String , Object > param = new HashMap< String , Object >();
			param.put("company", sessionUser.getCompany());
			param.put("groupName", selectedGroup.getName());
			topMenus = securityProfileService.retrieveTopMenus(sessionUser, param);
		}
		
		// user group and top menus
		map.put(PARAM_USER_GROUS, groups);
		map.put(PARAM_USER_SELECTED_GROUS, selectedGroup);
		map.put(PARAM_USER_TOP_MENU, topMenus);
		
		// username
		map.put(PARAM_USERNAME, sessionUser.getFirstname() + " " + sessionUser.getLastname());
		// add by Grey begin
		map.put(PARAM_USER, sessionUser);
		// add by Grey end
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
	
	@RequestMapping(value = "/updateUserRowHeightCode", method = RequestMethod.GET)
	@ResponseBody
	public Object updateUserRowHeightCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String userId = request.getParameter("userId");
		String rowHeightCode = request.getParameter("rowHeightCode");
		
		
		String rowHeight = authenticationService.updateUserRowHeightCode(userId, rowHeightCode);

		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		map.put(PARAM_RECORDS,rowHeight);
		return map;
	}
}
