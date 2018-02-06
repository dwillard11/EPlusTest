package ca.manitoulin.mtd.dto.security;

import ca.manitoulin.mtd.code.StatusCode;
import ca.manitoulin.mtd.constant.ApplicationConstants;
import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.AbstractDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.google.common.collect.Maps.newHashMap;

/**
 * User profile used in security model
 * @author Bob Yu
 *
 */
public class SecureUser extends AbstractDTO {
	private static final long serialVersionUID = 8844317015635790358L;
	//Mapped fields
	private String id;  //id
	private String firstname;
	private String lastname;
	private String password;
	private String account;
	private String accountDescription;
    private String defaultCurrency;
    private String uid; //eu_uid
	private String status; //eu_status
	private Integer depId; // eu_dp_id;
	private String email;
	private String mobile;
	private String company; //Eu_comp
	private String customer;  //Eu_cust
	private String type; //EMPLOYEE/PARTNER/CUSTOMER...
	private String group;
	private String referLanguage;
	private Date expireDate;
	private Integer retryCount;
	private Date lastLogin;
	private String lastIP;
	private String lastHost;
	private String superAdminIndCode;
	//Business fields
	private Locale locale;
	private String dbSchema;
	private String systemId;
	private StatusCode active;
	private List<SecureUserGroup> groups;
	private List<Account> globalAccount;
	private String domain;
	// add by GreyZeng for the table row setting
	private String rowHeightCode;
	// Business fields not in use
	private List<String> privileges;
	private List<RoleAccessRight> permission;
	private List<Role> roles;
	private List<Organization> departments;
	private Organization currentDepartment;
    private String currentDepCode;

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	private String signature;
    
    
    public void setReferLanguage(String language){
    	
    	language = StringUtils.lowerCase(language);
    	
    	Map<String, Locale> langMap = new HashMap<String, Locale>();
		langMap.put("english", ContextConstants.LOCALE_DEFAULT);
		langMap.put("french", ContextConstants.LOCALE_CANADA_FRENCH);
		langMap.put("chinese", ContextConstants.LOCALE_CHINESE);
		langMap.put("german", ContextConstants.LOCALE_GERMAN);
		langMap.put("spanish", ContextConstants.LOCALE_SPANISH);
		
		// set user default locale
		Locale userLocale = langMap.get(language);
		if(userLocale == null)
			userLocale = ContextConstants.LOCALE_DEFAULT;
		
		this.locale = userLocale;
		this.referLanguage = language;
    }
    

    
    public void setLocale(Locale locale) {
		
		Map<Locale, String> langMap = new HashMap<Locale, String>();
		langMap.put(ContextConstants.LOCALE_DEFAULT,"english");
		langMap.put(ContextConstants.LOCALE_CANADA_FRENCH,"french");
		langMap.put(ContextConstants.LOCALE_CHINESE,"chinese");
		langMap.put( ContextConstants.LOCALE_GERMAN,"german");
		langMap.put( ContextConstants.LOCALE_SPANISH,"spanish");
		
		// set user default language
		String lang = langMap.get(locale);
		if(lang == null)
			lang = "english";
		
		this.locale = locale;
		this.referLanguage = lang;
	}
    
    public boolean isInDep(Integer departmentId){
    	for(Organization org : departments){
    		if(org.getId() == departmentId){
    			return true;
    		}
    	}
    	return false;
    }


    public String getCurrentDepCode() {
        return currentDepCode;
    }

    public void setCurrentDepCode(String currentDepCode) {
        this.currentDepCode = currentDepCode;
    }




	public String getFullName(){
		return new StringBuilder().append(this.firstname).append(" ").append(this.lastname).toString();
	}
	/**
	 * Current user is an admin?
	 * @return
	 */
	public boolean isAdmin(){
		
		boolean rtn = false;
		if(roles == null) return rtn;
		for(Role role : roles){
			if(ContextConstants.ROLE_CATEGORY_ADMIN.equals(role.getCategory())){
				rtn = true;
				break;
			}
		}
		return rtn;
	}
	
	/**
	 * Current user is a finance user?
	 * @return
	 */
	public boolean isFinance(){
		
		boolean rtn = false;
		if(roles == null) return rtn;
		for(Role role : roles){
			if(ContextConstants.ROLE_CATEGORY_FINANCE.equals(role.getCategory())){
				rtn = true;
				break;
			}
		}
		return rtn;
	}
	
	
	/**
	 * Returns menu group the current user permitted to access
	 * @return
	 */
	public Set<String> getPermittedMenuGroup(){
		if(permission == null) return null;
		HashSet<String> set = new HashSet<String>();
		for(RoleAccessRight menu : permission){
			set.add(menu.getGroupName());
		}
		return set;
	}
	
	/**
	 * return menu name the current user permitted to access
	 * @return
	 */
	public Set<String> getPermittedMenu(){
		if(permission == null) return null;
		HashSet<String> set = new HashSet<String>();
		for(RoleAccessRight menu : permission){
			set.add(menu.getMenuName());
		}
		return set;
	}

	public Map<Integer, String> getDeptMap() {
		Map<Integer, String> map = newHashMap();
		if (CollectionUtils.isEmpty(departments)) return map;

		for (Organization organization : departments) {
			map.put(organization.getId(), organization.getName());
		}
		return map;
	}
	public Integer getDepId() {
		return depId;
	}

	public void setDepId(Integer depId) {
		this.depId = depId;
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public String getRowHeight() {
		return ApplicationConstants.getConfig(this.rowHeightCode);
	}
	public String getRowHeightCode() {
		return this.rowHeightCode;
	}
	public void setRowHeightCode(String rowHeightCode) {
		this.rowHeightCode = rowHeightCode;
	}
	
	public void setActiveCode(String active){
		this.active = StatusCode.valueByCode(active);
	}

	
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<SecureUserGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<SecureUserGroup> groups) {
		this.groups = groups;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public StatusCode getActive() {
		return active;
	}

	public void setActive(StatusCode active) {
		this.active = active;
	}


	public List<String> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<String> privileges) {
		this.privileges = privileges;
	}

	

	public String getFirstname() {
		return firstname;
	}


	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	public String getLastname() {
		return lastname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	public Date getExpireDate() {
		return expireDate;
	}


	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}


	public Date getLastLogin() {
		return lastLogin;
	}


	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}


	public String getLastIP() {
		return lastIP;
	}


	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}


	public String getLastHost() {
		return lastHost;
	}


	public void setLastHost(String lastHost) {
		this.lastHost = lastHost;
	}
	
	
	public String getCompany() {
		return company;
	}

	
	public void setCompany(String company) {
		this.company = company;
	}
	
	
	public String getGroup() {
		return group;
	}

	
	public void setGroup(String group) {
		this.group = group;
	}
	
	
	public String getSuperAdminIndCode() {
		return superAdminIndCode;
	}
	public void setSuperAdminIndCode(String superAdminIndCode) {
		this.superAdminIndCode = superAdminIndCode;
	}

	
	


	public Locale getLocale() {
		return locale;
	}


	


	public String getReferLanguage() {
		return referLanguage;
	}




	public Integer getRetryCount() {
		return retryCount;
	}


	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}


	public String getDbSchema() {
		return dbSchema;
	}


	public void setDbSchema(String dbSchema) {
		this.dbSchema = dbSchema;
	}


	public String getAccount() {
		return account;
	}


	public void setAccount(String account) {
		this.account = account;
	}


	public String getAccountDescription() {
		return accountDescription;
	}


	public void setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Account> getGlobalAccount() {
		return globalAccount;
	}
	public void setGlobalAccount(List<Account> globalAccount) {
		this.globalAccount = globalAccount;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

	public List<RoleAccessRight> getPermission() {
		return permission;
	}

	public void setPermission(List<RoleAccessRight> permission) {
		this.permission = permission;
	}
	public List<Organization> getDepartments() {
		return departments;
	}
	public void setDepartments(List<Organization> departments) {
		this.departments = departments;
	}
	public Organization getCurrentDepartment() {
		return currentDepartment;
	}
	public void setCurrentDepartment(Organization currentDepartment) {
		this.currentDepartment = currentDepartment;
	}

	


}
