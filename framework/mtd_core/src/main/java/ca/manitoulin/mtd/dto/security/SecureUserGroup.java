package ca.manitoulin.mtd.dto.security;

import java.util.List;

import ca.manitoulin.mtd.dto.AbstractDTO;

/**
 * Group usually contains a set of users which need the same privileges but not in the same post.
 * point to PMTGRPT
 * @author Bob Yu
 *
 */
//@Alias("SecureUserGroup")
public class SecureUserGroup extends AbstractDTO {

	private String id;
	private String name;
	private String organizationCode;
	private String description;
	
	//business fields
	private List<SecureUser> users;
	private Organization organization;
	

	public List<SecureUser> getUsers() {
		return users;
	}
	public void setUsers(List<SecureUser> users) {
		this.users = users;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrganizationCode() {
		return organizationCode;
	}
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	

}
