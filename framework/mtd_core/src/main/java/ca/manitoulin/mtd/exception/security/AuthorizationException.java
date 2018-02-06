package ca.manitoulin.mtd.exception.security;

import ca.manitoulin.mtd.exception.BusinessException;

/**
 * Exception raised when trying to perform options unauthorized
 * @author Bob Yu
 *
 */
public class AuthorizationException extends BusinessException {

	public AuthorizationException(){
		super();
	}
	
	public AuthorizationException(String msg){
		super(msg);
	}
	
	public AuthorizationException(String userId, String msg){
		super(new StringBuilder("Account ")
			.append(userId).append(" is not authorized to perform this operation.")
			.append(msg).toString());
	}
}
