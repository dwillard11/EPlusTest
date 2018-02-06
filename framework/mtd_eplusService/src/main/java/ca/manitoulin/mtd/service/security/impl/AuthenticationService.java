package ca.manitoulin.mtd.service.security.impl;


import ca.manitoulin.mtd.dao.maintenance.BusinessCodeMapper;
import ca.manitoulin.mtd.dao.maintenance.RoleMapper;
import ca.manitoulin.mtd.dao.security.AccountMapper;
import ca.manitoulin.mtd.dao.security.OrganizationMapper;
import ca.manitoulin.mtd.dao.security.SecureUserMapper;
import ca.manitoulin.mtd.dto.security.Organization;
import ca.manitoulin.mtd.dto.security.Role;
import ca.manitoulin.mtd.dto.security.RoleAccessRight;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.support.AppEnum;
import ca.manitoulin.mtd.exception.security.AuthenticationException;
import ca.manitoulin.mtd.service.operationconsole.ITripService;
import ca.manitoulin.mtd.service.security.IAuthenticationService;
import ca.manitoulin.mtd.util.EncryptUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static ca.manitoulin.mtd.ad.filter.ActivityDirectoryFilter.authenticate;
import static ca.manitoulin.mtd.constant.ApplicationConstants.getConfig;
import static java.lang.Boolean.parseBoolean;

@Service
public class AuthenticationService implements IAuthenticationService {

	private static final Logger log = Logger.getLogger(IAuthenticationService.class);
	@Autowired
	private SecureUserMapper secureUserMapper;
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private OrganizationMapper orgMapper;
	@Autowired
	private ITripService tripService;
	@Autowired
	private BusinessCodeMapper businessCodeMapper;
	
	@Override	
	public SecureUser signIn(String userId, String password, String ipAddress, String host) throws AuthenticationException{
		
		final String DEFAULT_LANG = "french";
		
		return signIn(userId, password, ipAddress, host, DEFAULT_LANG);
	}
	@Override	
	public SecureUser signIn(String userId, String password, String ipAddress, String host, String lang) throws AuthenticationException{
		//1.validate user profile exists in PMTUSR table
		SecureUser mtUser = getMtUserInfo(userId, password, ipAddress, host);
		if (mtUser == null) {
			log.error("User not found in PMTUSR: " + userId);
			throw new AuthenticationException("User account not exist!");
		}

		boolean adValidationEnabled = parseBoolean(getConfig("security.LDAP.enabled"));
		String passwordHashType = getConfig("security.pwd.hash");

		String domainStr;
		domainStr = mtUser.getDomain();
		if (adValidationEnabled) {
			domainStr = getConfig("security.LDAP.domain");
		}
        //2.if domain is not blank, use KRB5 token to validate credentials
        if(!StringUtils.isBlank(domainStr) && adValidationEnabled){
        	boolean isValidAdUser = false;
        	for(String domain : StringUtils.split(domainStr, ";")){
        		isValidAdUser = authenticate(userId, password, domain.toUpperCase());
        		if(isValidAdUser)
        			break;
        	}
          	if(isValidAdUser == false){
        		log.error("User validation failed in Domain: " + userId + " @ " + domainStr.toUpperCase());
        		throw new AuthenticationException("Invalid domain password!"); 
        	}
        }else{
        	String encryptedPassword = null;
        	if("SHA1".equals(passwordHashType))
        		encryptedPassword = EncryptUtil.encryptSHA1(password);
        	else if("MD5".equals(passwordHashType))
        		encryptedPassword = EncryptUtil.encode2MD5hex(password);
        	else
        		throw new AuthenticationException("Unsupport Hash Type: " + passwordHashType); 
        	
        	if(encryptedPassword.equals(mtUser.getPassword()) == false){
        		log.error("User validation failed with PMTUSR password: " + userId + " @ " + password);
    			throw new AuthenticationException("Invalid password!"); 
        	}
        }
            	
        SecureUser loginUser = secureUserMapper.selectUserByUID(userId);
		if(loginUser == null){
			//throw new AuthenticationException("error.sec.userNotExist", arrayToList(new String[]{userId}), null);
			throw new AuthenticationException("User Account does not exist!");
        }
        replicateUserInfo(mtUser, loginUser);		

		this.assembleUserProfile(loginUser);
		
		//Set user language
		loginUser.setReferLanguage(lang);

		//release all currency lock
		tripService.releaseAllConcurrencyLock(userId);
		try {
			List<AppEnum> list = businessCodeMapper.getEpCodeListByType("Email Signature",lang);
			if (null != list && list.size() > 0) {
			    AppEnum appEnum = list.get(0);
			    System.out.println(appEnum.getLabel());
				loginUser.setSignature(appEnum.getLabel());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loginUser;
	}

    private void replicateUserInfo(SecureUser mtUser, SecureUser loginUser) {
		loginUser.setFirstname(mtUser.getFirstname());
		loginUser.setLastname(mtUser.getLastname());
		
	}

	private SecureUser getMtUserInfo(String userId, String password, String ipAddress, String host) {
    	//TODO use webservice to validate user info instead of directly read PMTUSER
        return secureUserMapper.selectPMTUserById(userId);
    }

    private void assembleUserProfile(SecureUser loginUser) {
    	
    	loginUser.setCurrentCompany(loginUser.getCompany());
    	loginUser.setCurrentCustomer(loginUser.getCustomer());
    	
        //set department
		//Organization dep = orgMapper.selectDepartmentById(loginUser.getDepId());
		//loginUser.setOrganization(dep);
    	//Update on 30-Mar-2017, user-departments update to 1-N
    	List<Organization> deps = orgMapper.selectDepartmentsByUser(Integer.parseInt(loginUser.getId()));
    	
    	//Added on 2017-4-26
    	/*if(StringUtils.isEmpty(loginUser.getDefaultCurrency())){
    		String departmentDefaultCurrency = null;
	    	for(Organization dep : deps){
	    		if(StringUtils.isEmpty(dep.getDefaultCurrency()) == false){
	    			log.info("USER DEFAULT CURRENCY IS EMPTY, USE DEPARTMENT DEFAULT CURRENCY. USERID=" + loginUser.getUid());	        		
	    			departmentDefaultCurrency = dep.getDefaultCurrency();
	    			break;
	    		}
	    	}
	    	loginUser.setDefaultCurrency(departmentDefaultCurrency);
    	}*/
    	
    	loginUser.setDepartments(deps);
    	loginUser.setDepId(deps.get(0).getId());
    	
		//set user access rights
		List<RoleAccessRight> accessRights = roleMapper.selectRoleAccessRightsByUserId(loginUser.getId());
		loginUser.setPermission(accessRights);
		
		//set user roles
		List<Role> roles = roleMapper.selectRolesByUserID(Integer.parseInt(loginUser.getId()));
		loginUser.setRoles(roles);
		
		
		
	}

	@Override
	public SecureUser switchLanguage(String userId, String language)
			throws Exception {
		SecureUser secureUser = new SecureUser();
		secureUser.setId(userId);
		secureUser.setReferLanguage(language);
		// secureUserMapper.updateUserInfo(secureUser);
		return secureUser;
	}

	/*public String updateUserRowHeightCode(String userId, String rowHeightCode) {
		
		secureUserMapper.updateUserRowHeightCode(userId,rowHeightCode);
		return  ApplicationConstants.getConfig(rowHeightCode);
	}*/

}
