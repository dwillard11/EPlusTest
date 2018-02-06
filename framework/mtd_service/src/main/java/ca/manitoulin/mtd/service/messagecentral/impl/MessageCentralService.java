package ca.manitoulin.mtd.service.messagecentral.impl;

import static ca.manitoulin.mtd.constant.ContextConstants.*;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_MESSAGE_IDS;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_MESSAGE_LIST;
import static ca.manitoulin.mtd.util.json.JsonHelper.toObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.massagecentral.Message;
import ca.manitoulin.mtd.dto.massagecentral.MessageSubscription;
import ca.manitoulin.mtd.proxy.IWsClientProxy;
import ca.manitoulin.mtd.service.messagecentral.IMessageCentralService;
import ca.manitoulin.mtd.service.messagecentral.IMessageCentralWebService;
import ca.manitoulin.mtd.util.json.JacksonMapper;

@Service
public class MessageCentralService implements IMessageCentralService {
	@Autowired
	private IWsClientProxy proxy;

	@SuppressWarnings("unchecked")
	@Override
	public List<Message> retrieveMessages(String advanceSearch) throws Exception {
		
//		List<Message> messageList = new ArrayList<Message>();
//		for (int i = 0; i < 1000; i++) {
//			Message message = new Message();
//			message.setActivity("activity"+i*3);
//			message.setActivityDate(new Date());
//			message.setActivityTime(new Date());
//			message.setAuDelete('N');
//			message.setCreatedBy("createdBY"+i*10);
//			message.setCreateTime(new Date());
//			message.setId(1L+i);
//			message.setInformation("information"+i*8);
//			message.setProbillNo(i+100L*3);
//			message.setUpdatedBy("updatedBY"+3*i);
//			message.setUpdateTime(new Date());
//			messageList.add(message);
//		}
//		return messageList;
		
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralWebService.class);
		requestContext.setMethodName("retrieveMessages");

		requestContext.addParameter(PARAM_ADVANCESEARCH, advanceSearch);
		ResultContext result = proxy.execute(requestContext);
		List<Message> messageList = (List<Message>) result.getResult(PARAM_MESSAGE_LIST);

		return messageList;

	}
	@Override
	public void removeMessages(List<String> messageIds) throws Exception {
		if (messageIds == null || messageIds.isEmpty()) {
			return;
		}
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralWebService.class);
		requestContext.setMethodName("removeMessages");
		
		requestContext.addParameter(PARAM_MESSAGE_IDS, messageIds);
		proxy.execute(requestContext);

		
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<MessageSubscription> retrieveMessageSubscriptions() throws Exception {
		/*List<MessageSubscription> list = new ArrayList<MessageSubscription>();
		for (int i = 0; i < 1000; i++) {
			MessageSubscription messageSubscription = new MessageSubscription();
			messageSubscription.setActivityCode("activityCode"+i);
			messageSubscription.setActivityDescription("activityDescription"+i);
			messageSubscription.setCreatedBy("createdBy"+i);
			messageSubscription.setCreateTime(new Date());
			messageSubscription.setEmail("email"+i);
			messageSubscription.setExpiryDate(new Date());
			messageSubscription.setId("id"+i);
			messageSubscription.setMessageCentral("messageCentral"+i);
			messageSubscription.setNotification("notification"+i);
			messageSubscription.setProbillNo("probillNo"+i);
			messageSubscription.setRequestedDate(new Date());
			messageSubscription.setRequestedTime(new Date());
			messageSubscription.setUpdatedBy("updatedBy"+i);
			messageSubscription.setUpdateTime(new Date());
			list.add(messageSubscription);
		}
		return list;*/
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralWebService.class);
		requestContext.setMethodName("retrieveMessageSubscriptions");
		ResultContext result = proxy.execute(requestContext);
		List<MessageSubscription> list = (List<MessageSubscription>) result.getResult(ContextConstants.PARAM_MESSAGESUBSCRIPTIONS);

		
		return list;
	}

	@Override
	public MessageSubscription updateMessageSubscription(MessageSubscription messageSubscription) throws Exception {
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralWebService.class);
		requestContext.setMethodName("updateMessageSubscription");
		requestContext.addParameter(PARAM_MESSAGE_SUBSCRIPTION, messageSubscription);
		proxy.execute(requestContext);
		return messageSubscription;
	}

	@Override
	public void removeMessageSubscriptions(List<String> messageSubscriptionIds) throws Exception {
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralWebService.class);
		requestContext.setMethodName("removeMessageSubscription");
		for (String messageSubscriptionId : messageSubscriptionIds) {
			requestContext.addParameter(PARAM_MESSAGE_SUBSCRIPTION_ID, messageSubscriptionId);
			proxy.execute(requestContext);
		}
		
		
	}

	@Override
	public MessageSubscription createMessageSubscription(MessageSubscription messageSubscription) throws Exception {
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralWebService.class);
		requestContext.setMethodName("createMessageSubscription");
		requestContext.addParameter(PARAM_MESSAGE_SUBSCRIPTION, messageSubscription);
		proxy.execute(requestContext);
		return messageSubscription;
	}
	@Override
	public Message retrieveMessage(String messageId) throws Exception {
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(IMessageCentralWebService.class);
		requestContext.setMethodName("retrieveMessageDetails");
		requestContext.addParameter(ContextConstants.PARAM_MESSAGE_ID, messageId);
		ResultContext result = proxy.execute(requestContext);
		//Message message = (Message) result.getResult(ContextConstants.PARAM_MESSAGE_DETAIL);
		Message message = toObject(result.getResult(ContextConstants.PARAM_MESSAGE_DETAIL),Message.class);
		return message;
	}

	

	

}
