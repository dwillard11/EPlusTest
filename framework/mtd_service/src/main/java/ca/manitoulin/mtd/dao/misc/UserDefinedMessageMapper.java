package ca.manitoulin.mtd.dao.misc;

import ca.manitoulin.mtd.dto.misc.UserDefinedMessage;

/**
 * DAO for PMLANG
 * @author Bob Yu
 *
 */
public interface UserDefinedMessageMapper {

	
	UserDefinedMessage selectMessageByKey(String messageKey);
	
	void insertMessage(UserDefinedMessage message);
}
