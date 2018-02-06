package ca.manitoulin.mtd.dao.security;

import java.util.Date;
import java.util.List;

import ca.manitoulin.mtd.dto.security.Organization;
import org.apache.ibatis.annotations.Param;

import ca.manitoulin.mtd.dto.security.Role;
import ca.manitoulin.mtd.dto.security.SecureUser;

/**
 * DAO class point to PMTUSR
 * @author Bob Yu
 *
 */
public interface SecureUserMapper {

	/**
	 * search user with user id
	 * @param id
	 * @return
	 */
	SecureUser selectSecureUserByID(@Param("id") Integer id);
	
	/**
	 * retrieve user profile with given id and password.
	 * @param userId
	 * @param password - encrypted password
	 * @return
	 */
	SecureUser selectUserByIdAndPassword(@Param("userId") String userId, 
			@Param("password")String password);
	
	SecureUser selectPMTUserById(String userId);
	
	/**
	 * update user login info with given id
	 * @param uid
	 * @return
	 */
	void updateUserLastActionInfo(String uid);
	
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

	/**
	 * search all users
	 *
	 * @return
	 */
	List<SecureUser> selectSecureUsers(@Param("searchKey") String searchKey, @Param("language") String language);

	/**
	 * add a new user
	 * @param secureUser
	 */
	void insertSecureUser(SecureUser secureUser);

	void deleteSecureUser(@Param("id") Integer id);

	/**
	 * update a user
	 * @param secureUser
	 */
	void updateSecureUser(SecureUser secureUser);

	SecureUser selectUserByUID(@Param("value") String uid);

	SecureUser selectUserFromPMTUserByUID(@Param("uid") String uid);

    void insertUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId, @Param("updatedBy") String updatedBy, @Param("currentCompany") String currentCompany, @Param("currentCustomer") String currentCustomer);
	void deleteUserRole(@Param("userId") Integer userId);

	void insertUserDivision(@Param("userId") Integer userId, @Param("departmentId") Integer departmentId, @Param("updatedBy") String updatedBy, @Param("currentCompany") String currentCompany, @Param("currentCustomer") String currentCustomer);
	void deleteUserDivision(@Param("userId") Integer userId);
	List<Organization> selectDepartmentsByUserID(@Param("userId") Integer userId);
}