package ca.manitoulin.mtd.service.messagecentral;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.manitoulin.mtd.dto.Page;
import ca.manitoulin.mtd.dto.massagecentral.Message;
import ca.manitoulin.mtd.dto.massagecentral.MessageSubscription;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.util.ApplicationSession;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-ws.xml" })
public class MessageCentralServiceTest {

	
	@Autowired
	private IMessageCentralService service;

	
	@Before
	public void prepare(){
		SecureUser userSession = new SecureUser();
		userSession.setDbSchema("LTL400V1A3");
		userSession.setId("mkargar");
		userSession.setCompany("manitoulin");
		userSession.setAccount("0019672");
		ApplicationSession.set(userSession);
	}

	@Test
	public void testRetrieveMessageDetail() throws Exception {

		final String messageId = "12278275";
		
		Message message = service.retrieveMessage(messageId);
		
		Assert.assertNotNull(message);
		Assert.assertEquals("Delivered on:                      ", message.getDetailData().get(0).get("HEADING"));
		Assert.assertNotNull(message.getDetailData().get(0).get("DATA"));
	}

	@Test
	public void testRetrieveMessageSubscriptions() throws Exception {
		/*Page<MessageSubscription> page = service.retrieveMessageSubscriptions(new Page<MessageSubscription>());
		List<MessageSubscription> list = page.getResults();
		
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 1);*/

	}	
	
}
