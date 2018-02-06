package ca.manitoulin.mtd.service.operationconsole.impl;

import static org.apache.log4j.Logger.getLogger;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.manitoulin.mtd.dto.operationconsole.TripEvent;
import ca.manitoulin.mtd.dto.operationconsole.TripEventNotify;
import ca.manitoulin.mtd.service.operationconsole.ITripService;
import ca.manitoulin.mtd.util.ResultContextSimulator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-email.xml" })
public class TripNotificationTest extends ResultContextSimulator{
	static private Logger log = getLogger(TripNotificationTest.class);

	@Autowired
	private ITripService tripService;
	
	@Before
	public void genSession(){
		super.gengerateUserSession();
	}
	
	
	@Test
	@Rollback(true)
	public void testSendNotify() throws Exception {
		
		final int tripId = 155955;
		List<TripEvent> events = tripService.retrieveEvents(tripId);
		TripEvent event = events.get(0);
		event.setLinkedEntity(1);
		event.setActualDate(new Date());
		tripService.updateEvent(event);
		
		TripEventNotify notify = new TripEventNotify();
		notify.setEmail("yushujiang@gdyd.com");
		notify.setEventId(event.getId());
		notify.setName("Client");

		tripService.createEventNotify(notify);
		
		tripService.sendEventNotification(tripId, "Test Event Subject");
	}

}
