package ca.manitoulin.mtd.intercepter.sql;

import org.junit.Test;
import org.springframework.util.CollectionUtils;

import ca.manitoulin.mtd.code.SortingOrder;
import junit.framework.Assert;

public class DB2HelperTest {


	
	@Test
	public void testGetPageSql() {
		String original = "select * from table";
		String expected = "SELECT RS_.* FROM (SELECT ROW_.*, rownumber() over( ORDER BY name ASC,description DESC,nothing ASC ) as ROWNUM_ FROM ("
				+ "select * from table) ROW_ ) RS_ WHERE ROWNUM_ > 0 AND ROWNUM_ <= 5";
		String generated = new DB2sqlHelper().getPageSql(original, 1, 5, 
				CollectionUtils.arrayToList(new String[]{"name","description","nothing"}),
				CollectionUtils.arrayToList(new SortingOrder[]{SortingOrder.ASC, SortingOrder.DESC}));
		//System.out.println(generated);
		Assert.assertEquals(expected, generated);
	}

}
