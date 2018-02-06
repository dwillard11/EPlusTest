package ca.manitoulin.mtd.dao.operationconsole;

import ca.manitoulin.mtd.dto.operationconsole.TripCost;
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.apache.log4j.Logger.getLogger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-db.xml" })
public class TripCostMapperTest extends ResultContextSimulator{
	static private Logger log = getLogger(TripCostMapperTest.class);

	@Autowired
	private TripCostMapper costMapper;
	
	@Before
	public void genSession(){
		super.gengerateUserSession();
	}
	
	@Test
	@Rollback(true)
	public void testAll() {
		
		costMapper.deleteTripCostsByTrip(1);
		
		TripCost c = new TripCost();
		
		c.setTripId(1);
		c.setActCost(new BigDecimal(1.00f));
		c.setActCurrency("USD");
		c.setActDate(new Date());
		c.setActUsedCost(new BigDecimal(99.00f));
		c.setActUsedRate(new BigDecimal(0.99f));
		c.setAmount(new BigDecimal(99));
		c.setChargeCode("ABC");
		c.setChargeDesc("desc");
		c.setDescription("description");
		
		costMapper.insertTripCost(c);
		
		List<TripCost> lst = costMapper.selectTripCosts(1, true);
		
		c.setChargeCode(null);
		c.setEstCurrency("RMB");
		costMapper.updateTripCost(c);
		
		TripCost u = costMapper.selectTripCostById(c.getId());
		
		Assert.assertEquals("RMB", u.getEstCurrency());
		
		costMapper.deleteTripCost(u.getId());
		
		
	}

    @Test
    public void testSelectTripCostsByEventId() {

        List<TripCost> all = costMapper.selectTripCostsByEventId(450, true);
        Assert.assertEquals(4, all.size());

        List<TripCost> visible = costMapper.selectTripCostsByEventId(450, false);
        Assert.assertEquals(3, visible.size());


    }

    @Test
    public void testDeleteTripCostsByEvent() {

        costMapper.deleteTripCostsByEvent(451);


    }
}
