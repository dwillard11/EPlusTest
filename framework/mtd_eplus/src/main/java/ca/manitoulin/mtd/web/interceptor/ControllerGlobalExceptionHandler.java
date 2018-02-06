package ca.manitoulin.mtd.web.interceptor;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.misc.impl.LocalizedMessageService;

@ControllerAdvice
public class ControllerGlobalExceptionHandler {

	private static final Logger log = Logger.getLogger(ControllerGlobalExceptionHandler.class);
	
	@Autowired
	private LocalizedMessageService messageService;
	
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public Object handleBusinessException(HttpServletRequest request, Exception e) throws Exception{
		
		if(AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null )
			throw e;
		
		log.error("++ Exception catched in Controller: ++" ,e);

		
		SecureUser user = (SecureUser) request.getAttribute(ContextConstants.SESSION_USERPROFILE);
		Locale userLocale = (user != null) ? user.getLocale() : null;
		
		String errorMessage = "";
		
		//Generate localized error message
		if(e instanceof BusinessException){
			
			BusinessException bizExp = (BusinessException) e;
			
			if(StringUtils.isEmpty(bizExp.getMessageKey())){
				errorMessage = e.getMessage();
				
			}else{
				errorMessage = messageService.mergeMessage(bizExp.getMessageKey(), bizExp.getMessageArgs(), userLocale);
			}
			
		}else{
			
			//errorMessage = messageService.mergeMessage("error.global.unhandleException", null, userLocale);
			errorMessage = "Unexpected Error Occured!";
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(ContextConstants.KEY_RESULT, ContextConstants.RESPONSE_ERROR);
		map.put(ContextConstants.KEY_MESSAGE, errorMessage);
		return map;

	}
	
	
}
