package ca.manitoulin.mtd.tracing.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.manitoulin.mtd.code.SystemCode;
import ca.manitoulin.mtd.code.UserType;
import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.support.Attachment;
import ca.manitoulin.mtd.dto.tracing.Probill;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.tracing.ITracingWebService;
import ca.manitoulin.mtd.test.AbstractDatabaseOperationTest;
import ca.manitoulin.mtd.util.json.JsonHelper;
import junit.framework.Assert;

public class TracingByDateWebServiceTest extends AbstractDatabaseOperationTest  {
	
	private static final Logger log = Logger.getLogger(TracingByDateWebServiceTest.class);
	
	private RequestContext requestContext;
	
	@Autowired
	private ITracingWebService tracingWebService;
	
	@Before
	public void init(){
		requestContext = new RequestContext();
		requestContext.setServiceClass(ITracingWebService.class);
		Calendar c = Calendar.getInstance();
		
		c.set(2016, 1, 1);
		Date begin = c.getTime();
		c.add(Calendar.DATE, 30);
		Date end = c.getTime();
		requestContext.addParameter(ContextConstants.PARAM_DATE_BEGIN, begin);
		requestContext.addParameter(ContextConstants.PARAM_DATE_END, end);

		SecureUser user = new SecureUser();
		user.setDbSchema("LTL400V1A3");

		requestContext.setUserProfile(user);
	}
	
	
	@Test
	public void testRetrieveProbillsByPickupDate() throws BusinessException{
		
		SecureUser user = requestContext.getUserProfile();
		user.setAccount("0014246");
		user.setType(UserType.EMPLOYEE.toString());
		ResultContext result = (ResultContext) tracingWebService.retrieveProbillsByPickupDate(requestContext);
		List<Probill> probills = (List<Probill>) result.getResult(ContextConstants.PARAM_PROBILL);
        Assert.assertNotNull(probills); 	
        
        //Partner
        user.setAccount("TGBT");
		user.setType(UserType.PARTNER.toString());
		
		result = (ResultContext) tracingWebService.retrieveProbillsByPickupDate(requestContext);
		List<Probill> partnerProbills = (List<Probill>) result.getResult(ContextConstants.PARAM_PROBILL);
        Assert.assertNotNull(partnerProbills); 	
	
	}
	
	@Test
	public void testRetrieveProbillsByDeliveryDate() throws BusinessException{
		
		SecureUser user = requestContext.getUserProfile();
		user.setAccount("0014246");
		user.setType(UserType.EMPLOYEE.toString());
		ResultContext result = (ResultContext) tracingWebService.retrieveProbillsByDeliveryDate(requestContext);
		List<Probill> probills = (List<Probill>) result.getResult(ContextConstants.PARAM_PROBILL);
        Assert.assertNotNull(probills); 	
        
        //Partner
        user.setAccount("TGBT");
		user.setType(UserType.PARTNER.toString());
		
		result = (ResultContext) tracingWebService.retrieveProbillsByDeliveryDate(requestContext);
		List<Probill> partnerProbills = (List<Probill>) result.getResult(ContextConstants.PARAM_PROBILL);
        Assert.assertNotNull(partnerProbills); 	
	
	}
}
