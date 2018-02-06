package ca.manitoulin.mtd.dao.security;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import ca.manitoulin.mtd.dto.security.SecureUser;

/**
 * DAO class point to PMTUSR
 * @author Bob Yu
 *
 */
public interface SecureUserMapper {

	/**
	 * search user with user id
	 * @param userId
	 * @return
	 */
	SecureUser selectUserById(String userId);
	
	/**
	 * retrieve user profile with given id and password.
	 * @param userId
	 * @param password - encrypted password
	 * @return
	 */
	SecureUser selectUserByIdAndPassword(@Param("userId") String userId, 
			@Param("password")String password);
	
	/**
	 * update user login info with given id
	 * @param secureUser
	 * @return
	 */
	void updateUserLastLoginInfo(SecureUser secureUser);
	
	/**
	 * update user info with given id
	 * @param secureUser
	 * @return
	 */
	void updateUserInfo(SecureUser secureUser);

	void updateUserRowHeightCode(@Param("id") String userId,  @Param("rowHeightCode")String rowHeightCode);
	
	void updateUserExpiryDate(@Param("userId") String userId, 
			@Param("expiryDate") Date expiryDate);
	void updateUserAttemptPasswordCount(@Param("userId") String userId, 
			@Param("count") Integer count);
}