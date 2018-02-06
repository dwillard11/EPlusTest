package ca.manitoulin.mtd.dao.operationconsole;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.manitoulin.mtd.dto.operationconsole.Condition;
import ca.manitoulin.mtd.dto.operationconsole.Trip;
import ca.manitoulin.mtd.service.operationconsole.ITripService;
import ca.manitoulin.mtd.util.ResultContextSimulator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-db.xml" })
public class QuoteMapperTest extends ResultContextSimulator{
	static private Logger log = getLogger(QuoteMapperTest.class);

	@Autowired
	private ConditionMapper quoteMapper;
	@Autowired
	private ITripService tripService;
	
	@Before
	public void genSession(){
		super.gengerateUserSession();
	}
	
	@Test
	@Rollback(true)
	public void testAll() {
		Trip trip = tripService.touchQuote(0);
		
		Condition c = new Condition();
		c.setTripId(trip.getId());
		c.setType("Hand Carry Service");
		c.setCategory("New Trip");
		c.setName("Hand Carry Service");
		c.setSequence(1);
		
		quoteMapper.insertCondition(c);
				
		List<Condition> fList = quoteMapper.selectConditions(trip.getId());
		Assert.assertTrue(1 == fList.size());
		
		c.setItem("Booked");
		c.setDescription("Test Booked description");
		quoteMapper.updateCondition(c);
		
		quoteMapper.deleteConditionsByTrip(trip.getId());
	}
}
