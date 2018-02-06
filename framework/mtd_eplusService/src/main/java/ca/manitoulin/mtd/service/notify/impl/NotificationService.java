package ca.manitoulin.mtd.service.notify.impl;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import ca.manitoulin.mtd.code.NotificationType;
import ca.manitoulin.mtd.constant.ApplicationConstants;
import ca.manitoulin.mtd.dto.misc.CommunicationEmail;
import ca.manitoulin.mtd.dto.operationconsole.TripDocument;
import ca.manitoulin.mtd.dto.security.Organization;
import ca.manitoulin.mtd.service.maintenance.ISystemMaintenanceService;
import ca.manitoulin.mtd.service.notify.INotificationService;
import ca.manitoulin.mtd.util.IDocumentManager;

@Service
public class NotificationService implements INotificationService {
	private static final Logger log = Logger.getLogger(NotificationService.class);

	@Resource(name = "velocityEngine")
	private VelocityEngine ve;
	@Resource(name = "javaMailSender")
	private JavaMailSenderImpl mailSender;

	@Autowired
	private IDocumentManager docManager;	
	
	@Autowired
	private ISystemMaintenanceService orgService;
	
	@Override
	public CommunicationEmail notify(Integer departmentId, String[] recipients,
			NotificationType notificationType, String templateId, String emailSubject,
			Map<String, Object> args) {
		String text = null;
		String templateFile = getTemplateFileName(notificationType, templateId);
		try {
			text = VelocityEngineUtils.mergeTemplateIntoString(ve, templateFile + "-content.vm", "utf-8", args);
		} catch (VelocityException e) {
			log.error("Failed to merge template: " + templateFile);
			return null;
		}

		String subject = null;
		if (notificationType == NotificationType.EMAIL) {
			if (emailSubject == null || emailSubject.equals("")) {
				subject = VelocityEngineUtils.mergeTemplateIntoString(ve, templateFile + "-subject.vm", "utf-8", args);
			} else {
				subject = emailSubject;
			}
		}
		//Update on 24 Aug 2017
		//create email object to save the email into EpCommunication
		CommunicationEmail email = new CommunicationEmail();
		email.setContent(text);
		email.setType("Out");
		email.setSubject(subject);
		email.setMailFrom(ApplicationConstants.getConfig("mail.default.from"));
		email.setMailTo(StringUtils.join(recipients, ","));
		email.setReadStatus("N");
		email.setProcessedStatus("Processed");

		//Update on 6/6/2017. Use division settings - begin.
		String emailFrom = "";
		String host = "";
		String username = "";
		String password = "";
		Organization dep = null;
		if(departmentId == null){
			log.warn("DEP ID IS NOT FOUND IN EMAIL ENTITY, USE DEFAULT EMAIL SETTINGS");
		}else{
			try {
				dep = orgService.retrieveDepartmentById(departmentId);
				if(dep != null) {
					emailFrom = dep.getDefaultEmail();
					host = dep.getMailHost();
					username = dep.getMailUserName();
					password = dep.getMailPassword();
					if (StringUtils.isEmpty(host) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
						log.warn("EMAIL HOST INFO IS NOT COMPLETED IN DEP PROFILE, USE DEFAULT EMAIL SETTINGS");
					} else {
						log.info("--> Use department email host settings : " + host);
					}

					if (!StringUtils.isEmpty(emailFrom))
						email.setMailFrom(emailFrom);
				}
			}catch (Exception e) {
				log.error("Failed to get division settings: " + departmentId);
			}
		}
		//Update on 6/6/2017. Use division settings - end.

		notify(host, username, password, recipients, notificationType, subject, text);
		
		return email;
	}
	
	@Override
	@Async
	public void notify(String host, String username, String password, String[] recipients,
			NotificationType notificationType, String subject, String msgConent) {
		if (recipients == null)
			return;
		if (notificationType == NotificationType.EMAIL) {
			for (String recipient : recipients) {
				try {
					sendMimeMail(host, username, password, recipient, subject, msgConent);
				} catch (MessagingException e) {
					log.error("Failed to send email to " + recipient + ", subject = " + subject, e);
				}
			}
		} else if (notificationType == NotificationType.SMS) {
			try {
				//TODO
				//sendSms(recipients, msgConent);
			} catch (Exception e) {
				log.error("Failed to send sms to " + recipients + ", msgConent = " + msgConent, e);
			}
		} else if (notificationType == NotificationType.INTERNAL_MSG) {
			//TODO 
		} else {
			log.error("Nothing to do, Notification Type is not specified!");
		}
	}

	private String getTemplateFileName(NotificationType notificationType, String templateId) {
		if (notificationType == NotificationType.EMAIL)
			return "mail-" + templateId;
		else if (notificationType == NotificationType.SMS)
			return "sms-" + templateId;
		else if (notificationType == NotificationType.INTERNAL_MSG)
			return "msg-" + templateId;
		else
			return null;
	}

	private void sendMimeMail(String host, String username, String password, String recipient,String subject, String content) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		mimeMessage.setHeader("Content-Type", ApplicationConstants.getConfig("mail.default.contentType"));
		MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, ApplicationConstants.getConfig("mail.defaultEncoding"));
		message.setFrom(ApplicationConstants.getConfig("mail.default.from"));

		if(StringUtils.isEmpty(host) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
			log.warn("EMAIL HOST INFO IS NOT COMPLETED IN DEP PROFILE, USE DEFAULT EMAIL SETTINGS");
		}else{
			mailSender.setHost(host);
			mailSender.setUsername(username);
			mailSender.setPassword(password);
			log.info("--> Use department email host settings : " + host );
		}

		message.setSubject(subject);
		message.setTo(recipient);
		message.setText(content, true);
		mailSender.send(mimeMessage);
		log.debug("[SUCCESS] Mail sent to " + recipient + " subjected with " + subject);
	}

	@Override
	//@Async
	public void sendEmail(CommunicationEmail email) throws Exception {
		log.info("--> Begine to send out email");
		
		//Update on 6/6/2017. Use division settings - begin.
		Integer depId = email.getDepartmentId();
		Organization dep = null;
		if(depId == null){
			log.warn("DEP ID IS NOT FOUND IN EMAIL ENTITY, USE DEFAULT EMAIL SETTINGS");
		}else{
			dep = orgService.retrieveDepartmentById(depId);
			String host = dep.getMailHost();
			String username = dep.getMailUserName();
			String password = dep.getMailPassword();
			if(StringUtils.isEmpty(host) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
				log.warn("EMAIL HOST INFO IS NOT COMPLETED IN DEP PROFILE, USE DEFAULT EMAIL SETTINGS");
			}else{
				mailSender.setHost(host);
				mailSender.setUsername(username);
				mailSender.setPassword(password);
				log.info("--> Use department email host settings : " + host );
			}
		}
		//Update on 6/6/2017. Use division settings - end.
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		mimeMessage.setHeader("Content-Type", ApplicationConstants.getConfig("mail.default.contentType"));
		
		MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, ApplicationConstants.getConfig("mail.defaultEncoding"));
		
		//Update on 6/6/2017. Use division settings - begin.
		String mailFrom = null;
		
		if(dep != null && !StringUtils.isEmpty(dep.getDefaultEmail())){
			log.info("--> Use department email address : " + dep.getDefaultEmail() );
			mailFrom = dep.getDefaultEmail();
		}else{
			mailFrom = ApplicationConstants.getConfig("mail.default.from");
		}
		message.setFrom(mailFrom);
		//Update on 6/6/2017. Use division settings - end.
		message.setSubject(email.getSubject());		
		message.setText(email.getContent(), true);
		
		//To
		String[] recipients = StringUtils.split(email.getMailTo(),",");
		for(String recipient : recipients){
			message.addTo(recipient);
		}
		//CC
		if(StringUtils.isEmpty(email.getMailCc()) == false){
			String[] ccs = StringUtils.split(email.getMailCc(), ",");
			for(String cc : ccs){
				message.addCc(cc);
			}
		}
		//BCC
		if(StringUtils.isEmpty(email.getMailBcc()) == false){
			String[] ccs = StringUtils.split(email.getMailBcc(), ",");
			for(String cc : ccs){
				message.addBcc(cc);
			}
		}
		
		//attachment
		if(email.getAttachments() != null){
			for(TripDocument attachment : email.getAttachments()){
				try {
					if (email.getTripId() != null) {
						//--> Wait. All uploads saved in generic upload folder. Before sending email, it will be moved to the email folder first.
						message.addAttachment(attachment.getOriginalFileName(), docManager.getDocument(email.getTripId(), attachment.getFilename()));
					}
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
		
		mailSender.send(mimeMessage);
		log.info("[SUCCESS] Mail sent to " + StringUtils.join(recipients,",") + " subjected with " + email.getSubject());
	}

}
