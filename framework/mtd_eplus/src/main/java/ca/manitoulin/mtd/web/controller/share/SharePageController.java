package ca.manitoulin.mtd.web.controller.share;

import static ca.manitoulin.mtd.constant.ApplicationConstants.getConfig;
import static ca.manitoulin.mtd.constant.ContextConstants.KEY_RESULT;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_RECORDS;
import static ca.manitoulin.mtd.constant.ContextConstants.RESPONSE_SUCCESS;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.manitoulin.mtd.service.share.IShareURLService;

@Controller
public class SharePageController {

	@Autowired
	private IShareURLService shareURLService;

	@RequestMapping(value = "/generateShareProbillDetailsURL", method = RequestMethod.GET)
	@ResponseBody
	public Object generateShareProbillDetailsURL(HttpServletRequest request, HttpServletResponse response) {
		String base = getConfig("url.base");
		String destination = "/index.html#/app/tracing/resultforprobill";
		String probillId = request.getParameter("probillNo");
		Map<String, String> params = new HashMap<>();
		params.put("isShare", "Y");
		params.put("id", probillId);
		
		String url = shareURLService.generateShareURL(base, destination, params);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(PARAM_RECORDS, url);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);

		return map;
	}
	
	@RequestMapping(value = "/generateShareMessageDetailsURL", method = RequestMethod.GET)
	@ResponseBody
	public Object generateShareMessageDetailsURL(HttpServletRequest request, HttpServletResponse response) {
		String base = getConfig("url.base");
		String destination = "/index.html#/app/messagecentral/messagedetails";
		String messageId = request.getParameter("messageId");
		Map<String, String> params = new HashMap<>();
		params.put("isShare", "Y");
		params.put("id", messageId);
		
		String url = shareURLService.generateShareURL(base, destination, params);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(PARAM_RECORDS, url);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);

		return map;
	}
}
