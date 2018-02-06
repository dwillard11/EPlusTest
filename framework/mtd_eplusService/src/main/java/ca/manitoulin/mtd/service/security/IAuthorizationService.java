package ca.manitoulin.mtd.service.security;

import java.util.List;

import ca.manitoulin.mtd.dto.security.SecureMenu;
import ca.manitoulin.mtd.dto.security.SecureUserGroup;

/**
 * Interface provides API of privilege authorization checking.
 * @author Bob Yu
 *
 */
public interface IAuthorizationService {
	
	List<SecureUserGroup> retrieveGroups();
	
	List<SecureMenu> retrieveTopMenus(String groupId);
	
	List<SecureMenu> retireveLeftMenus(String menuId);

}
