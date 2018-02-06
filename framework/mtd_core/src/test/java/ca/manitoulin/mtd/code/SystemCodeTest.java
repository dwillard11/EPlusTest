package ca.manitoulin.mtd.code;

import org.junit.Test;

import junit.framework.Assert;

public class SystemCodeTest {

	@Test
	public void testValue() {
		SystemCode code = SystemCode.valueByCompanyCode("duke");
		Assert.assertEquals(SystemCode.DUKE, code);
		SystemCode code2 = SystemCode.valueByCompanyCode("toyota");
		Assert.assertEquals(SystemCode.MANITOULIN, code2);
	}

}
