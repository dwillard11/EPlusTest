package ca.manitoulin.mtd.dao.security;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.test.AbstractDatabaseOperationTest;


public class SecureUserMapperTest extends AbstractDatabaseOperationTest {
	static private Logger log = Logger.getLogger(SecureUserMapperTest.class);

	@Autowired
	private SecureUserMapper secureUserMapper;

	@Test
	//@Rollback(true) -- defined in its supper class
	public void testSelectUserById() {
		final String userid = "044225";
		SecureUser user = secureUserMapper.selectUserById(userid);
		Assert.assertNotNull(user);
	}
	
	
	@Test
	//@Rollback(true) -- defined in its supper class
	public void testupdateUserRowHeightCode() {
		final String userid = "anixter";
		final String rowHeight = "RC.Cozy";
		secureUserMapper.updateUserRowHeightCode(userid,rowHeight);
		
	}
}
