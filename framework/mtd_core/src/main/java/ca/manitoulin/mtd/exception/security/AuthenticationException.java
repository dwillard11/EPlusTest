package ca.manitoulin.mtd.exception.security;

import java.util.List;

import ca.manitoulin.mtd.exception.BusinessException;

/**
 * Exception raised when authenticating
 * @author Bob Yu
 *
 */
public class AuthenticationException extends BusinessException {
	
	public AuthenticationException(){
		super();
	}
	
	public AuthenticationException(String msg){
		super(msg);
	}
	
	public AuthenticationException(Throwable arg0){
		super(arg0);
	}
	
	public AuthenticationException(String msg, Throwable t){
		super(msg, t);
	}
	
	public AuthenticationException(String msgKey, List<String> msgArgs, Throwable t){
		super(msgKey, msgArgs, t);
	}

}
