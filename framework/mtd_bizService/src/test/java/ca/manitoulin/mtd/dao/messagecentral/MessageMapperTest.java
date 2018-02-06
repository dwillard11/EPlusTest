package ca.manitoulin.mtd.dao.messagecentral;

import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.manitoulin.mtd.dto.massagecentral.Message;
import ca.manitoulin.mtd.test.AbstractDatabaseOperationTest;
import junit.framework.Assert;

public class MessageMapperTest extends AbstractDatabaseOperationTest {
	static private Logger log = Logger.getLogger(MessageMapperTest.class);

	@Autowired
	private MessageMapper messageMapper;

	@Test
	public void testSelectMessages() throws ParseException {
		// TODO unit test for select messages
		String schema = "LTL400V1A3";
		String company = "MANITOULIN";
		String userAccount = "0061650";
		String userid = "affiated";
		String advanceSearch = null;
		List<Message> list = messageMapper.selectMessages(schema, company, userid, userAccount, advanceSearch);
		Assert.assertFalse(list.isEmpty());

		for (Message message : list) {
			log.debug(message);
		}

	}

	/*
	 * @Test // @Rollback(true) -- defined in its supper class public void
	 * public void testDeleteMessage() { Long id = 1513499L;
	 * 
	 * messageMapper.deleteMessage(SCHEMA_TEST, id);
	 * 
	 * Page<Message> page8 = new Page<Message>(); page8.setPageNo(1);
	 * page8.setPageSize(10); page8.setParameter("schema", SCHEMA_TEST);
	 * page8.setParameter("username", "abisram"); page8.setParameter("userid",
	 * "0030404"); page8.setParameter("company", "MANITOULIN");
	 * page8.setParameter("id", id); List<Message> listPyID =
	 * messageMapper.selectMessages(page8);
	 * Assert.assertFalse(listPyID.isEmpty());
	 * log.debug("===========list By id(value:" + id +
	 * ")  start==============="); for (Message message : listPyID) {
	 * log.debug(message); } log.debug("===========list By id(value:" + id +
	 * ")  end===============");
	 * 
	 * }
	 */

}
