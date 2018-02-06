package ca.manitoulin.mtd.service.messagecentral;

import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.exception.BusinessException;

public interface IMessageCentralWebService {

	/**
	 * Retrieve Messages
	 * @param requestContext
	 * @return
	 * @throws BusinessException 
	 */
	ResultContext retrieveMessages(RequestContext requestContext) ;
	
	/**
	 * Remove messages by message reference id
	 * @param requestContext
	 * @return
	 */
	ResultContext removeMessages(RequestContext requestContext);
	
	/**
	 * Retrieve message details by reference id
	 * @param requestContext -ContextConstants.PARAM_MESSAGE_ID is required for messageId
	 * @return ContextConstants.PARAM_MESSAGE_DETAIL to get message
	 */
	ResultContext retrieveMessageDetails(RequestContext requestContext);
	
	ResultContext createMessageSubscription(RequestContext requestContext) throws BusinessException;
	
	ResultContext updateMessageSubscription(RequestContext requestContext) throws BusinessException;
	
	ResultContext removeMessageSubscription(RequestContext requestContext);
	
	ResultContext retrieveMessageSubscriptions(RequestContext requestContext);
	
	ResultContext retrieveMessageSubscription(RequestContext requestContext);
	
}
