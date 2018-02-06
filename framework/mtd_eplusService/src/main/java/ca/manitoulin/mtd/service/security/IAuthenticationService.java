package ca.manitoulin.mtd.service.security;

import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.security.AuthenticationException;

/**
 * Interface provides API of accessing authentication
 * @author Bob Yu
 *
 */
public interface IAuthenticationService {
	
	/**
	 * Sign in with userid and password user inputed
	 * @param userId
	 * @param password un-encrypted password
	 * @param ipAddress IP address
	 * @return
	 */
	SecureUser signIn(String userId, String password, String ipAddress, String host) throws Exception;
	
	SecureUser signIn(String userId, String password, String ipAddress, String host, String lang) throws AuthenticationException;
	
	/**
	 * Sign in with userid and password user inputed
	 * @param userId
	 * @param password un-encrypted password
	 * @param ipAddress IP address
	 * @return
	 */
	SecureUser switchLanguage(String userId, String language) throws Exception;
	
	//String updateUserRowHeightCode(String userId, String rowHeightCode) throws Exception;
	

}
