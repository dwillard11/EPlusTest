package ca.manitoulin.mtd.tracing.impl;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.manitoulin.mtd.code.SystemCode;
import ca.manitoulin.mtd.code.UserType;
import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.support.Attachment;
import ca.manitoulin.mtd.dto.tracing.ManifestInfo;
import ca.manitoulin.mtd.dto.tracing.Probill;
import ca.manitoulin.mtd.service.tracing.ITracingWebService;
import ca.manitoulin.mtd.service.tracing.impl.TracingWebService;
import ca.manitoulin.mtd.test.AbstractDatabaseOperationTest;
import ca.manitoulin.mtd.util.json.JacksonMapper;
import ca.manitoulin.mtd.util.json.JsonHelper;
import junit.framework.Assert;

public class TracingWebServiceTest extends AbstractDatabaseOperationTest  {
	
	private static final Logger log = Logger.getLogger(TracingWebServiceTest.class);
	
	private RequestContext requestContext;
	
	@Autowired
	private ITracingWebService tracingWebService;
	
	@Before
	public void init(){
		requestContext = new RequestContext();
		requestContext.setServiceClass(ITracingWebService.class);
		
		SecureUser user = new SecureUser();
		user.setDbSchema(ContextConstants.SCHEMA_PRD);
		user.setCompany("MANITOULIN");
		user.setSystemId(SystemCode.valueByCompanyCode(user.getCompany()).toString());
		user.setAccount("0017205");
		requestContext.setUserProfile(user);
	}

	@Test
	public void testCalculateResechduleDate(){
		String probillNumber = "18460423";
		requestContext.addParameter(ContextConstants.PARAM_PROBILLNO, probillNumber);
		ResultContext result = (ResultContext) tracingWebService.retrieveProbillByNumber(requestContext);
		Probill probill = (Probill) result.getResult(ContextConstants.PARAM_PROBILL); 	
		
		Assert.assertNotNull(probill.getRescheduledReason());
		
	}
	
	@Test
	public void testCalculateETAAndSpecialTips(){
		String probillNumber = "200590442";
		requestContext.addParameter(ContextConstants.PARAM_PROBILLNO, probillNumber);
		ResultContext result = (ResultContext) tracingWebService.retrieveProbillByNumber(requestContext);
		Probill probill = (Probill) result.getResult(ContextConstants.PARAM_PROBILL); 	
		
		Assert.assertTrue(probill.getDeliveryDate() == null);
		Assert.assertEquals("2016/04/07",probill.getCalculatedETA());
		Assert.assertNotNull(probill.getDeliverySpecialTips());
		Assert.assertEquals("NA", probill.getDeliveryAppointment());
	}

	@Test
	public void testSectionControlForPartner(){
		String probillNumber = "91805427";
		requestContext.addParameter(ContextConstants.PARAM_PROBILLNO, probillNumber);
		SecureUser user = requestContext.getUserProfile();
		user.setType(UserType.PARTNER.toString());
		user.setAccount("DAFG");
		ResultContext result = (ResultContext) tracingWebService.retrieveProbillByNumber(requestContext);
		Probill probill = (Probill) result.getResult(ContextConstants.PARAM_PROBILL); 	
		
		Assert.assertTrue(probill.getPiecesList() == null);
		Assert.assertFalse(probill.getPartnerList() == null);
		Assert.assertNull(probill.getInterlineTo());
		System.out.println(probill.getInterlineFrom());
		Assert.assertEquals("$53.00 for DAFG", probill.getRevenue());
		Assert.assertEquals("65.20% $197.62", probill.getDiscount());
	}

	@Test
	public void testRetrieveManifestInfo() throws IOException{
		
		String[] probillNumbers = {"23697301","18356805","91882775","40600316"};
		Probill probill = new Probill();
		String systemCode = "MAN";
		for(String probillNumber : probillNumbers){
			
			probill.setProbillNumber(probillNumber);
			List<ManifestInfo>  list = tracingWebService.retrieveManifestInfo(probill, systemCode, ContextConstants.SCHEMA_PRD);
			Assert.assertNotNull(list);
			
			for(ManifestInfo manifest : list){
				System.out.println("***");
				System.out.println(JsonHelper.toJsonString(manifest));
				System.out.println("***");
			}
			
			ManifestInfo m1 = list.get(0);
			Assert.assertEquals("Delivered", m1.getStatus());
			Assert.assertNotNull(m1.getStatusTooltip());
		}

		
	}

	@Test
	public void testRetrieveProbillByPO(){
		String po = "VPO00406135";
		requestContext.addParameter(ContextConstants.PARAM_TRACING_PO, po);

		ResultContext result = (ResultContext) tracingWebService.retrieveProbillByPO(requestContext);

        Assert.assertNotNull(result.getResult(ContextConstants.PARAM_PROBILL)); 	
	
	}
	
	@Test
	public void testRetrieveProbillByNumber() throws Exception{
		String probill = "91882775";
		requestContext.addParameter(ContextConstants.PARAM_PROBILLNO, probill);

		ResultContext result = (ResultContext) tracingWebService.retrieveProbillByNumber(requestContext);
		
		Probill p = (Probill) result.getResult(ContextConstants.PARAM_PROBILL);

        Assert.assertNotNull(p); 	
        
        Assert.assertNotNull(p.getImages());
        
        for(Attachment image : p.getImages()){
        	log.debug(JsonHelper.toJsonString(image));
        }
	}

	@Test
	public void testRetrieveProbillByBOL() throws Exception{
		//String bol = "AAAEA8D574E182B3";
		String bol = "23730093";
		requestContext.addParameter(ContextConstants.PARAM_TRACING_BOL, bol);
		requestContext.addParameter(ContextConstants.PARAM_ACCOUNT_TYPE, "C");

		ResultContext result = (ResultContext) tracingWebService.retrieveProbillByBOL(requestContext);

        Assert.assertNotNull(result.getResult(ContextConstants.PARAM_PROBILL)); 	
        
        bol = "AAAEA8D574E182B3"; 
        requestContext.addParameter(ContextConstants.PARAM_TRACING_BOL, bol);
        result = (ResultContext) tracingWebService.retrieveProbillByBOL(requestContext);
        Assert.assertNull(result.getResult(ContextConstants.PARAM_PROBILL)); 	
	}
	
	@Test
	public void testRetrieveProbillByShipper() throws Exception{
		String shipper = "PON4133959";
		requestContext.addParameter(ContextConstants.PARAM_TRACING_SHIPPER, shipper);

		ResultContext result = (ResultContext) tracingWebService.retrieveProbillByShipper(requestContext);

        Assert.assertNotNull(result.getResult(ContextConstants.PARAM_PROBILL)); 
        
	}	
	
	@Test
	public void testRetrieveProbillByPickupNumber() throws Exception{
		String pickup = "8655013";
		//String pickup = "0018526429";
		requestContext.addParameter(ContextConstants.PARAM_TRACING_PICKUPNUMBER, pickup);

		ResultContext result = (ResultContext) tracingWebService.retrieveProbillByPickupNumber(requestContext);

        Assert.assertNotNull(result.getResult(ContextConstants.PARAM_PROBILL)); 
        
	}	


	
}
