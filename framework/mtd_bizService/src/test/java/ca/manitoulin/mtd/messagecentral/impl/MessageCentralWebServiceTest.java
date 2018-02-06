package ca.manitoulin.mtd.messagecentral.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.massagecentral.Message;
import ca.manitoulin.mtd.dto.massagecentral.MessageSubscription;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.messagecentral.IMessageCentralWebService;
import ca.manitoulin.mtd.service.messagecentral.impl.MessageCentralWebService;
import ca.manitoulin.mtd.test.AbstractDatabaseOperationTest;
import ca.manitoulin.mtd.util.ReflectionUtil;
import junit.framework.Assert;

public class MessageCentralWebServiceTest extends AbstractDatabaseOperationTest {
	
	@Autowired
	private MessageCentralWebService messageService;
	
	@Test
	public void testRetrieveMessages(){
		RequestContext requestContext = new RequestContext();
		
		SecureUser user = new SecureUser();
		user.setDbSchema("LTL400V1A3");
		user.setId("affiated");
		user.setAccount("0061650");
		user.setCompany("manitoulin");
		requestContext.setUserProfile(user);
		
		ResultContext result = messageService.retrieveMessages(requestContext);
		List<Message> list = (List<Message>) result.getResult(ContextConstants.PARAM_MESSAGE_LIST);
		
		Assert.assertNotNull(list);
	}
	
	@Test 
	public void testRetrieveMessageDetails(){
		final String messageId = "12278275";
		RequestContext requestContext = new RequestContext();
		requestContext.addParameter(ContextConstants.PARAM_MESSAGE_ID, messageId);
		SecureUser user = new SecureUser();
		user.setDbSchema("LTL400V1A3");
		requestContext.setUserProfile(user);
		
		ResultContext result = messageService.retrieveMessageDetails(requestContext);
		Message message = (Message) result.getResult(ContextConstants.PARAM_MESSAGE_DETAIL);
		
		Assert.assertNotNull(message);
		Assert.assertEquals("Delivered on:                      ", message.getDetailData().get(0).get("HEADING"));
		Assert.assertNotNull(message.getDetailData().get(0).get("DATA"));
	}
	
	@Test
	public void testInvoke() throws Exception{
		String probill = "9999";
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralWebService.class);
		requestContext.setMethodName("createMessageSubscription");
		
		MessageSubscription subscription = new MessageSubscription();
		subscription.setProbillNo(null);
		subscription.setCreateTime(new Date());
		subscription.setTrigger(null);
		
		requestContext.addParameter(ContextConstants.CRUD_OBJECT, subscription);
		requestContext.setUserProfile(new SecureUser());
		
		MessageCentralWebService service = new MessageCentralWebService();
		try{
			ResultContext result = (ResultContext) ReflectionUtil.invokeMethod(service, "createMessageSubscription", new Class[]{requestContext.getClass()}, new Object[]{requestContext});

        } catch (Exception e){
        	InvocationTargetException itException = (InvocationTargetException) e;
        	Assert.assertEquals(BusinessException.class, itException.getTargetException().getClass());
        }   	
	}
	
	@Test
	public void testRetrieveMessageSubscriptions(){
		
		RequestContext requestContext = new RequestContext();
		
		SecureUser user = new SecureUser();
		user.setDbSchema("LTL400V1A3");
		user.setId("mkargar");
		user.setCompany("manitoulin");
		user.setAccount("0019672");
		requestContext.setUserProfile(user);
		
		ResultContext result = messageService.retrieveMessageSubscriptions(requestContext);
		List<MessageSubscription> list = (List<MessageSubscription>) result.getResult(ContextConstants.PARAM_MESSAGESUBSCRIPTIONS);
		
		Assert.assertNotNull(list);

	}
}
