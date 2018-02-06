package ca.manitoulin.mtd.intercepter.sql;

import org.junit.Test;
import org.springframework.util.CollectionUtils;

import ca.manitoulin.mtd.code.SortingOrder;
import junit.framework.Assert;

public class MysqlHelperTest {

	@Test
	public void testCombineSortingFragment() {
		String expected = " ORDER BY name ASC,description DESC,nothing ASC";
		String generated = new MysqlHelper().combineSortingFragment(
				CollectionUtils.arrayToList(new String[]{"name","description","nothing"}),
				CollectionUtils.arrayToList(new SortingOrder[]{SortingOrder.ASC, SortingOrder.DESC}));
		
		Assert.assertEquals(expected, generated);
	}
	
	@Test
	public void testGetPageSql() {
		String original = "select * from table";
		String expected = "select * from table ORDER BY name ASC,description DESC,nothing ASC limit 0, 5";
		String generated = new MysqlHelper().getPageSql(original, 1, 5,
				CollectionUtils.arrayToList(new String[]{"name","description","nothing"}), 
						CollectionUtils.arrayToList(new SortingOrder[]{SortingOrder.ASC, SortingOrder.DESC}));
		
		Assert.assertEquals(expected, generated);
	}

}
