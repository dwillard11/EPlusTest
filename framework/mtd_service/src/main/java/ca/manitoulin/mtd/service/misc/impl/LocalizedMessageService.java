package ca.manitoulin.mtd.service.misc.impl;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dao.misc.UserDefinedMessageMapper;
import ca.manitoulin.mtd.dto.LocalizedMessage;
import ca.manitoulin.mtd.dto.misc.UserDefinedMessage;
import ca.manitoulin.mtd.service.misc.ILocalizedMessageService;

@Service
public class LocalizedMessageService implements ILocalizedMessageService {
	
	private static Logger log = Logger.getLogger(LocalizedMessageService.class);
	
	@Autowired
	private UserDefinedMessageMapper userDefinedMessageMapper;
	
	private Locale useDefaultLocaleWhenNull(Locale in){
		if(in == null)
			return ContextConstants.LOCALE_DEFAULT;
		else
			return in;
	}

	@Override
	public List<LocalizedMessage> mergeMessages(List<LocalizedMessage> msgList, Locale locale) {
		if(msgList == null) return Collections.EMPTY_LIST;
		
		locale = useDefaultLocaleWhenNull(locale);
		
		for(LocalizedMessage msg : msgList){
			//If message key is not provided, skip.
			if( !StringUtils.isEmpty(msg.getMessageKey())){
				msg.setMessage(this.mergeMessage(msg, locale));
			}			
		}
		
		return msgList;
	}

	@Override
	public String mergeMessage(LocalizedMessage msg, Locale locale) {
		
		locale = useDefaultLocaleWhenNull(locale);
		
		String message = mergeMessage(msg.getMessageKey(), msg.getMessageArg(), locale);
		return message;
	}

	@Override
	public String mergeMessage(String key, List<String> args, Locale locale) {
		
		if(StringUtils.isEmpty(key)) return StringUtils.EMPTY;
		
		locale = useDefaultLocaleWhenNull(locale);
		
		UserDefinedMessage userDefineMessage = userDefinedMessageMapper.selectMessageByKey(key);
		
		String mergedMessage = MessageFormat.format(userDefineMessage.getContent(), (args != null ? args.toArray(new String[]{}) : null));
		String mergedTranslated = MessageFormat.format(userDefineMessage.getTranslatedContent(), (args != null ? args.toArray(new String[]{}) : null));
		
		userDefineMessage.setTranslatedContent(mergedTranslated);
		userDefineMessage.setContent(mergedMessage);
		
		return userDefineMessage.getLocaleContent(locale);
	}

	@Override
	public List<LocalizedMessage> mergeMessages(List<LocalizedMessage> msgList) {
		
		return this.mergeMessages(msgList, null);
	}

	@Override
	public String mergeMessage(LocalizedMessage msg) {
		
		return this.mergeMessage(msg, null);
	}

	@Override
	public String mergeMessage(String key, List<String> args) {
		return this.mergeMessage(key, args, null);
	}

	
	
	

}
