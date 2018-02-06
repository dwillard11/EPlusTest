package ca.manitoulin.mtd.dao.security;

import java.util.List;
import java.util.Map;

import ca.manitoulin.mtd.dto.Page;
import ca.manitoulin.mtd.dto.security.SecureMenu;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.security.SecureUserGroup;

/**
 * User group CRUD operation
 * 
 * @author Bob Yu
 *
 */
public interface SecureUserGroupMapper {

	/**
	 * search groups page by page
	 * 
	 * @param page
	 *            In case parameter "organizationCode" specified with
	 *            page.setParameter(), it will retrieve groups belong to the
	 *            organization. otherwise, retrieve all.
	 * @return
	 */
	List<SecureUserGroup> selectGroups(Page<SecureUserGroup> page);
	
	/**
	 * search Retrieve all groups of the login user.
	 * @return
	 */
	List<SecureUserGroup> selectLoginUserGroups(SecureUser loginUser);

	/**
	 * search Retrieve one group by id
	 * @return
	 */
	List<SecureUserGroup> selectUserGroupById(String groupId);

}