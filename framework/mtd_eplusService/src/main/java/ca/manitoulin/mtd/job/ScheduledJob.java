package ca.manitoulin.mtd.job;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJob {
	
	private static final Logger log = Logger.getLogger(ScheduledJob.class);
	
	

	/*@Scheduled(cron="0/5 * *  * * ? ")   //execute every 5 seconds
	public void healthCheckJob(){
		log.debug("I am alive");
	}*/
	
	
}
