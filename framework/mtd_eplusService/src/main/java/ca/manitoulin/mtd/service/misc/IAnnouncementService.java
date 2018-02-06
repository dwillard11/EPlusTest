package ca.manitoulin.mtd.service.misc;

import java.util.List;

import ca.manitoulin.mtd.dto.misc.Announcement;
import ca.manitoulin.mtd.dto.security.SecureUser;

public interface IAnnouncementService {
	List<Announcement> retrieveAnnouncements(SecureUser loginUser);
}
