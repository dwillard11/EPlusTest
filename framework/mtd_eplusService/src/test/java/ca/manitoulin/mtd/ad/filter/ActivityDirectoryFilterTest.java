package ca.manitoulin.mtd.ad.filter;

import org.junit.Test;


public class ActivityDirectoryFilterTest {

	
	@Test
	public void testAuth() {
		System.out.println(ActivityDirectoryFilter.authenticate("mkargar", "Developement972","secure.manitoulintransport.com"));
	}
}
