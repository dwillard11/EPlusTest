package ca.manitoulin.mtd.web.controller.dashboard;

import ca.manitoulin.mtd.dto.dashboard.ShipmentSummary;
import ca.manitoulin.mtd.dto.dashboard.ShipmentSummaryGrid;
import ca.manitoulin.mtd.dto.security.Role;
import ca.manitoulin.mtd.service.dashboard.IDepartmentSummaryService;
import ca.manitoulin.mtd.service.security.ISecurityProfileService;
import ca.manitoulin.mtd.web.controller.maintenance.DepartmentManagementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ca.manitoulin.mtd.constant.ContextConstants.*;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Controller
public class DashboardController {
    @Autowired
    private IDepartmentSummaryService departmentSummaryService;

    @Autowired
    private DepartmentManagementController departmentManagementController;
    @Autowired
    private ISecurityProfileService securityProfileService;

    @RequestMapping(value = "/dashboard/departSummary/getDepartmentDropDownList", method = RequestMethod.GET)
    @ResponseBody
    public Object getDepartmentDropDownList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return departmentManagementController.getDepartmentDropDownList(request, response);
    }

    @RequestMapping(value = "/dashboard/departSummary/getDepartmentDropDownListWithAll", method = RequestMethod.GET)
    @ResponseBody
    public Object getDepartmentDropDownListWithAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return departmentManagementController.getDepartmentDropDownListWithAll(request, response);
    }

    @RequestMapping(value = "/dashboard/departSummary/retrieveShipmentForDashboard", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveShipmentForDashboardWithCurrency(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String departmentId = request.getParameter("departmentId");
        String startDate =request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        List<ShipmentSummaryGrid> temp = departmentSummaryService.retrieveShipmentForDashboardWithCurrency(parseInt(departmentId), startDate,endDate);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, temp);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/dashboard/departSummary/retrieveQuoteForDashboard", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveQuoteForDashboard(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String departmentId = request.getParameter("departmentId");
        String startDate =request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        ShipmentSummary temp = departmentSummaryService.retrieveQuoteForDashboard(parseInt(departmentId), startDate,endDate);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, temp);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/dashboard/departSummary/retrieveNumberOfOnlineUsers", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveNumberOfOnlineUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String departmentId = request.getParameter("departmentId");
        String activeDate = request.getParameter("startDate");
        if (isBlank(request.getParameter(activeDate)))
        {
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            activeDate = formatDate.format((new Date().getTime() - 30*60*1000));
        }
        List<Role> roles = departmentSummaryService.retrieveOnlineUserCount(parseInt(departmentId), activeDate);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        map.put(PARAM_RECORDS, roles);
        return map;
    }

    @RequestMapping(value = "/dashboard/departSummary/retrieveCriticalShipment", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveCriticalShipment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String departmentId = request.getParameter("departmentId");
        String criticalShipmentFlag = departmentSummaryService.retrieveCriticalShipment(parseInt(departmentId));
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, criticalShipmentFlag);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/dashboard/departSummary/retrieveMessageQueue", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveMessageQueue(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String departmentId = request.getParameter("departmentId");
        ShipmentSummary temp = departmentSummaryService.retrieveMessageQueue(parseInt(departmentId));
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        map.put(PARAM_RECORDS, temp);
        return map;
    }
}
