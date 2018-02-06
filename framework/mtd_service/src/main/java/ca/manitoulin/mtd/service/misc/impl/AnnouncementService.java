package ca.manitoulin.mtd.service.misc.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.manitoulin.mtd.dao.misc.AnnouncementMapper;
import ca.manitoulin.mtd.dto.misc.Announcement;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.service.misc.IAnnouncementService;

@Service
public class AnnouncementService implements IAnnouncementService{
	@Autowired
	private AnnouncementMapper announcementMapper;
	@Override
	public List<Announcement> retrieveAnnouncements(SecureUser loginUser) {
		List<Announcement> announcements =  announcementMapper.selectAnnouncements(loginUser);
		return announcements;
	}
	
}
