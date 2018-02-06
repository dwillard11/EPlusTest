package ca.manitoulin.mtd.service.notify.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.manitoulin.mtd.dao.operationconsole.TripDocumentMapper;
import ca.manitoulin.mtd.dto.misc.CommunicationEmail;
import ca.manitoulin.mtd.dto.operationconsole.TripDocument;
import ca.manitoulin.mtd.service.notify.INotificationService;
import ca.manitoulin.mtd.util.ApplicationSession;
import ca.manitoulin.mtd.util.ResultContextSimulator;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-email.xml"})
public class NotificationServiceTest extends ResultContextSimulator{

   /* static private Logger log = Logger.getLogger(NotificationServiceTest.class);
    @Autowired
    private INotificationService noteService;
    @Autowired
    private TripDocumentMapper docMapper;
    @Before
	public void genSession(){
		super.gengerateUserSession();
	}
    
    @Test
    public void testSendEmail() throws Exception {
    	final int tripId = 155960;
    	CommunicationEmail mail = new CommunicationEmail();
    	mail.setTripId(tripId);
    	mail.setSubject("Eplus Test Email");
    	mail.setContent("<H>Hello World</H>");
    	mail.setMailTo("1050389989@qq.com;yushujiang@gdyd.com");
    	mail.setMailCc("shujiang.yu@gmail.com");
    	
    	List<TripDocument> doc = docMapper.selectTripDocuments(tripId, ApplicationSession.get().getReferLanguage());
    	mail.setAttachments(doc);
    	
    	noteService.sendEmail(mail);
    	
    }*/

    
}