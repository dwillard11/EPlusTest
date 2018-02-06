package ca.manitoulin.mtd.proxy;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ca.manitoulin.mtd.dto.LocalizedMessage;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.misc.ILocalizedMessageService;
import ca.manitoulin.mtd.util.ApplicationSession;
import ca.manitoulin.mtd.util.ReflectionUtil;
import ca.manitoulin.mtd.util.json.JsonHelper;
import ca.manitoulin.mtd.webservice.IBusinessFacade;

public class WsClientProxy implements IWsClientProxy {

	private static final Logger log = Logger.getLogger(WsClientProxy.class);
	@Autowired
	private IBusinessFacade businessFacade;
	@Autowired
	private ILocalizedMessageService localizedMessageService;

	@Override
	public ResultContext execute(RequestContext requestContext) throws Exception {

		//retrieve User profile from ThreadLocal
		SecureUser requestUser = ApplicationSession.get();
		requestContext.setUserProfile(requestUser);
		
		// Do validation before call remote service
		if(validateRequestContext(requestContext) == false)
			return null;
		
		// Set schema as one of params in Page, if page is not null
		if(requestContext.getPage() != null){
			requestContext.getPage().setParameter("schema", requestUser.getDbSchema());
		}
		
		//Invoke remote service
		long begin = System.currentTimeMillis();
		String in = JsonHelper.toJsonString(requestContext);
		String result = businessFacade.execute(in);
		ResultContext resultContext = JsonHelper.toObject(result, ResultContext.class);

		//Logging
		logOperation(requestContext, resultContext, begin);
		
		if(!resultContext.isSuccess()){
			localizedMessage(resultContext.getErrorMessage(), resultContext.getLocale());
			throw new BusinessException(resultContext.getErrorMessage().getMessage());
			
		}
		
		//Retrieve back-end message when necessary
		localizedMessage(resultContext.getInfoMessage(), resultContext.getLocale());
		
		
		
		return resultContext;
	}
	
	private void localizedMessage(LocalizedMessage infoMessage, Locale locale){
		if(infoMessage == null) return;
		
		if(StringUtils.isEmpty(infoMessage.getMessage())){
			
			String message = localizedMessageService.mergeMessage(infoMessage, locale);
			infoMessage.setMessage(message);
		}
	}
	
	
	
	private void logOperation(RequestContext requestContext, ResultContext resultContext, long timeBegin){
		
		Class serviceClass = requestContext.getServiceClass();
		String methodInvoked = requestContext.getMethodName();
		SecureUser user = requestContext.getUserProfile();
		
		log.info(".");
		log.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		log.info("+   USER ID 	= " + user.getId());
		log.info("+   SERVICE 	= " + serviceClass.getName());
		log.info("+   METHOD 	= " + methodInvoked);
		log.info("+   TIME 		= " + (System.currentTimeMillis() - timeBegin) / 1000 + " SECONDS");
		log.info("+   SUCCESS 	= " + resultContext.isSuccess());
		log.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		log.info(".");

		// TODO log in system table!
	}
	
	private boolean validateRequestContext(RequestContext requestContext) throws Exception{
		Class serviceClass = requestContext.getServiceClass();
		String methodInvoked = requestContext.getMethodName();
		SecureUser user = requestContext.getUserProfile();

		if(serviceClass == null){
			throw new BusinessException("service class is required!");
		}
		
		if(StringUtils.isEmpty(methodInvoked) || ReflectionUtil.obtainMethod(serviceClass, methodInvoked) ==null){
			throw new BusinessException("Method '" + methodInvoked + "' is not find in " + serviceClass);
			
		}
		
		if(user == null){
			throw new BusinessException("User profile is required!");
			
		}
		return true;
	}

}
