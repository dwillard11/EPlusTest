package ca.manitoulin.mtd.job;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.Page;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.proxy.IWsClientProxy;
import ca.manitoulin.mtd.service.tracing.ITracingService;
import ca.manitoulin.mtd.service.tracing.ITracingWebService;
import ca.manitoulin.mtd.util.ApplicationSession;

@Component
public class ScheduledJob {
	
	private static final Logger log = Logger.getLogger(ScheduledJob.class);
	
	@Autowired
	private IWsClientProxy proxy;
	
	@Autowired
	private ITracingService tracingService;

	@Scheduled(cron="0/5 * *  * * ? ")   //execute every 5 seconds
	public void healthCheckJob(){
		log.debug("I am alive");
	}
	
	//TODO too many probills, over 10K+, we can not sure it is ok for our server.
	//@Scheduled(cron="0 15 12 * * ?")   //12:15 AM start
	public void downloadAFP() throws Exception{
		log.info("** Scheduled job of retrieving AFP file begin");
		
		//Page by page retrieve
		Page<String> probillPage = new Page<String>();
		
		probillPage.setParameter("dateBegin", "20160101");
		probillPage.setParameter("dateEnd", "20160301");
		
		SecureUser user = new SecureUser();
		user.setDbSchema(ContextConstants.SCHEMA_PRD);
		user.setId("batchUser");
		ApplicationSession.set(user);
		
		RequestContext requestContext = new RequestContext();
		requestContext.setUserProfile(user);
		requestContext.setPage(probillPage);
		requestContext.setServiceClass(ITracingWebService.class);
		requestContext.setMethodName("retrieveProbillNumberByPage");
		
		ResultContext resultContext = proxy.execute(requestContext);
		
		Page<String> resultPage = (Page<String>) resultContext.getPage();
		
		int totalPage = resultPage.getTotalPage();
		
		log.debug("-> total page of probill numbers is " + totalPage);
		
		downloadAFP(resultPage.getResults());
		log.debug("-> page download: 1" );
		
		for(int i = 2; i <= totalPage; i++){
			resultPage.setPageNo(i);
			downloadAFP((List<String>) proxy.execute(requestContext).getPage().getResults());
			log.debug("-> page download: " + i);
		}
		
		//Download AFP of all probills 
		log.info("** Scheduled job of retrieving AFP file end");
	}
	
	private void downloadAFP(List<String> probillList){
		if(probillList == null) return;
		for(String probillNumber : probillList){
			try {
				
				//TODO tracingService.syncPrepareImages(probillNumber);
				log.info("-> Complete to download images of probill " + probillNumber);
			} catch (Exception e) {
				// Whenever exception catch, never stop
				log.error("-> Failed to download Images of probill " + probillNumber, e);
				log.info("-> Continue to next one");
			}
		}
	}

}
