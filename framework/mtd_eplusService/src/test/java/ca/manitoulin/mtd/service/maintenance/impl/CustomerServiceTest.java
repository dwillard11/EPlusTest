package ca.manitoulin.mtd.service.maintenance.impl;


import ca.manitoulin.mtd.dto.customer.CustomerLocation;
import ca.manitoulin.mtd.service.customer.ICustomerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.Assert.notEmpty;


/**
 * Created by zhuiz on 2017/2/19.
 * ICustomerService unit test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-ws.xml"})
public class CustomerServiceTest {


    @Autowired
    private ICustomerService customerService;

    @Test
    public void testRetrieveCustomerLocationByFuzzySearch() throws Exception {
        String keyword = EMPTY;
        String customerType = "SHIPPER";
        List<CustomerLocation> customerLocations = customerService.retrieveCustomerLocationByFuzzySearch(keyword, customerType);
        notEmpty(customerLocations);
    }


}