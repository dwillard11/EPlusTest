package ca.manitoulin.mtd.wsclient;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.Page;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.massagecentral.Message;
import ca.manitoulin.mtd.dto.massagecentral.MessageSubscription;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.proxy.IWsClientProxy;
import ca.manitoulin.mtd.service.messagecentral.IMessageCentralService;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-ws.xml" })
public class GenericWsClientTest {

	
	
	@Autowired
	private IWsClientProxy proxy;
	

	@Test
	public void testRetrievePage() throws Exception {

		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralService.class);
		requestContext.setMethodName("retrieveMessages");
		requestContext.setPage(new Page<Message>());
		requestContext.setUserProfile(new SecureUser());

		ResultContext resultContext = proxy.execute(requestContext);
		Assert.assertNotNull(resultContext);

	}

	@Test
	public void testRetrieveInfoMessage() throws Exception {
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralService.class);
		requestContext.setMethodName("removeMessages");
		requestContext.addParameter(ContextConstants.CRUD_OBJECT,
				CollectionUtils.arrayToList(new String[] { "101", "102" }));
		requestContext.setUserProfile(new SecureUser());
		ResultContext resultContext = proxy.execute(requestContext);
		Assert.assertNotNull(resultContext);

	}

	@Test(expected=BusinessException.class)
	public void testExceptionWithoutMethodName() throws Exception {
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralService.class);
		// set method name to null to test exception handling
		requestContext.setMethodName(null);
		requestContext.addParameter(ContextConstants.CRUD_OBJECT,
				CollectionUtils.arrayToList(new String[] { "101", "102" }));
		requestContext.setUserProfile(new SecureUser());
		ResultContext resultContext = proxy.execute(requestContext);

		
	}
	
	@Test
	public void testTransferLocaleMessage() throws Exception{
		
		String probill = "9999";
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralService.class);
		requestContext.setMethodName("createMessageSubscription");
		
		MessageSubscription subscription = new MessageSubscription();
		subscription.setProbillNo(probill);
		subscription.setCreateTime(new Date());
		subscription.setTrigger(null);
		
		requestContext.addParameter(ContextConstants.CRUD_OBJECT, subscription);
		requestContext.setUserProfile(new SecureUser());
		ResultContext resultContext = proxy.execute(requestContext);
		Assert.assertNotNull(resultContext);
		final String englishInfo = "Message Subscrption "+probill+" is saved";
		
		Assert.assertEquals(englishInfo, resultContext.getInfoMessage().getMessage());
		
		//Validate French
		requestContext.setLocale(ContextConstants.LOCALE_CANADA_FRENCH);
		final String frenchInfo = "Message Subscrption "+probill+" est enregistré";
		resultContext = proxy.execute(requestContext);
		Assert.assertEquals(Locale.CANADA_FRENCH, resultContext.getLocale());
		
		Assert.assertNotNull(resultContext);
		Assert.assertEquals(frenchInfo, resultContext.getInfoMessage().getMessage());
		
		//Test exception handling
		subscription.setProbillNo(null);
		try{
			resultContext = proxy.execute(requestContext);
		}catch(BusinessException e){
			String errorMsg = "Probill# est nécessaire";
			Assert.assertEquals(errorMsg, e.getMessage());
		}
		
	}
	
}
