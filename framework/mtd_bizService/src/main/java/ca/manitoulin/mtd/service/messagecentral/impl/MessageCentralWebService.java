package ca.manitoulin.mtd.service.messagecentral.impl;

import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_MESSAGEIDS;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_MESSAGE_SUBSCRIPTION;
import static ca.manitoulin.mtd.util.json.JsonHelper.toObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dao.messagecentral.MessageMapper;
import ca.manitoulin.mtd.dao.messagecentral.MessageSubscriptionMapper;
import ca.manitoulin.mtd.dao.misc.SequenceMapper;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.massagecentral.Message;
import ca.manitoulin.mtd.dto.massagecentral.MessageSubscription;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.messagecentral.IMessageCentralWebService;

@Service
public class MessageCentralWebService implements IMessageCentralWebService {

	private static final Logger log = Logger.getLogger(MessageCentralWebService.class);

	@Autowired
	private MessageMapper messageMapper;
	
	@Autowired
	private MessageSubscriptionMapper messageSubscriptionMapper;
	
	@Autowired
	private SequenceMapper sequenceMapper;

	@Override
	public ResultContext retrieveMessages(RequestContext requestContext)  {
		ResultContext rlt = new ResultContext();
		String advanceSearch = (String) requestContext.getParameter(ContextConstants.PARAM_ADVANCESEARCH);
		String schema = requestContext.getUserProfile().getDbSchema();
		String company = requestContext.getUserProfile().getCompany();
		String userid = requestContext.getUserProfile().getId();
		String userAccount = requestContext.getUserProfile().getAccount();
		
		//NO NEED. Format parameter to fixed length
//		company = StringUtils.rightPad(StringUtils.trimToEmpty(company), 20);
//		userid = StringUtils.rightPad(StringUtils.trimToEmpty(userid), 20);
//		userAccount = StringUtils.rightPad(StringUtils.trimToEmpty(userAccount), 7);
		
		List<Message> messageList = messageMapper.selectMessages(schema, company, userid, userAccount, advanceSearch);
		rlt.addResult(ContextConstants.PARAM_MESSAGE_LIST, messageList);
		return rlt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultContext removeMessages(RequestContext requestContext) {
		ResultContext resultContext = new ResultContext();
		List<String> messageIds = (List<String>) requestContext.getParameter(PARAM_MESSAGEIDS);

		String schema = requestContext.getUserProfile().getDbSchema();

		for (String msgId : messageIds) {
			messageMapper.updateMessageDeleteIndicator(schema, msgId);
			String info = "Message is removed: " + msgId;
			log.info(info);
			resultContext.addInfoMessage(info);
		}
		return resultContext;
	}

	@Override
	@Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
	public ResultContext createMessageSubscription(RequestContext requestContext) throws BusinessException {
		ResultContext resultContext = new ResultContext();

		Object param = requestContext.getParameter(PARAM_MESSAGE_SUBSCRIPTION);
		SecureUser user = requestContext.getUserProfile();

		MessageSubscription messageSubscription = toObject(param,MessageSubscription.class);

		//Do validation
		validateSubscription(messageSubscription);
		//Do extend validation
		if(messageSubscription.getTrigger() == null || messageSubscription.getTrigger() .isEmpty()){
			throw new BusinessException("MessageSubscription.getTrigger() is empty!");
		}
		if(messageSubscription.getRequestedDate() == null) messageSubscription.setRequestedDate(new Date());
		if(messageSubscription.getRequestedTime() == null) messageSubscription.setRequestedTime(new Date());

		
		String probillNo = messageSubscription.getProbillNo();
		//TODO validation. current user has access right to the probill
		
		//Save subscription
		List<String> trigger = messageSubscription.getTrigger();
		for(String activity : trigger){
			if(!"DLVY".equals(activity) && !"RSC".equals(activity)){
				throw new BusinessException("Invalid Activity Code found in MessageSubscription.getTrigger() :" + activity);
			}
			messageSubscription.setActivityCode(activity);
			messageSubscription.setId(null);
			
			messageSubscriptionMapper.insertSubscription(user.getDbSchema(), messageSubscription, user);
			log.debug("-> Message Subscription is saved, action code: " + activity);
			//Update Sequence
			sequenceMapper.updateSequence("MCSEQ");
			
		}
		
		
		
		// Push message to the front.
		resultContext.addInfoMessage("messagecental.create", CollectionUtils.arrayToList(new String[] { probillNo }));

		return resultContext;
	}

	@Override
	public ResultContext retrieveMessageDetails(RequestContext requestContext) {
		ResultContext resultContext = new ResultContext();
		String schema = requestContext.getUserProfile().getDbSchema();


		String messageId = (String) requestContext.getParameter(ContextConstants.PARAM_MESSAGE_ID);
		
		Message message = messageMapper.selectMessageById(schema, messageId);
		if(message != null){
			log.debug("MESSAGE FOUND WITH REFID = "  + messageId);
			List<Map<String, String>> detail = messageMapper.selectMessageDetailById(schema, messageId);
			message.setDetailData(detail);
		}else{
			log.error("MESSAGE NOT FOUND WITH REFID = " + messageId);
		}
		
		resultContext.addResult(ContextConstants.PARAM_MESSAGE_DETAIL, message);
		


		return resultContext;
	}

	
	private boolean validateSubscription(MessageSubscription messageSubscription) throws BusinessException{

		if(StringUtils.isEmpty(messageSubscription.getNotification())){
			throw new BusinessException("MessageSubscription.notification is empty!");
		}
		if(!"B".equals(messageSubscription.getNotification()) && !"E".equals(messageSubscription.getNotification()) && !"M".equals(messageSubscription.getNotification())){
			throw new BusinessException("Invalid Notificaiton Type found in MessageSubscription.getNotification() :" +messageSubscription.getNotification());
		}
		return true;
	}
	
	@Override
	public ResultContext updateMessageSubscription(RequestContext requestContext) throws BusinessException {
		ResultContext resultContext = new ResultContext();
		String schema = requestContext.getUserProfile().getDbSchema();
		Object param = requestContext.getParameter(PARAM_MESSAGE_SUBSCRIPTION);
		
		MessageSubscription messageSubscription = toObject(param, MessageSubscription.class);
		
		validateSubscription(messageSubscription);
		//activityCode should be given
		String activity = StringUtils.trimToEmpty(messageSubscription.getActivityCode());
		if(StringUtils.isEmpty(activity)){
			throw new BusinessException("MessageSubscription.activityCode is empty!");
		}
		if(!"DLVY".equals(activity) && !"RSC".equals(activity)){
			throw new BusinessException("Invalid Activity Code found in MessageSubscription.activityCode :" + activity);
		}
		//Prevent API users make mistake
		if("All Probills".equals(messageSubscription.getProbillNo())){
			messageSubscription.setProbillNo(null);
		}
		
		String probillNo = messageSubscription.getProbillNo();
		//TODO validation. current user has access right to the probill
		messageSubscriptionMapper.updateSubscription(schema, messageSubscription);
		// Push message to the front.
		resultContext.addInfoMessage("messagecental.create", CollectionUtils.arrayToList(new String[] { probillNo }));

		return resultContext;
	}

	@Override
	@Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRED)
	public ResultContext removeMessageSubscription(RequestContext requestContext) {
		String schema = requestContext.getUserProfile().getDbSchema();
		String subscriptionId = (String) requestContext.getParameter(ContextConstants.PARAM_MESSAGE_SUBSCRIPTION_ID);
		messageSubscriptionMapper.deleteSubscription(schema, subscriptionId);
		//TODO insert log in audit table
		/*
		 *  my $sql2 = qq{ 	INSERT INTO $CEnv{LIB}.mcp130
							(AUDELETE, AUACCOUNT, AUCOMPMTD, AUCONTENT, AUMTDHIDE, 
								AULTLHIDE, AUDATEADD, AUTIMEADD, AUMTDUSER )
						VALUES
							(?,?,?,?,?,?,?,?,?) };
							 $deletemessage = $chk . " subscription deleted by user.";
			$dbh->do( $sql2, undef, ( 'Y', $User->{num}, $User->{comp}, $deletemessage, 'Y', 'Y', TDSformat_date( $User->{curdate}, "-" ), TDSformat_time( $User->{curtime}, ":" ), $User->{uid} ) );
		 */
		
		ResultContext resultContext = new ResultContext();
		return resultContext;
	}
	

	@Override
	public ResultContext retrieveMessageSubscriptions(RequestContext requestContext) {
		ResultContext resultContext = new ResultContext();
		String schema = requestContext.getUserProfile().getDbSchema();
		String company = requestContext.getUserProfile().getCompany();
		String userid = requestContext.getUserProfile().getId();
		String userAccount = requestContext.getUserProfile().getAccount();
		
		List<MessageSubscription> list = messageSubscriptionMapper.selectSubscriptions(schema, userid, company, userAccount);
		
		resultContext.addResult(ContextConstants.PARAM_MESSAGESUBSCRIPTIONS, list);
		
		return resultContext;
	}

	@Override
	public ResultContext retrieveMessageSubscription(RequestContext requestContext) {
		String schema = requestContext.getUserProfile().getDbSchema();
		String subscriptionId = (String) requestContext.getParameter(ContextConstants.CRUD_OBJECT);
		MessageSubscription ms = messageSubscriptionMapper.selectSubscription(schema, subscriptionId);
		ResultContext resultContext = new ResultContext();
		resultContext.addResult(ContextConstants.CRUD_OBJECT, ms);
		return resultContext;
		
	}
}
