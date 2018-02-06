package ca.manitoulin.mtd.util.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.security.SecureUser;

public class JsonHelperTest {

	@Test
	public void testConvertMap() throws IOException{
		Map map = new HashMap();
		map.put("String", new String[]{"A","B"});
		System.out.println(JsonHelper.toJsonString(map));
		
		RequestContext requestContext = new RequestContext();
		requestContext.setServiceClass(String.class);
		requestContext.setMethodName("removeMessages");
		requestContext.addParameter(ContextConstants.CRUD_OBJECT, new String[] { "101", "102" });
		requestContext.setUserProfile(new SecureUser());
		String in = JsonHelper.toJsonString(requestContext);
		System.out.println(in);
	}
}
