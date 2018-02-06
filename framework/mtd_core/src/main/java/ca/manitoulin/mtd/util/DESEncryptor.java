package ca.manitoulin.mtd.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

public class DESEncryptor {
	
	private static final Logger log = Logger.getLogger(DESEncryptor.class);
	 
    private static KeyGenerator keygen;  

    private static SecretKey deskey;  

    private static Cipher c;  

   
      
    static {  
    	try{
	        Security.addProvider(new com.sun.crypto.provider.SunJCE());  
	        
	        keygen = KeyGenerator.getInstance("DES");  
	
	        deskey = keygen.generateKey();  
	
	        c = Cipher.getInstance("DES");  
    	}catch(Exception e){
    		log.error(e);
    	}
    }  
      
    /** 
     * 
     *  
     * @param str 
     * @return 
     * @throws UnsupportedEncodingException 
     * @throws InvalidKeyException 
     * @throws IllegalBlockSizeException 
     * @throws BadPaddingException 
     */  
    public static String encrypt(String str) throws UnsupportedEncodingException  {  
    	byte[] cipherByte = null;  
        
    	try{
	        c.init(Cipher.ENCRYPT_MODE, deskey);  
	        byte[] src = str.getBytes();  
	        
	        cipherByte = c.doFinal(src); 
    	}catch(Exception e){
    		log.error(e);
    	}
        return new String(cipherByte, "UTF-8");  
    }  
  
    /** 
     * 
     *  
     * @param buff 
     * @return 
     * @throws InvalidKeyException 
     * @throws IllegalBlockSizeException 
     * @throws BadPaddingException 
     */  
    public static String decrypt(String buff)  {  
    	byte[] cipherByte = null;  
    	
    	try{
	        c.init(Cipher.DECRYPT_MODE, deskey);  
	        cipherByte = c.doFinal(buff.getBytes("UTF-8") );  
    	}catch(Exception e){
    		log.error(e);
    	}
    	 return new String(cipherByte);  
    }  
}
