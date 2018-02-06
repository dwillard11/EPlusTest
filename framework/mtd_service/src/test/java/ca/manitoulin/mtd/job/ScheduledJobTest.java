package ca.manitoulin.mtd.job;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-ws.xml" })
public class ScheduledJobTest {

	@Autowired
	private ScheduledJob sechduledJob;
	public static void main(String[] arg){

		ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring/applicationContext-wsClient.xml",
				"spring/applicationContext-core.xml",
				"spring/applicationContext-mysql.xml",
				
				"spring/applicationContext-schedule.xml"
				});

	}
	
	@Test
	public void testDownloadAFP() throws Exception{
		sechduledJob.downloadAFP();
	}
	
}
