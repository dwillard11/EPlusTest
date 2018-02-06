package ca.manitoulin.mtd.dao.security;

import java.util.List;
import java.util.Map;

import ca.manitoulin.mtd.dto.security.SecureMenu;

public interface SecureMenuMapper {
	List<SecureMenu> selectTopMenus(Map<String, Object> parmas);
	List<SecureMenu> selectSideMenus(Map<String, Object> parmas);
	List<SecureMenu> selectFirstLevelSideMenus(Map<String, Object> parmas);
	List<SecureMenu> selectSecondLevelSideMenus(Map<String, Object> parmas);
}
