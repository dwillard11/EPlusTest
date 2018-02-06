package ca.manitoulin.mtd.exception;

import java.util.List;

import ca.manitoulin.mtd.exception.BusinessException;

/**
 * Exception raised in concurrent problem
 * @author Bob Yu
 *
 */
public class ConcurrentException extends BusinessException {
	
	public ConcurrentException(){
		super();
	}
	
	public ConcurrentException(String msg){
		super(msg);
	}
	
	public ConcurrentException(Throwable arg0){
		super(arg0);
	}
	
	public ConcurrentException(String msg, Throwable t){
		super(msg, t);
	}
	
	public ConcurrentException(String msgKey, List<String> msgArgs, Throwable t){
		super(msgKey, msgArgs, t);
	}

}
