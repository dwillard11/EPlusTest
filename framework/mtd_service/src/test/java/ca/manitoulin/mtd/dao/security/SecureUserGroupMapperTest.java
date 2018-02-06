package ca.manitoulin.mtd.dao.security;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import ca.manitoulin.mtd.code.SortingOrder;
import ca.manitoulin.mtd.dao.security.SecureUserGroupMapper;
import ca.manitoulin.mtd.dto.Page;
import ca.manitoulin.mtd.dto.security.SecureUserGroup;
import ca.manitoulin.mtd.test.AbstractDatabaseOperationTest;


public class SecureUserGroupMapperTest extends AbstractDatabaseOperationTest {
	static private Logger log = Logger.getLogger(SecureUserGroupMapperTest.class);

	@Autowired
	private SecureUserGroupMapper secureUserGroupMapper;

	@Test
	//@Rollback(true) -- defined in its supper class
	public void selectGroups() {
		final String company = "duke";
		Page<SecureUserGroup> page = new Page<SecureUserGroup>();
		page.setPageSize(10);
		List<SecureUserGroup> list = secureUserGroupMapper.selectGroups(page);
		
				
		Assert.assertNotNull(list);
		Assert.assertFalse(list.isEmpty());
		int totalPage = page.getTotalPage();
		
		page.setSortedBy(CollectionUtils.arrayToList(new String[]{"name", "description"}));
		page.setSortingOrder(CollectionUtils.arrayToList(new SortingOrder[]{SortingOrder.DESC, SortingOrder.ASC}));
		
		page.setParameter("organizationCode", company);
		
		list = secureUserGroupMapper.selectGroups(page);
		Assert.assertNotNull(list);
		Assert.assertFalse(list.isEmpty());
		int totalPageOfDuke = page.getTotalPage();
		Assert.assertTrue(totalPage > totalPageOfDuke);
		
	}
}
