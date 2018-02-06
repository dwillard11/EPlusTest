package ca.manitoulin.mtd.web.controller.reports;

import ca.manitoulin.mtd.dto.customer.CustomerLocation;
import ca.manitoulin.mtd.dto.operationconsole.Trip;

import ca.manitoulin.mtd.dto.security.Organization;
import ca.manitoulin.mtd.service.reports.IReportService;
import ca.manitoulin.mtd.util.ApplicationSession;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static ca.manitoulin.mtd.constant.ContextConstants.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.parseInt;

@Controller
public class ReportController {
    @Autowired
    private IReportService reportService;

    @RequestMapping(value = "/report/reportManager/getTSAAuthorizedAgentByAirportReporting", method = RequestMethod.GET)
    @ResponseBody
    public Object getTSAAuthorizedAgentByAirportReporting(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tsaAirport = request.getParameter("tsaAirport");
        String address = request.getParameter("address");
        String quickName = request.getParameter("quickName");
        String searchKey = request.getParameter("searchKey");

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        List<CustomerLocation> records = reportService.getTSAAuthorizedAgentByAirportReporting(tsaAirport, address, quickName, searchKey);
        map.put(PARAM_RECORDS, records);
        return map;
    }

    @RequestMapping(value = "/report/reportManager/getTripReport", method = RequestMethod.GET)
    @ResponseBody
    public Object getTripReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        if (StringUtils.isEmpty(request.getParameter("departmentIds"))) {
            map.put(PARAM_RECORDS, Lists.newArrayList());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        Iterable<String> departmentIds = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(request.getParameter("departmentIds"));
        List<String> deptIdStrs = Lists.newArrayList(departmentIds);
        List<Integer> deptIds = newArrayList();
        for (String deptId : deptIdStrs) {
            deptIds.add(parseInt(deptId));
        }

        String tripNum = request.getParameter("tripNum");
        String pickupDate = request.getParameter("pickupDate");
        String deliveryDate = request.getParameter("deliveryDate");
        String shipperName = request.getParameter("shipperName");
        String consigneeName = request.getParameter("consigneeName");
        String podName = request.getParameter("podName");
        String searchKey = request.getParameter("searchKey");

        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        List<Trip> records = reportService.getTripReport(deptIds, tripNum, pickupDate, deliveryDate, shipperName, consigneeName, podName, searchKey);
        map.put(PARAM_RECORDS, records);
        return map;
    }

    @RequestMapping(value = "/report/reportManager/getCurrencyReportByDepartment", method = RequestMethod.GET)
    @ResponseBody
    public Object getCurrencyReportByDepartment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        if (StringUtils.isEmpty(request.getParameter("departmentIds"))) {
            map.put(PARAM_RECORDS, Lists.newArrayList());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        Iterable<String> departmentIds = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(request.getParameter("departmentIds"));
        List<String> deptIdStrs = Lists.newArrayList(departmentIds);
        List<Integer> deptIds = newArrayList();
        for (String deptId : deptIdStrs) {
            deptIds.add(parseInt(deptId));
        }

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String clientName = request.getParameter("clientName");
        String shipperName = request.getParameter("shipperName");
        String consigneeName = request.getParameter("consigneeName");
        String tripNum = request.getParameter("tripNum");
        String tripStatus = request.getParameter("selectedCategory");
        String currency = request.getParameter("currency");
        String searchKey = request.getParameter("searchKey");

        List<Trip> records = reportService.getCurrencyReportByDepartment(deptIds, startDate, endDate, clientName, shipperName, consigneeName, tripNum, tripStatus, currency, searchKey);
        map.put(PARAM_RECORDS, records);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/report/reportManager/getSaleReporting", method = RequestMethod.GET)
    @ResponseBody
    public Object getSaleReporting(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        if (StringUtils.isEmpty(request.getParameter("departmentIds"))) {
            map.put(PARAM_RECORDS, Lists.newArrayList());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        Iterable<String> departmentIds = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(request.getParameter("departmentIds"));
        List<String> deptIdStrs = Lists.newArrayList(departmentIds);
        List<Integer> deptIds = newArrayList();
        for (String deptId : deptIdStrs) {
            deptIds.add(parseInt(deptId));
        }

        String currency = request.getParameter("currency");
        String searchKey = request.getParameter("searchKey");

        map.put(KEY_RESULT, RESPONSE_SUCCESS);

        List<Trip> records = reportService.getSaleReporting(deptIds, currency, searchKey);
        map.put(PARAM_RECORDS, records);
        return map;
    }

    @RequestMapping(value = "/report/reportManager/getServiceReporting", method = RequestMethod.GET)
    @ResponseBody
    public Object getServiceReporting(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        if (StringUtils.isEmpty(request.getParameter("departmentIds"))) {
            map.put("records", Lists.newArrayList());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        Iterable<String> departmentIds = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(request.getParameter("departmentIds"));
        List<String> deptIdStrs = Lists.newArrayList(departmentIds);
        List<Integer> deptIds = newArrayList();
        for (String deptId : deptIdStrs) {
            deptIds.add(parseInt(deptId));
        }

        String consigneeName = request.getParameter("consigneeName");
        String currency = request.getParameter("currency");
        String searchKey = request.getParameter("searchKey");

        map.put(KEY_RESULT, RESPONSE_SUCCESS);

        List<Trip> records = reportService.getServiceReporting(deptIds, consigneeName, currency, searchKey);
        map.put(PARAM_RECORDS, records);
        return map;
    }

    @RequestMapping(value = "/report/reportManager/getCostReporting", method = RequestMethod.GET)
    @ResponseBody
    public Object getCostReporting(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        if (StringUtils.isEmpty(request.getParameter("departmentIds"))) {
            map.put(PARAM_RECORDS, Lists.newArrayList());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        Iterable<String> departmentIds = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(request.getParameter("departmentIds"));
        List<String> deptIdStrs = Lists.newArrayList(departmentIds);
        List<Integer> deptIds = newArrayList();
        for (String deptId : deptIdStrs) {
            deptIds.add(parseInt(deptId));
        }

        String tripNum = request.getParameter("tripNum");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String chargeCode = request.getParameter("chargeCode");
        String chargeDetail = request.getParameter("chargeDetail");
        String currency = request.getParameter("currency");
        String searchKey = request.getParameter("searchKey");

        List<Trip> records = reportService.getCostReporting(deptIds, tripNum, startDate, endDate, chargeCode, chargeDetail, currency, searchKey);
        map.put(PARAM_RECORDS, records);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }
}