package ca.manitoulin.mtd.service.messagecentral;

import java.util.List;

import ca.manitoulin.mtd.dto.Page;
import ca.manitoulin.mtd.dto.massagecentral.Message;
import ca.manitoulin.mtd.dto.massagecentral.MessageSubscription;

public interface IMessageCentralService {

	List<Message> retrieveMessages(String advanceSearch) throws Exception;
	void removeMessages(List<String> messageIds) throws Exception;
	
	List<MessageSubscription> retrieveMessageSubscriptions()throws Exception;
	
	MessageSubscription updateMessageSubscription(MessageSubscription messageSubscription) throws Exception;
	
	void removeMessageSubscriptions(List<String> messageSubscription) throws Exception;
	
	MessageSubscription createMessageSubscription(MessageSubscription messageSubscription)  throws Exception;

	Message retrieveMessage(String messageId)throws Exception;
}
