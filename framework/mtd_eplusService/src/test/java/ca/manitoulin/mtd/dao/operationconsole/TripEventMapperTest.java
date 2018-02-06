package ca.manitoulin.mtd.dao.operationconsole;

import ca.manitoulin.mtd.dto.operationconsole.Trip;
import ca.manitoulin.mtd.dto.operationconsole.TripEvent;
import ca.manitoulin.mtd.dto.operationconsole.TripEventNotify;
import ca.manitoulin.mtd.util.ApplicationSession;
import ca.manitoulin.mtd.util.ResultContextSimulator;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.apache.log4j.Logger.getLogger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-email.xml" })
public class TripEventMapperTest extends ResultContextSimulator{
	static private Logger log = getLogger(TripEventMapperTest.class);

	@Autowired
	private TripEventMapper eventMapper;
	
	@Before
	public void genSession(){
		super.gengerateUserSession();
	}
	
	@Test
	@Rollback(true)
	public void testAll() {
		
		final int tripId = 10666;
		eventMapper.deleteTripEventsByTrip(tripId);
		Trip trip = new Trip();
		trip.setId(tripId);
		eventMapper.insertTripsWithTemplate(trip);
		
		List<TripEvent> events = eventMapper.selectTripEvents(tripId, ApplicationSession.get().getReferLanguage());	
		Assert.assertNotNull(events);
		
		TripEvent event = events.get(2);
		event.setId(null);
		event.setSequence(9999);
		eventMapper.insertTripEvent(event);
		
		TripEvent saveEvent = eventMapper.selectTripEventById(event.getId());
		Assert.assertNotNull(saveEvent);
		eventMapper.deleteTripEvent(saveEvent.getId());
		
		TripEvent e = events.get(0);
		e.setLink("Link");
		e.setSetup("Setup");
		eventMapper.updateTripEvent(e);
		
		
		List<TripEvent> newEvents = eventMapper.selectTripEvents(tripId, ApplicationSession.get().getReferLanguage());	
		Assert.assertEquals(events.size(), newEvents.size());
		Assert.assertEquals("Link",	 newEvents.get(0).getLink());
		
	}

    @Test
    public void testSelectEventNotifies() throws Exception {
        List<TripEventNotify> list = eventMapper.selectEventNotifies(277);
        //Assert.assertEquals(1, list.size());
    }

    @Test
    public void testeleteEventNotifiesWithEventId() throws Exception {
        eventMapper.deleteEventNotifiesWithEventId(262);
    }

    @Test
    public void testDeleteEventNotifiesWithTripId() throws Exception {
        eventMapper.deleteEventNotifiesWithTripId(492, null);
    }

}
