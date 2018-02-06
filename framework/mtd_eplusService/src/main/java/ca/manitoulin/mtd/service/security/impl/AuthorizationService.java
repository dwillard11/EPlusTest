package ca.manitoulin.mtd.service.security.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.manitoulin.mtd.dao.security.SecureMenuMapper;
import ca.manitoulin.mtd.dao.security.SecureUserGroupMapper;
import ca.manitoulin.mtd.dto.security.SecureMenu;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.security.SecureUserGroup;
import ca.manitoulin.mtd.service.security.IAuthorizationService;
import ca.manitoulin.mtd.util.ApplicationSession;

@Service
public class AuthorizationService implements IAuthorizationService {
	
	@Autowired
	private SecureUserGroupMapper userGroupMapper;
	@Autowired
	private SecureMenuMapper menuMapper;

	@Override
	public List<SecureUserGroup> retrieveGroups() {
		
		SecureUser currentUser = ApplicationSession.get();
		//retrieve groups of current user.
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SecureMenu> retrieveTopMenus(String groupId) {
		//TODO  will filter by current user
		SecureUser currentUser = ApplicationSession.get();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SecureMenu> retireveLeftMenus(String menuId) {
		
		SecureUser currentUser = ApplicationSession.get();
		// TODO Auto-generated method stub
		return null;
	}

}
