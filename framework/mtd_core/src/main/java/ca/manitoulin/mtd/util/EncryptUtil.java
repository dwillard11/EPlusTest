package ca.manitoulin.mtd.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.UrlBase64;

/**
 * Encryption tool, provides DES, AES encryption and decryption.
 * 
 * @author: Job Yu
 */
public class EncryptUtil {
	// key
	private final static byte[] KRY_BYTES = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10, 0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB,
			(byte) 0xDD, 0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36, (byte) 0xE2 };

	// IV random seed
	private final static byte[] IV_B = { 0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xab, (byte) 0xcd, (byte) 0xef };

	private static final String KeyALGORITHMAES = "AES";

	private static final String ALGORITHM = "DESede";

	private static final String ALGORITHMAES = "AES/CBC/NoPadding";

	private static final String ProviderAES = "BC";// BouncyCastle

	private static final String[] torrentseed = { "S", "O", "F", "T", "H", "I", "G", "s", "_", "f", "a", "2", "3" };

	private static final int AES_BYTE_SIZE = 16;

	private static final int BYTE_LENGTH = 8;

	static {
		
		Security.addProvider(new BouncyCastleProvider());
	}

	private EncryptUtil() {
	}

	/**
	 * encrypt
	 * 
	 * @param oriString
	 *            String to be encrypted
	 * @return String encrypted string
	 */
	@SuppressWarnings("restriction")
	public static String encrypt(String oriString) {
		if (oriString == null || oriString.equals("")) {
			return oriString;
		}
		String result = "";
		byte[] value = encrypt(oriString.getBytes());
		if (value != null) {
			result = (new sun.misc.BASE64Encoder()).encode(value);
		}
		return result;
	}

	/**
	 * decrypt
	 * 
	 * @param oriString
	 *            encrypted string
	 * @return String decrypted string
	 */
	public static String decrypt(String oriString) {
		if (oriString == null || oriString.equals("")) {
			return oriString;
		}
		String result = "";
		try {
			@SuppressWarnings("restriction")
			byte[] value = (new sun.misc.BASE64Decoder()).decodeBuffer(oriString);
			value = decrypt(value);
			if (value != null) {
				result = new String(value);
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * encrypt in bytes 
	 * 
	 * @param src
	 * @return byte[]
	 */
	public static byte[] encrypt(byte[] src) {
		try {

			SecretKey deskey = new SecretKeySpec(KRY_BYTES, ALGORITHM);
			Cipher c1 = Cipher.getInstance("DESede/CFB/NoPadding");

			IvParameterSpec iv = new IvParameterSpec(IV_B);
			c1.init(Cipher.ENCRYPT_MODE, deskey, iv);
			return c1.doFinal(src);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * decrypt in bytes
	 * 
	 * @param src
	 * @return byte[]
	 */
	public static byte[] decrypt(byte[] src) {
		try {
			SecretKey deskey = new SecretKeySpec(KRY_BYTES, ALGORITHM);
			Cipher c1 = Cipher.getInstance("DESede/CFB/NoPadding");
			IvParameterSpec iv = new IvParameterSpec(IV_B);
			c1.init(Cipher.DECRYPT_MODE, deskey, iv);
			return c1.doFinal(src);
		} catch (java.lang.Exception e) {
		}
		return null;
	}


	/**
	 * encode in URL Base64
	 * @param sSectext
	 * @return
	 * @throws Exception:
	 */
	public static String urlBase64Encode(String sSectext) throws Exception{
		byte[] bSectextByte = sSectext.getBytes(Charset.defaultCharset());
		byte[] bBase64Byte = UrlBase64.encode(bSectextByte);
		return new String(bBase64Byte, Charset.defaultCharset());
	}
	
	/**
	 * decode in URL Base64
	 * @param sSectext
	 * @return
	 * @throws Exception:
	 */
	public static String urlBase64Decode(String sSectext) throws Exception{
		byte[] bSectextByte = sSectext.getBytes(Charset.defaultCharset());
		byte[] bBase64Byte = UrlBase64.decode(bSectextByte);
		return new String(bBase64Byte, Charset.defaultCharset());
	}
	
	public static String encryptSHA1(String text){
		
		String rtn = StringUtils.EMPTY;
		try {
			MessageDigest digest = java.security.MessageDigest
					.getInstance("SHA-1");
			digest.update(text.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// Convert to Hex string
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			rtn = hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return rtn;
	}
	
	/** 
     * MD5
     * @param source 
     * @return 
     */  
    public static byte[] encode2MD5bytes(String source) {  
        byte[] result = null;  
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.reset();  
            md.update(source.getBytes("UTF-8"));  
            result = md.digest();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
          
        return result;  
    }  
      
    /** 
     * MD5 HEX string
     * @param source 
     * @return 
     */  
    public static String encode2MD5hex(String source) {  
        byte[] data = encode2MD5bytes(source);  
  
        StringBuffer hexString = new StringBuffer();  
        for (int i = 0; i < data.length; i++) {  
            String hex = Integer.toHexString(0xff & data[i]);  
              
            if (hex.length() == 1) {  
                hexString.append('0');  
            }  
              
            hexString.append(hex);  
        }  
          
        return hexString.toString();  
    }  
      
}
