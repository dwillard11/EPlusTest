package ca.manitoulin.mtd.web.controller.misc;

import static ca.manitoulin.mtd.constant.ContextConstants.KEY_RESULT;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_RECORDS;
import static ca.manitoulin.mtd.constant.ContextConstants.RESPONSE_SUCCESS;
import static ca.manitoulin.mtd.util.ApplicationSession.get;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.manitoulin.mtd.dto.misc.Announcement;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.service.misc.IAnnouncementService;

@Controller
public class AnnouncementController {

	@Autowired
	private IAnnouncementService announcementService;

	@RequestMapping(value = "/retrieveAnnouncements", method = GET)
	@ResponseBody
	public Object retrieveAnnouncements(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SecureUser sessionUser = get();
		List<Announcement> list = announcementService.retrieveAnnouncements(sessionUser);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PARAM_RECORDS, list);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}

	
}