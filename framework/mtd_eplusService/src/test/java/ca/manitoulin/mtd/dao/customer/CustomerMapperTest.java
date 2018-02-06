package ca.manitoulin.mtd.dao.customer;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import ca.manitoulin.mtd.dto.customer.Customer;
import ca.manitoulin.mtd.dto.customer.CustomerContact;
import ca.manitoulin.mtd.service.customer.ICustomerService;
import ca.manitoulin.mtd.util.ResultContextSimulator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-email.xml" })
public class CustomerMapperTest extends ResultContextSimulator{
	static private Logger log = getLogger(CustomerMapperTest.class);

	@Autowired
	private CustomerMapper customerMapper;
	
	@Autowired
	private ICustomerService service;
	
	@Before
	public void genSession(){
		super.gengerateUserSession();
	}
	
	@Test
	@Rollback(true)
	public void testAll() throws Exception {
		//List<CustomerContact> c = customerMapper.selectAllCustomerContact("Chris", null, null, true);
		
		
		Customer c1 = service.retrieveCustomerProfileById(11);
		junit.framework.Assert.assertNotSame("Custom Brokers", c1.getTypeDesc());
	}

}
