package ca.manitoulin.mtd.service.security.impl;

import ca.manitoulin.mtd.constant.ApplicationConstants;
import ca.manitoulin.mtd.dao.maintenance.RoleMapper;
import ca.manitoulin.mtd.dao.security.SecureMenuMapper;
import ca.manitoulin.mtd.dao.security.SecureUserGroupMapper;
import ca.manitoulin.mtd.dao.security.SecureUserMapper;
import ca.manitoulin.mtd.dto.Page;
import ca.manitoulin.mtd.dto.security.Role;
import ca.manitoulin.mtd.dto.security.SecureMenu;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.security.SecureUserGroup;
import ca.manitoulin.mtd.service.security.ISecurityProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SecurityProfileService implements ISecurityProfileService {
	@Autowired
	private SecureUserGroupMapper secureUserGroupMapper;

	@Autowired
	private SecureMenuMapper secureMenuMapper;
	
	@Autowired
	private SecureUserMapper secureUserMapper;
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Override
	public Page<SecureUserGroup> retrieveGroups(Page<SecureUserGroup> page) {
		List<SecureUserGroup> groupList = secureUserGroupMapper.selectGroups(page);
		page.setResults(groupList);
		return page;
	}
	
	@Override
	public List<SecureUserGroup> retrieveUserGroupById(String groupId) {
		List<SecureUserGroup> userGroup = secureUserGroupMapper.selectUserGroupById(groupId);
		return userGroup;
	}

	@Override
	public List<SecureUserGroup> retrieveLoginUserGroups(SecureUser user) {
		List<SecureUserGroup> groupList = secureUserGroupMapper.selectLoginUserGroups(user);
		return groupList;
	}

	@Override
	public List<SecureMenu> retrieveTopMenus(SecureUser user, Map<String, Object> parmas) {
		List<SecureMenu> topMenuList = secureMenuMapper.selectTopMenus(parmas);
		return topMenuList;
	}
	
	@Override
	public List<SecureMenu> retrieveSideMenus(SecureUser user, Map<String, Object> parmas) {
		List<SecureMenu> sideMenuList = secureMenuMapper.selectSideMenus(parmas);
		return sideMenuList;
	}
	

	@Override
	public List<SecureMenu> retrieveFirstLevelSideMenus(SecureUser user, Map<String, Object> parmas) {
		List<SecureMenu> topMenuList = secureMenuMapper.selectFirstLevelSideMenus(parmas);
		return topMenuList;
	}

	@Override
	public List<SecureMenu> retrieveSecondLevelSideMenus(SecureUser user, Map<String, Object> parmas) {
		List<SecureMenu> topMenuList = secureMenuMapper.selectSecondLevelSideMenus(parmas);
		return topMenuList;
	}

	@Override
	public void saveUserAction(String uId) {
		secureUserMapper.updateUserLastActionInfo(uId);
		
	}

	@Override
	public List<Role> retrieveNumberOfOnlineUsers(Integer departmentId) {
		final int TIME_DIFF_IN_SECONDS = Integer.parseInt(ApplicationConstants.getConfig("sys.user.expiredTime"));
		List<Role> roles = roleMapper.selectOnlineUserCount(departmentId, TIME_DIFF_IN_SECONDS);
		return roles;
	}

}
