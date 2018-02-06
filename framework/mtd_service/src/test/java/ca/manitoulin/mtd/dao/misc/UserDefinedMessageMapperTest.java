package ca.manitoulin.mtd.dao.misc;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.manitoulin.mtd.dto.misc.UserDefinedMessage;
import ca.manitoulin.mtd.test.AbstractDatabaseOperationTest;
import junit.framework.Assert;


public class UserDefinedMessageMapperTest extends AbstractDatabaseOperationTest {
	static private Logger log = Logger.getLogger(UserDefinedMessageMapperTest.class);

	@Autowired
	private UserDefinedMessageMapper userDefinedMessageMapper;

	@Test
	public void selectMessage() {
		
		final String messageKey = "999999";
		UserDefinedMessage msgTemplate = new UserDefinedMessage();
		
		msgTemplate.setUniqueKey(messageKey);
		msgTemplate.setContent("Are you the {0}");
		msgTemplate.setLanguage("French");
		msgTemplate.setTranslatedContent("Êtes-vous le {0}");
		
		userDefinedMessageMapper.insertMessage(msgTemplate);
		UserDefinedMessage msg = userDefinedMessageMapper.selectMessageByKey(messageKey);
		log.debug(msg);
		Assert.assertNotNull(msg);
		
	}
}
