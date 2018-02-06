package ca.manitoulin.mtd.exception;

import java.util.List;

/**
 * Super class of all application exceptions
 * @author Bob Yu
 *
 */
public class BusinessException extends Exception {
	
	//For localization, user messageBuilderId, can present messages in different language
	protected String messageKey;
	protected List<String> messageArgs;

	public BusinessException(){
		super();
	}
	
	public BusinessException(Throwable arg0){
		super(arg0);
	}
	
	public BusinessException(String msg){
		super(msg);
	}
	
	public BusinessException(String msg, Throwable t){
		super(msg, t);
	}
	
	public BusinessException(String msgKey, List<String> msgArgs, Throwable t){
		super(t);
		messageKey = msgKey;
		messageArgs = msgArgs;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public List<String> getMessageArgs() {
		return messageArgs;
	}

	public void setMessageArgs(List<String> messageArgs) {
		this.messageArgs = messageArgs;
	}

	

	
}
