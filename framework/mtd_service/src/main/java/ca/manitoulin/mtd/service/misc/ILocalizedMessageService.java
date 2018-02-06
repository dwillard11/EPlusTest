package ca.manitoulin.mtd.service.misc;

import java.util.List;
import java.util.Locale;

import ca.manitoulin.mtd.dto.LocalizedMessage;

public interface ILocalizedMessageService {
	
	/**
	 * Generate message content
	 * @param msgList
	 * @return
	 */
	List<LocalizedMessage> mergeMessages(List<LocalizedMessage> msgList, Locale locale);
	
	/**
	 * retrieve messages and merge with parameters in need.
	 * @param msg
	 * @param locale
	 * @return
	 */
	String mergeMessage(LocalizedMessage msg, Locale locale);
	
	/**
	 * retrieve messages and merge with parameters provided
	 * @param key
	 * @param args
	 * @param locale
	 * @return
	 */
	String mergeMessage(String key, List<String> args, Locale locale);
	
	/**
	 * Generate message content in English
	 * @param msgList
	 * @return
	 */
	List<LocalizedMessage> mergeMessages(List<LocalizedMessage> msgList);
	
	/**
	 * retrieve English message and merge with parameters in need
	 * @param msg
	 * @param locale
	 * @return
	 */
	String mergeMessage(LocalizedMessage msg);
	
	/**
	 * retrieve English message and merge with parameters provided
	 * @param key
	 * @param args
	 * @param locale
	 * @return
	 */
	String mergeMessage(String key, List<String> args);

}
