package ca.manitoulin.mtd.dto.security;

import ca.manitoulin.mtd.code.StatusCode;
import ca.manitoulin.mtd.dto.AbstractDTO;

import java.util.List;

/**
 * Role is an important POJO in a security model. In most case, it equals a post.
 * @author Bob Yu
 *
 */
public class Role extends AbstractDTO {

    public String accessRightIds;
    private int id;
	private String name;
	private String description;
	private boolean systemDefault;
	private String status; //er_status
	private StatusCode active;
	private String category;
	//business fields
	//private List<SecureUser> users;
	//private List<SecureUserGroup> groups;
	//private List<Privilege> privileges;
	private List<RoleAccessRight> roleAccessRights;
	private StatusCode isInherited;
	private Integer onlineUserCount;

    public Integer getOnlineUserCount() {
        return onlineUserCount;
    }

    public void setOnlineUserCount(Integer onlineUserCount) {
        this.onlineUserCount = onlineUserCount;
    }

	public void setIsInheritedCode(String code){
		this.isInherited = StatusCode.valueByCode(code);
	}
	
	public void setActiveCode(String active){
		this.active = StatusCode.valueByCode(active);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getSystemDefault() {
		return systemDefault;
	}
	public void setSystemDefault(boolean systemDefault) {
		this.systemDefault = systemDefault;
	}

	public String getStatus() {
		return this.status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public StatusCode getActive() {
		return active;
	}
	public void setActive(StatusCode active) {
		this.active = active;
	}

	public String getAccessRightIds() {
		return accessRightIds;
	}

	public void setAccessRightIds(String accessRightIds) {
		this.accessRightIds = accessRightIds;
	}

	/*
	public List<SecureUser> getUsers() {
		return users;
	}
	public void setUsers(List<SecureUser> users) {
		this.users = users;
	}

	public List<SecureUserGroup> getGroups() {
		return groups;
	}
	public void setGroups(List<SecureUserGroup> groups) {
		this.groups = groups;
	}

	public StatusCode getIsInherited() {
		return isInherited;
	}
	public void setIsInherited(StatusCode isInherited) {
		this.isInherited = isInherited;
	}

	public List<Privilege> getPrivileges() {
		return privileges;
	}
	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}
	*/

	public List<RoleAccessRight> getRoleAccessRights() {
		return roleAccessRights;
	}

	public void setRoleAccessRights(List<RoleAccessRight> roleAccessRights) {
		this.roleAccessRights = roleAccessRights;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
