package ca.manitoulin.mtd.web.controller.messagecentral;

import static ca.manitoulin.mtd.constant.ContextConstants.KEY_RESULT;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_RECORDS;
import static ca.manitoulin.mtd.constant.ContextConstants.RESPONSE_SUCCESS;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.manitoulin.mtd.dto.massagecentral.Message;
import ca.manitoulin.mtd.service.messagecentral.IMessageCentralService;

@Controller
public class MessageController {

	@Autowired
	private IMessageCentralService messageCentralService;

	@RequestMapping(value = "/retrieveMessages", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieveMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String advanceSearch = request.getParameter("advanceSearch");
		List<Message> list = messageCentralService.retrieveMessages(advanceSearch);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PARAM_RECORDS, list);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}

	@RequestMapping(value = "/retrieveMessage", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieveMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String messageId = request.getParameter("messageId");
		Message message = messageCentralService.retrieveMessage(messageId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PARAM_RECORDS, message);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}

	@RequestMapping(value = "/removeMessages", method = RequestMethod.GET)
	@ResponseBody
	public Object removeMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String messageIds = request.getParameter("messageIds");
		List<String> messageList = Arrays.asList(messageIds.split(","));
		messageCentralService.removeMessages(messageList);
		Map<String, Object> map = new HashMap<String, Object>();

		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}

}