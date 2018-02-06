package ca.manitoulin.mtd.dto.security;

import ca.manitoulin.mtd.dto.AbstractDTO;

public class RoleAccessRight extends AbstractDTO {
	private Integer id;
	private Integer roleId;
	private Integer accessRightId;
	private Integer functionId;
	private String groupName;
	private String menuName;
	private String url;
	private Integer sortNum;
	private Boolean hasChecked;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getAccessRightId() {
		return accessRightId;
	}

	public void setAccessRightId(Integer accessRightId) {
		this.accessRightId = accessRightId;
	}

	public Integer getFunctionId() {
		return functionId;
	}

	public void setFunctionId(Integer functionId) {
		this.functionId = functionId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public Boolean getHasChecked() {
		return hasChecked;
	}

	public void setHasChecked(Boolean hasChecked) {
		this.hasChecked = hasChecked;
	}

}
