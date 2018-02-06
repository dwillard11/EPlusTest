package ca.manitoulin.mtd.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Object to transfer message which can be presented in different languages.
 * @author Bob Yu
 *
 */
public class LocalizedMessage implements Serializable {
	
	private String messageKey;
	private String message;
	private List<String> messageArg;
	
	private String category; //INFO, WARN, ERROR
	
	public LocalizedMessage(){
		
	}
	
	public LocalizedMessage(String msg){
		message = msg;
	}
	
	public LocalizedMessage(String key, List<String> msgArgs){
		messageKey = key;
		messageArg = msgArgs;
	}
	
	public String getMessageKey() {
		return messageKey;
	}
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}


	public List<String> getMessageArg() {
		return messageArg;
	}

	public void setMessageArg(List<String> messageArg) {
		this.messageArg = messageArg;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "LocalizedMessage [messageKey=" + messageKey + ", message=" + message + ", messageArg=" + messageArg
				+ ", category=" + category + "]";
	}

}
