package ca.manitoulin.mtd.dao.operationconsole;

import static org.apache.log4j.Logger.getLogger;

import java.math.BigDecimal;
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

import ca.manitoulin.mtd.dto.operationconsole.Freight;
import ca.manitoulin.mtd.util.ResultContextSimulator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-db.xml" })
public class FreightMapperTest extends ResultContextSimulator{
	static private Logger log = getLogger(FreightMapperTest.class);

	@Autowired
	private FreightMapper freightMapper;
	
	@Before
	public void genSession(){
		super.gengerateUserSession();
	}
	
	@Test
	@Rollback(true)
	public void testAll() {
		final int tripId = 1;
		freightMapper.deleteFreightByTripId(tripId);
		Freight f = new Freight();
		
		//TODO create a trip first
		
		f.setTripId(tripId);

		f.setEstimatedCost(BigDecimal.valueOf(1.0001));
		f.setEstimatedPieces(255);
		f.setEstimatedCurrency("RMB");
		freightMapper.insertFreight(f);
		
		Integer fId = f.getId();
		Assert.assertNotNull(fId);
		
		List<Freight> fList = freightMapper.selectFreightByTripId(tripId);
		Assert.assertTrue(1 == fList.size());
		Assert.assertTrue(255 == fList.get(0).getEstimatedPieces());
		
		f = freightMapper.selectFreightById(fId);
		Assert.assertNotNull(f.getCurrentCompany());
		
		f.setItem("Bolt");
		freightMapper.updateFreight(f);
		
		freightMapper.deleteFreight(fId);
		
		fList = freightMapper.selectFreightByTripId(tripId);
		Assert.assertTrue(fList.isEmpty());
	}

}
