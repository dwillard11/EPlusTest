package ca.manitoulin.mtd.service.share;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-ws.xml" })
public class ShareURLServiceTest{
	@Autowired
	private IShareURLService shareURLService;
	 @Test
	 public void testGenerateShareURL(){
	  		Map<String, String> params = new HashMap<String,String>();
	  		params.put("id", "abc");
	  		params.put("isShare", "Y");
	  		String base = "http://mtdirect.dapasoft.com";
	  		String destination = "/index.html#/app/messagecentral/messagedetails";
	  		System.out.println(shareURLService.generateShareURL(base, destination, params));
	 }
	 @Test
	 public void testObtainURLDestinationAndParams() {
		System.out.println(shareURLService.obtainURLDestinationAndParams("http://mtdirect.dapasoft.com/login.html#/app/messagecentral/messagedetails?id=12278275&isShare=Y"));
	 }
   
}
