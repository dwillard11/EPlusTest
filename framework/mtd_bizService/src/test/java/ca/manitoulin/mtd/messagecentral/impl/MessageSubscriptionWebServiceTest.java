package ca.manitoulin.mtd.messagecentral.impl;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.massagecentral.MessageSubscription;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.messagecentral.IMessageCentralWebService;
import ca.manitoulin.mtd.test.AbstractDatabaseOperationTest;
import junit.framework.Assert;

public class MessageSubscriptionWebServiceTest extends AbstractDatabaseOperationTest {
	

	@Autowired
	private IMessageCentralWebService messageService;
	
	private MessageSubscription ms;
	
	private RequestContext requestContext;
	
	@Before
	public void init(){
		requestContext = new RequestContext();
		
		
		SecureUser user = new SecureUser();
		//Writable Schema
		user.setDbSchema("LTL400TST3");
		user.setAccount("0061650");
		user.setCompany("MANITOULIN");
		user.setCustomer("AFFILIA");
		user.setId("affiated");
		
		
		requestContext.setUserProfile(user);
		
		ms = new MessageSubscription();
		ms.setActivityCode("DLVY");
		ms.setEmail("someone@q.com");
		ms.setExpiryDate(null);
		ms.setNotification("E");
		ms.setTrigger(CollectionUtils.arrayToList(new String[]{"DLVY","RSC"}));
		
		requestContext.addParameter(ContextConstants.CRUD_OBJECT, ms);
		
	}
	
	@Test
	public void testCRUDOperation() throws BusinessException{
		List<MessageSubscription> begin = queryCurrentSubscriptions();
		int beginCount = begin == null ? 0 : begin.size();
		
		//create
		messageService.createMessageSubscription(requestContext);
		
		List<MessageSubscription> afterInsert = queryCurrentSubscriptions();
		Assert.assertTrue(afterInsert.size() == beginCount + 2);
		
		MessageSubscription newly = afterInsert.get(afterInsert.size() - 1);
		Assert.assertEquals("All Probills", newly.getProbillNo());
		
		String updateId = newly.getId();
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, 1);
		newly.setExpiryDate(c.getTime());
		newly.setProbillNo("89038473");
		newly.setNotification("M");
		//update 
		requestContext.addParameter(ContextConstants.CRUD_OBJECT, newly);
		messageService.updateMessageSubscription(requestContext);
		
		//retrieve
		requestContext.addParameter(ContextConstants.CRUD_OBJECT, updateId);
		ResultContext rc = messageService.retrieveMessageSubscription(requestContext);
		MessageSubscription updated = (MessageSubscription) rc.getResult(ContextConstants.CRUD_OBJECT);
		
		//Probill will be appended to 25 length 
		Assert.assertEquals(newly.getProbillNo(), updated.getProbillNo().trim());
		Assert.assertNotNull(updated.getExpiryDate());
		
		//Remove
		requestContext.addParameter(ContextConstants.CRUD_OBJECT, updateId);
		messageService.removeMessageSubscription(requestContext);
		List<MessageSubscription> afterDelete = queryCurrentSubscriptions();
		Assert.assertEquals(beginCount + 1, afterDelete.size());
		
		
	}
	
	private List<MessageSubscription> queryCurrentSubscriptions(){
		ResultContext result = messageService.retrieveMessageSubscriptions(requestContext);
		List<MessageSubscription> list = (List<MessageSubscription>) result.getResult(ContextConstants.PARAM_MESSAGESUBSCRIPTIONS);
		return list;
	}
	
}
