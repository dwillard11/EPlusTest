package ca.manitoulin.mtd.service.misc;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.manitoulin.mtd.dto.misc.Announcement;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.service.misc.impl.AnnouncementService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-ws.xml" })
public class AnnouncementMapperTest {
	static private Logger log = getLogger(AnnouncementMapperTest.class);

	@Autowired
	private AnnouncementService announcementService;
	@Test
	public void testSelectAnnouncements() {
		SecureUser user = new SecureUser();
		user.setReferLanguage("English");
		List<Announcement> announcements = announcementService.retrieveAnnouncements(user);
		for (Announcement announcement : announcements) {
			log.debug(announcement);
		}
	}

}
