package ca.manitoulin.mtd.service.security;

import java.util.List;
import java.util.Map;

import ca.manitoulin.mtd.dto.Page;
import ca.manitoulin.mtd.dto.security.SecureMenu;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.security.SecureUserGroup;

/**
 * Provide API to query or update User\Group\Company etc.
 * @author Bob Yu
 *
 */
public interface ISecurityProfileService {

	/**
	 * Retrieve page of groups
	 * @param page In case parameter "organizationCode" specified with
	 *            page.setParameter(), it will retrieve groups belong to the
	 *            organization. otherwise, retrieve all.
	 * @return
	 */
	Page<SecureUserGroup> retrieveGroups(Page<SecureUserGroup> page);
	
	/**
	 * Retrieve all groups of the login user.
	 * @return
	 */
	List<SecureUserGroup> retrieveLoginUserGroups(SecureUser user);

	/**
	 * Retrieve all groups of the login user.
	 * @return
	 */
	List<SecureUserGroup> retrieveUserGroupById(String groupId);

	/**
	 * Retrieve top menu on condition.
	 * @return
	 */
	List<SecureMenu> retrieveTopMenus(SecureUser user, Map<String, Object> parmas);

	/**
	 * Retrieve side menu on condition.
	 * @return
	 */
	List<SecureMenu> retrieveSideMenus(SecureUser user, Map<String, Object> parmas);

	/**
	 * Retrieve first level side menu on condition.
	 * @return
	 */
	List<SecureMenu> retrieveFirstLevelSideMenus(SecureUser user, Map<String, Object> parmas);

	/**
	 * Retrieve second level side menu on condition.
	 * @return
	 */
	List<SecureMenu> retrieveSecondLevelSideMenus(SecureUser user, Map<String, Object> parmas);

}
