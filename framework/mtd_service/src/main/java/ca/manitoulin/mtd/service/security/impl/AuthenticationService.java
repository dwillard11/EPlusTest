package ca.manitoulin.mtd.service.security.impl;


import static ca.manitoulin.mtd.ad.filter.ActivityDirectoryFilter.authenticate;
import static ca.manitoulin.mtd.constant.ApplicationConstants.getConfig;
import static java.lang.Boolean.parseBoolean;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ca.manitoulin.mtd.code.SystemCode;
import ca.manitoulin.mtd.constant.ApplicationConstants;
import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dao.security.AccountMapper;
import ca.manitoulin.mtd.dao.security.SecureUserMapper;
import ca.manitoulin.mtd.dto.security.Account;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.security.AuthenticationException;
import ca.manitoulin.mtd.service.security.IAuthenticationService;
import ca.manitoulin.mtd.util.EncryptUtil;

@Service
public class AuthenticationService implements IAuthenticationService {

	private static final Logger log = Logger.getLogger(IAuthenticationService.class);
	@Autowired
	private SecureUserMapper secureUserMapper;
	@Autowired
	private AccountMapper accountMapper;
	@Override
	public SecureUser signIn(String userId, String password, String ipAddress, String host) throws AuthenticationException{
		
		//retrieve user profile
		SecureUser loginUser = secureUserMapper.selectUserById(userId);
		
		//if loginUser is not exist, throw UserLoginException
		if(loginUser == null){
			throw new AuthenticationException("error.login.accountNotExists", CollectionUtils.arrayToList(new String[] {userId}), null);
		}
				
		//save login Id, and login time
		loginUser.setLastHost(host);
		loginUser.setLastIP(ipAddress);
		loginUser.setLastLogin(new Date());
		secureUserMapper.updateUserLastLoginInfo(loginUser);
				
		//if the expire date is before current time, which means user account is locked, then throw UserAccountLockedException
		if(loginUser.getExpireDate() != null && loginUser.getExpireDate().before(new Date())){
			log.info("User is expired at " + loginUser.getExpireDate());
			throw new AuthenticationException("error.login.accountExpired", CollectionUtils.arrayToList(new String[] {userId}), null);
			
		}		
		
		final boolean adValidationEnabled = parseBoolean(getConfig("security.LDAP.enabled"));
		final int MAX_PASSWORD_ATTEMPT = Integer.parseInt(getConfig("security.maxPasswordAttemptAllowed"));
		boolean authenticated = false;
		
		if(adValidationEnabled && StringUtils.trimToNull(loginUser.getDomain()) != null){
			log.info("User login with AD validation: " + userId);
			String domain = StringUtils.trim(loginUser.getDomain());
			//AD authentication
			authenticated = authenticate(userId, password, loginUser.getDomain());

		}else{
			//Validation password
			
			//encrypt password in SHA1
			password = EncryptUtil.encryptSHA1(password);			
			authenticated = password.equals(loginUser.getPassword());
		}
		
		//Prevent guess password
		if(authenticated == false){
			int failedTimes = (loginUser.getRetryCount() == null ? 0 : loginUser.getRetryCount() ) ;
			//if continuously  attempt to input wrong password in 5 times. Break.
			if(failedTimes >= MAX_PASSWORD_ATTEMPT){
				
				// Reset count and set to expired
				secureUserMapper.updateUserAttemptPasswordCount(userId, 0);
				secureUserMapper.updateUserExpiryDate(userId, new Date());
				log.info("User locked, too many times input wrong password: " + userId);
				throw new AuthenticationException("error.login.accountLocked", CollectionUtils.arrayToList(new String[] {userId}), null);
				
			}
			else{
				// break
				secureUserMapper.updateUserAttemptPasswordCount(userId, failedTimes++);
				throw new AuthenticationException("error.login.wrongPassword", CollectionUtils.arrayToList(new String[] {userId}), null);
			}
		}else{
			// Reset count 
			secureUserMapper.updateUserAttemptPasswordCount(userId, 0);
			
			//Retrieve other information
			assembleUserProfile(loginUser);
		}
		return loginUser;
	}
		
	private void assembleUserProfile(SecureUser loginUser) {
		// TODO choose user's chema, so far we hard code it.
		// Development schema, for safe update
		// loginUser.setDbSchema("LTL400TST3");
		// Production schema, be careful
		loginUser.setDbSchema(ContextConstants.SCHEMA_PRD);

		// Retrieve account info
		List<Account> userAccounts = accountMapper.selectAccountsByNumberAndCompany(loginUser.getAccount(),loginUser.getCompany());
		if (userAccounts != null && !userAccounts.isEmpty()) {
			Account a = userAccounts.get(0);
			loginUser.setAccountDescription(a.getDescription());
		} else {
			log.info("## NO ACCOUNT INFO FOUND FOR USER: " + loginUser.getId());
		}
		
		// Retrieve global accounts
		List<Account> globalAccounts = accountMapper.selectGlobalAccount(loginUser.getCompany(), loginUser.getCustomer(), loginUser.getId());
		loginUser.setGlobalAccount(globalAccounts);
		
		// set system code
		String companyCode = loginUser.getCompany();
		SystemCode systemCode = SystemCode.valueByCompanyCode(companyCode);
		log.info("## SET SYSTEM ID TO [" + systemCode + "] ACCORDING TO COMPANY [" + companyCode + "]");
		loginUser.setSystemId(systemCode.toString());
	}

	@Override
	public SecureUser switchLanguage(String userId, String language)
			throws Exception {
		SecureUser secureUser = new SecureUser();
		secureUser.setId(userId);
		secureUser.setReferLanguage(language);
		secureUserMapper.updateUserInfo(secureUser);
		return secureUser;
	}

	public String updateUserRowHeightCode(String userId, String rowHeightCode) {
		
		secureUserMapper.updateUserRowHeightCode(userId,rowHeightCode);
		return  ApplicationConstants.getConfig(rowHeightCode);
	}

}
