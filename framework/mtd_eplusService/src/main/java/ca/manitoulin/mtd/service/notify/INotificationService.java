package ca.manitoulin.mtd.service.notify;

import java.util.Map;

import ca.manitoulin.mtd.code.NotificationType;
import ca.manitoulin.mtd.dto.misc.CommunicationEmail;

public interface INotificationService {
	
	CommunicationEmail notify(Integer departmentId, String[] recipients, NotificationType notificationType, String templateId, String emailSubject, Map<String, Object> args);
	
	void notify(String host, String username, String password, String[] recipients, NotificationType notificationType, String subject, String msgConent);
	
	void sendEmail(CommunicationEmail email) throws Exception;

}
