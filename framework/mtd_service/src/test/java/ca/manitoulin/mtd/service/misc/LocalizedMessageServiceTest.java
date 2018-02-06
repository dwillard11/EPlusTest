package ca.manitoulin.mtd.service.misc;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dao.misc.UserDefinedMessageMapper;
import ca.manitoulin.mtd.dto.misc.UserDefinedMessage;
import ca.manitoulin.mtd.service.misc.impl.LocalizedMessageService;
import ca.manitoulin.mtd.test.AbstractDatabaseOperationTest;
import junit.framework.Assert;


public class LocalizedMessageServiceTest extends AbstractDatabaseOperationTest {
	static private Logger log = Logger.getLogger(LocalizedMessageServiceTest.class);

	@Autowired
	private LocalizedMessageService localizedMessageService;
	
	@Autowired
	private UserDefinedMessageMapper userDefinedMessageMapper;
	

	@Test
	public void testMergeMessage() {
		
		final String messageKey = "999999";
		UserDefinedMessage msgTemplate = new UserDefinedMessage();
		
		msgTemplate.setUniqueKey(messageKey);
		msgTemplate.setContent("Are you the {0}");
		msgTemplate.setLanguage("French");
		msgTemplate.setTranslatedContent("Êtes-vous le {0}");
		
		userDefinedMessageMapper.insertMessage(msgTemplate);
		
		String englishText = localizedMessageService.mergeMessage(messageKey, CollectionUtils.arrayToList(new String[] {"Bob"}), ContextConstants.LOCALE_DEFAULT);
		Assert.assertEquals("Are you the Bob", englishText);
		
		String frenchText = localizedMessageService.mergeMessage(messageKey, CollectionUtils.arrayToList(new String[] {"Bob"}), ContextConstants.LOCALE_CANADA_FRENCH);
		Assert.assertEquals("Êtes-vous le Bob", frenchText);
		
		String defaultText = localizedMessageService.mergeMessage(messageKey, CollectionUtils.arrayToList(new String[] {"Bob"}));
		
		Assert.assertEquals("Are you the Bob", englishText);
	}
}
