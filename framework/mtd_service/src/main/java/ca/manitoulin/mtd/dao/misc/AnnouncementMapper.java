package ca.manitoulin.mtd.dao.misc;

import java.util.List;

import ca.manitoulin.mtd.dto.misc.Announcement;
import ca.manitoulin.mtd.dto.security.SecureUser;

public interface AnnouncementMapper {
	List<Announcement> selectAnnouncements(SecureUser loginUser);
}
