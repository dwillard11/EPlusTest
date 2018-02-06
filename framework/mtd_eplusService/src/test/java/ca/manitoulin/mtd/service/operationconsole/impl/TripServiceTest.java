package ca.manitoulin.mtd.service.operationconsole.impl;

import ca.manitoulin.mtd.service.operationconsole.ITripService;
import ca.manitoulin.mtd.util.ResultContextSimulator;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.apache.log4j.Logger.getLogger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-db.xml" })
public class TripServiceTest extends ResultContextSimulator{
	static private Logger log = getLogger(TripServiceTest.class);

	@Autowired
	private ITripService tripService;
	
	@Before
	public void genSession(){
		super.gengerateUserSession();
	}
	
	@Test
	@Rollback(true)
	public void testAddConditions() {
		
		ArrayList templates = new ArrayList();
		templates.add(1);
		templates.add(2);
		tripService.addConditions(1,templates);
		
		List rlt = tripService.retrieveQuoteTreeByTripID(1);
		Assert.assertEquals(2, rlt.size());
		
	}
	@Test
	public void testGetDocVersionByType() throws Exception {
		Assert.assertEquals(2,tripService.getDocVersionByType(105178,"Pickup"));
	}
	@Test
	public void testIsExistTripTemplate() throws Exception {
		Assert.assertEquals(1,tripService.isExistTripTemplate("PWC"));
		Assert.assertEquals(0,tripService.isExistTripTemplate("PWCC"));
	}

}
