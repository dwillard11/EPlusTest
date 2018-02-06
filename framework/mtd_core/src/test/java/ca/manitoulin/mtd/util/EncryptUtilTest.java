package ca.manitoulin.mtd.util;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import junit.framework.Assert;

public class EncryptUtilTest {

	@Test
	public void testEncryptSHA1() throws UnsupportedEncodingException {
		System.out.println(EncryptUtil.encrypt("viewservlets.jar"));
		System.out.println(DESEncryptor.encrypt("viewservlets.jar"));
		String expected = "c65e6a613aea0a5b32d04ebf61a3b791962eb6e1";
		String input = "helloW0rld!@";
		Assert.assertEquals(expected, EncryptUtil.encryptSHA1(input));
	}

}
