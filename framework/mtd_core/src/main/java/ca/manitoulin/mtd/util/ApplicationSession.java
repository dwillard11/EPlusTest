package ca.manitoulin.mtd.util;

import ca.manitoulin.mtd.dto.security.SecureUser;

/**
 * Container to save user session in thread scope
 * @author Bob Yu
 *
 */
public class ApplicationSession {

	private static ThreadLocal<SecureUser> currentUser = new ThreadLocal<SecureUser>();
	
    /** 
     * setter
     * @param session 
     */  
    public static void set(SecureUser session) {    
    	currentUser.set(session);    
    }    
    /** 
     * getter 
     * @return 
     */  
    public static SecureUser get() {    
        return currentUser.get();    
    }      
    /** 
     * remove
     */  
    public static void remove(){  
    	currentUser.remove();  
    } 
}
