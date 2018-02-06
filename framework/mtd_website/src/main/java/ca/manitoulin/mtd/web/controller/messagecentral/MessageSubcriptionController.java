package ca.manitoulin.mtd.web.controller.messagecentral;

import static ca.manitoulin.mtd.constant.ContextConstants.*;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_PLEASE_SELECT_ONLY_ONE_ACTIVITY_NOTIFICATION;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_PROBILLNUMBER_MUST_BE_NUMBERIC;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_YOU_APPEAR_TO_HAVE_AN_INVALID_EMAIL_ADDRESS_ENTERED;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_YOU_MUST_ENTER_AN_EMAIL_ADDRESS;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_YOU_MUST_SELECT_AN_OPTION_FROM_ACTIVITY_NOTIFICATION;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_YOU_MUST_SELECT_A_METHOD_OF_NOTIFICATION;
import static ca.manitoulin.mtd.constant.ContextConstants.NOTIFICATION_BOTH_EMAIL_MESSAGECENTRAL;
import static ca.manitoulin.mtd.constant.ContextConstants.NOTIFICATION_EMAIL;
import static ca.manitoulin.mtd.constant.ContextConstants.NOTIFICATION_MESSAGECENTRAL;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_RECORDS;
import static ca.manitoulin.mtd.constant.ContextConstants.RESPONSE_SUCCESS;
import static ca.manitoulin.mtd.constant.ContextConstants.TRIGGER_DELIVERY_STATUS;
import static ca.manitoulin.mtd.constant.ContextConstants.TRIGGER_RESCHEDULED_STATUS;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.manitoulin.mtd.dto.massagecentral.MessageSubscription;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.messagecentral.impl.MessageCentralService;
import static ca.manitoulin.mtd.util.DateUtil.*;

@Controller
public class MessageSubcriptionController {

	@Autowired
	private MessageCentralService messageCentralService;

	@RequestMapping(value = "/retrieveMessageSubscriptions", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieveMessageSubscriptions(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<MessageSubscription> list = messageCentralService.retrieveMessageSubscriptions();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PARAM_RECORDS, list);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}

	@RequestMapping(value = "/createMessageSubscription", method = RequestMethod.GET)
	@ResponseBody
	public Object createMessageSubscription(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MessageSubscription messageSubscription = new MessageSubscription();
		List<String> trigger = new ArrayList<String>();
		boolean noExpiry = Boolean.parseBoolean(request.getParameter("noExpiry"));
		boolean rescheduledStatus = Boolean.parseBoolean(request.getParameter("rescheduledStatus"));
		boolean deliveryStatus = Boolean.parseBoolean(request.getParameter("deliveryStatus"));
		boolean emailChecked = Boolean.parseBoolean(request.getParameter("emailChecked"));
		String emailValue = request.getParameter("emailValue");
		boolean messageCentralChecked = Boolean.parseBoolean(request.getParameter("messageCentralChecked"));
		String probillNo = request.getParameter("probillNo");

		if (isNotBlank(probillNo) && !isNumeric(probillNo)) {
			throw new BusinessException(LAKEY_PROBILLNUMBER_MUST_BE_NUMBERIC, null, null);
		} else {
			messageSubscription.setProbillNo(probillNo);
		}
		if (noExpiry) {
			messageSubscription.setExpiryDate(null);
		} else {
			if (isNotBlank(request.getParameter("expiryDate"))) {
				Date expiryDate = toDate(request.getParameter("expiryDate").trim(), LONG_DATE_FORMAT_SHOW);
				messageSubscription.setExpiryDate(expiryDate);
			}
		}
		if (rescheduledStatus) {
			trigger.add(TRIGGER_RESCHEDULED_STATUS);
		}
		if (deliveryStatus) {
			trigger.add(TRIGGER_DELIVERY_STATUS);
		}
		messageSubscription.setTrigger(trigger);

		if (emailChecked && isBlank(emailValue)) {
			throw new BusinessException(LAKEY_YOU_MUST_ENTER_AN_EMAIL_ADDRESS, null, null);
		}
		if (isNotBlank(emailValue)) {
			try {
				new InternetAddress(emailValue).validate();
				messageSubscription.setEmail(emailValue);
			} catch (AddressException e) {
				throw new BusinessException(LAKEY_YOU_APPEAR_TO_HAVE_AN_INVALID_EMAIL_ADDRESS_ENTERED, null, null);
			}
		}
		if (!rescheduledStatus && !deliveryStatus) {
			throw new BusinessException(LAKEY_YOU_MUST_SELECT_AN_OPTION_FROM_ACTIVITY_NOTIFICATION, null, null);
		}
		if (emailChecked && messageCentralChecked) {
			messageSubscription.setNotification(NOTIFICATION_BOTH_EMAIL_MESSAGECENTRAL);
		} else if (emailChecked) {
			messageSubscription.setNotification(NOTIFICATION_EMAIL);
		} else if (messageCentralChecked) {
			messageSubscription.setNotification(NOTIFICATION_MESSAGECENTRAL);
		} else {
			throw new BusinessException(LAKEY_YOU_MUST_SELECT_A_METHOD_OF_NOTIFICATION, null, null);
		}
		messageCentralService.createMessageSubscription(messageSubscription);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}

	@RequestMapping(value = "/updateSubscription", method = RequestMethod.GET)
	@ResponseBody
	public Object updateSubscription(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MessageSubscription messageSubscription = new MessageSubscription();
		String id = request.getParameter("id");
		messageSubscription.setId(id.trim());
		boolean noExpiry = Boolean.parseBoolean(request.getParameter("noExpiry"));
		boolean rescheduledStatus = Boolean.parseBoolean(request.getParameter("rescheduledStatus"));
		boolean deliveryStatus = Boolean.parseBoolean(request.getParameter("deliveryStatus"));
		boolean emailChecked = Boolean.parseBoolean(request.getParameter("emailChecked"));
		String emailValue = request.getParameter("emailValue");
		boolean messageCentralChecked = Boolean.parseBoolean(request.getParameter("messageCentralChecked"));
		String probillNo = request.getParameter("probillNo");


		if (isNotBlank(probillNo) && !isNumeric(probillNo.trim())) {
			throw new BusinessException(LAKEY_PROBILLNUMBER_MUST_BE_NUMBERIC, null, null);
		} else {
			messageSubscription.setProbillNo(probillNo);
		}

		if (noExpiry) {
			messageSubscription.setExpiryDate(null);
		} else {
			if (isNotBlank(request.getParameter("expiryDate"))) {
				Date expiryDate = toDate(request.getParameter("expiryDate").trim(), LONG_DATE_FORMAT_SHOW);
				messageSubscription.setExpiryDate(expiryDate);
			}
		}
		if (rescheduledStatus && deliveryStatus) {
			throw new BusinessException(LAKEY_PLEASE_SELECT_ONLY_ONE_ACTIVITY_NOTIFICATION, null, null);
		}

		if (rescheduledStatus) {
			messageSubscription.setActivityCode(TRIGGER_RESCHEDULED_STATUS);
		}
		if (deliveryStatus) {
			messageSubscription.setActivityCode(TRIGGER_DELIVERY_STATUS);
		}

		if (emailChecked && isBlank(emailValue)) {
			throw new BusinessException(LAKEY_YOU_MUST_ENTER_AN_EMAIL_ADDRESS, null, null);
		}

		if (isNotBlank(emailValue)) {
			try {
				new InternetAddress(emailValue).validate();
				messageSubscription.setEmail(emailValue.trim());
			} catch (AddressException e) {
				throw new BusinessException(LAKEY_YOU_APPEAR_TO_HAVE_AN_INVALID_EMAIL_ADDRESS_ENTERED, null, null);
			}
		}

		if (!rescheduledStatus && !deliveryStatus) {
			throw new BusinessException(LAKEY_YOU_MUST_SELECT_AN_OPTION_FROM_ACTIVITY_NOTIFICATION, null, null);
		}

		if (emailChecked && messageCentralChecked) {
			messageSubscription.setNotification(NOTIFICATION_BOTH_EMAIL_MESSAGECENTRAL);
		} else if (emailChecked) {
			messageSubscription.setNotification(NOTIFICATION_EMAIL);
		} else if (messageCentralChecked) {
			messageSubscription.setNotification(NOTIFICATION_MESSAGECENTRAL);
		} else {
			throw new BusinessException(LAKEY_YOU_MUST_SELECT_A_METHOD_OF_NOTIFICATION, null, null);
		}

		messageCentralService.updateMessageSubscription(messageSubscription);
		Map<String, Object> map = new HashMap<String, Object>();

		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}

	@RequestMapping(value = "/removeMessageSubscriptions", method = RequestMethod.GET)
	@ResponseBody
	public Object removeMessageSubscriptions(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String messageSubscriptionIds = request.getParameter("messageSubscriptionIds");
		List<String> messageSubscriptionIdList = Arrays.asList(messageSubscriptionIds.split(","));
		messageCentralService.removeMessageSubscriptions(messageSubscriptionIdList);
		Map<String, Object> map = new HashMap<String, Object>();

		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}
}
