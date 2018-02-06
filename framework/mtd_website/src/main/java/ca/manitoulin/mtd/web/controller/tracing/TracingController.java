package ca.manitoulin.mtd.web.controller.tracing;

import static ca.manitoulin.mtd.constant.ContextConstants.KEY_RESULT;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_BOL_SHIPPER_PO_NEED_VALUE;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_NO_MATCH_FOUND_FOR_BOL;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_NO_MATCH_FOUND_FOR_PICKUPNUMBER;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_NO_MATCH_FOUND_FOR_PURCHASEORDERNUMBER;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_NO_MATCH_FOUND_FOR_SHIPPERNUMBER;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_NO_RECORDS_FOUND;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_PLEASE_SPECIFY_DATE_RANGE;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_PROBILLNUMBER_MUST_BE_NUMBERIC;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_PROBILLNUMBER_NOT_FOUND;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_SPECIFY_DATA_RANGE_WITHIN_FOUR_MONTH;
import static ca.manitoulin.mtd.constant.ContextConstants.LAKEY_STARTING_DATE_SHOULD_BE_EARLIER_THAN_ENDING_DATE;
import static ca.manitoulin.mtd.constant.ContextConstants.LENGTH_OF_PROBILLNO;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_BEGIN_DATE;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_END_DATE;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_NUMBER_TYPE_BOL;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_NUMBER_TYPE_POL;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_NUMBER_TYPE_SHIPPER;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_RECORDS;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_RECORDS_EXTRA;
import static ca.manitoulin.mtd.constant.ContextConstants.RESPONSE_SUCCESS;
import static ca.manitoulin.mtd.util.DateUtil.LONG_DATE_FORMAT_SHOW;
import static ca.manitoulin.mtd.util.DateUtil.toDate;
import static ca.manitoulin.mtd.util.DateUtil.toDateString;
import static java.util.Calendar.MONTH;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.manitoulin.mtd.dto.tracing.PickupStatusDetail;
import ca.manitoulin.mtd.dto.tracing.Probill;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.tracing.ITracingService;

@Controller
public class TracingController {

	@Autowired
	private ITracingService tracingService;

	@RequestMapping(value = "/retrieveProbillByNumber", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieveProbillByNumber(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		String probillNo = request.getParameter("probillNo");
		String accountType = request.getParameter("accountType");
		
		if (!validateProbillNo(probillNo)) {			
			throw new BusinessException(LAKEY_PROBILLNUMBER_MUST_BE_NUMBERIC,null,null);
		}
		
		Probill probill = tracingService.retrieveProbillByNumber(probillNo,accountType);
		if (null == probill) {
			throw new BusinessException(LAKEY_PROBILLNUMBER_NOT_FOUND,null,null);

		}
		map.put(PARAM_RECORDS, probill);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}

	private boolean validateProbillNo(String probillNo) {
		try {
			if (NumberUtils.isNumber(probillNo)) {
				if (LENGTH_OF_PROBILLNO >= probillNo.length()) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	@RequestMapping(value = "/retrieveProbillByPickupNumber", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieveProbillByPickupNumber(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String pickupNumber = request.getParameter("pickupNumber");
		
		Probill probill = tracingService.retrieveProbillByPickupNumber(pickupNumber);
		Map<String, Object> map = new HashMap<String, Object>();
		if (null == probill) {
			throw new BusinessException(LAKEY_NO_MATCH_FOUND_FOR_PICKUPNUMBER,null,null);
		}
		List<PickupStatusDetail> pickupStatusDetailList = tracingService.retrieveStatusDetailsByPickupNumber(pickupNumber);
		
		map.put(PARAM_RECORDS, probill);
		map.put(PARAM_RECORDS_EXTRA, pickupStatusDetailList);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;

	}

	@RequestMapping(value = "/retrieveProbillByByBolShipperPo", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieveProbillByByBolShipperPo(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		String numberValue = request.getParameter("numberValue");
		if (StringUtils.isBlank(numberValue)) {
			throw new BusinessException(LAKEY_BOL_SHIPPER_PO_NEED_VALUE,null,null);

		}
		String numberType = request.getParameter("numberType");
		String accountType = request.getParameter("accountType");
		Probill probill = new Probill();
		if (PARAM_NUMBER_TYPE_BOL.equals(numberType)) {
			probill = tracingService.retrieveProbillByBoL(numberValue,accountType);
			if (null == probill) {
				throw new BusinessException(LAKEY_NO_MATCH_FOUND_FOR_BOL,null,null);
			}
		} else if (PARAM_NUMBER_TYPE_SHIPPER.equals(numberType)) {
			probill = tracingService.retrieveProbillByShipper(numberValue,accountType);
			if (null == probill) {
				throw new BusinessException(LAKEY_NO_MATCH_FOUND_FOR_SHIPPERNUMBER,null,null);
			}
		} else if (PARAM_NUMBER_TYPE_POL.equals(numberType)) {
			probill = tracingService.retrieveProbillByPO(numberValue,accountType);
			if (null == probill) {
				throw new BusinessException(LAKEY_NO_MATCH_FOUND_FOR_PURCHASEORDERNUMBER,null,null);
			}
		}
	
		
		map.put(PARAM_RECORDS, probill);
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}
	
	
	@RequestMapping(value = "/retrieveProbillsByPickupDate", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieveProbillsByPickupDate(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		Date beginDate = new Date();
		Date endDate = new Date();
		if (isNotBlank(request.getParameter("beginDate"))) {
			beginDate = toDate(request.getParameter("beginDate").trim(), LONG_DATE_FORMAT_SHOW);
		} else {
			beginDate = null;
		}
		if (isNotBlank(request.getParameter("endDate"))) {
			endDate = toDate(request.getParameter("endDate").trim(), LONG_DATE_FORMAT_SHOW);
		} else {
			endDate = null;
		}
		
		// beginDate and endDate should be at least one not null
		if (null == beginDate && null == endDate) {
			throw new BusinessException(LAKEY_PLEASE_SPECIFY_DATE_RANGE,null,null);
		}
		
		// if one of beginDate and endDate is null, the null param should be set as current Date
		if (null == beginDate) {
			beginDate = new Date();
		}
		if (null == endDate) {
			endDate = new Date();
		}
		if (beginDate.after(endDate)) {
			throw new BusinessException(LAKEY_STARTING_DATE_SHOULD_BE_EARLIER_THAN_ENDING_DATE,null,null);
		}
		
		// search range should be within one month
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(beginDate);
		calendar.add(MONTH, 4);
		Date dateOneMonth = calendar.getTime();
		if (endDate.after(dateOneMonth)) {
			throw new BusinessException(LAKEY_SPECIFY_DATA_RANGE_WITHIN_FOUR_MONTH,null,null);
		}
		String accountType = request.getParameter("accountType");
		List<Probill> probills = tracingService.retrieveProbillsByPickupDate(beginDate,endDate,accountType);
		
		if (null == probills || 0 == probills.size()) {
			throw new BusinessException(LAKEY_NO_RECORDS_FOUND,null,null);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PARAM_RECORDS, probills);
		map.put(PARAM_BEGIN_DATE, toDateString(beginDate,LONG_DATE_FORMAT_SHOW));
		map.put(PARAM_END_DATE, toDateString(endDate,LONG_DATE_FORMAT_SHOW));
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}
	
	@RequestMapping(value = "/retrieveProbillsByDeliveryDate", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieveProbillsByDeliveryDate(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		Date beginDate = new Date();
		Date endDate = new Date();
		if (isNotBlank(request.getParameter("beginDate"))) {
			beginDate = toDate(request.getParameter("beginDate").trim(), LONG_DATE_FORMAT_SHOW);
		} else {
			beginDate = null;
		}
		if (isNotBlank(request.getParameter("endDate"))) {
			endDate = toDate(request.getParameter("endDate").trim(), LONG_DATE_FORMAT_SHOW);
		} else {
			endDate = null;
		}
		
		// beginDate and endDate should be at least one not null
		if (null == beginDate && null == endDate) {
			throw new BusinessException(LAKEY_PLEASE_SPECIFY_DATE_RANGE,null,null);
		}
		
		// if one of beginDate and endDate is null, the null param should be set as current Date
		if (null == beginDate) {
			beginDate = new Date();
		}
		if (null == endDate) {
			endDate = new Date();
		}
		if (beginDate.after(endDate)) {
			throw new BusinessException(LAKEY_STARTING_DATE_SHOULD_BE_EARLIER_THAN_ENDING_DATE,null,null);
		}
		
		// search range should be within one month
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(beginDate);
		calendar.add(MONTH, 4);
		Date dateOneMonth = calendar.getTime();
		if (endDate.after(dateOneMonth)) {
			throw new BusinessException(LAKEY_SPECIFY_DATA_RANGE_WITHIN_FOUR_MONTH,null,null);
		}
		String accountType = request.getParameter("accountType");
		List<Probill> probills = tracingService.retrieveProbillsByDeliveryDate(beginDate,endDate,accountType);
		
		if (null == probills || 0 == probills.size()) {
			throw new BusinessException(LAKEY_NO_RECORDS_FOUND,null,null);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PARAM_RECORDS, probills);
		map.put(PARAM_BEGIN_DATE, toDateString(beginDate,LONG_DATE_FORMAT_SHOW));
		map.put(PARAM_END_DATE, toDateString(endDate,LONG_DATE_FORMAT_SHOW));
		map.put(KEY_RESULT, RESPONSE_SUCCESS);
		return map;
	}
	
}