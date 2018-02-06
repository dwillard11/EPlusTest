package ca.manitoulin.mtd.web.controller.maintenance;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.security.Organization;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.maintenance.ISystemMaintenanceService;
import ca.manitoulin.mtd.util.ApplicationSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static ca.manitoulin.mtd.constant.ContextConstants.*;
import static ca.manitoulin.mtd.util.ApplicationSession.get;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.arrayToList;

@Controller
public class DepartmentManagementController {

    @Autowired
    private ISystemMaintenanceService systemMaintenanceService;
    @Autowired
    private BusinessCodeController businessCodeController;

    @RequestMapping(value = "/maintenance/departmentManager/retrieveDepartments", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveDepartments(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String advanceSearch = request.getParameter("advanceSearch");
        List<Organization> list = systemMaintenanceService.retrieveDepartments(advanceSearch);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, list);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/departmentManager/getDepartmentDropDownList", method = RequestMethod.GET)
    @ResponseBody
    public Object getDepartmentDropDownList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Organization> list = systemMaintenanceService.retrieveDepartments("");

        List<Map<String, String>> mapList = newArrayList();
        SecureUser user = ApplicationSession.get();
        for (Organization o : list) {
            Map<String, String> map = newHashMap();
            map.put("id", o.getId().toString());
            map.put("name", o.getName());
            mapList.add(map);
        }

        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, mapList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/departmentManager/getDepartmentDropDownListWithAll", method = RequestMethod.GET)
    @ResponseBody
    public Object getDepartmentDropDownListWithAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SecureUser currentUser = get();
        Integer userId = parseInt(currentUser.getId());
        List<Organization> list = systemMaintenanceService.selectDepartmentsByUserID(userId);

        List<Map<String, String>> mapList = newArrayList();
        SecureUser user = ApplicationSession.get();
        if ( user.isAdmin()) {
            Map<String, String> allOption = newHashMap();
            allOption.put("id", "0");
            allOption.put("name", "All");
            mapList.add(allOption);
        }
        for (Organization o : list) {
            Map<String, String> map = newHashMap();
            map.put("id", o.getId().toString());
            map.put("name", o.getName());
            mapList.add(map);
        }

        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, mapList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/departmentManager/updateDepartment", method = RequestMethod.GET)
    @ResponseBody
    public Object updateDepartment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Organization organization = new Organization();
        String[] requiredParams = new String[]{"name", "shortName", "id", "status", "invoiceOfficeLine1", "country", "invoiceOfficeName", "invoiceOfficeSeq", "phoneOffice", "address1", "city"};
        for (String param : requiredParams) {
            if (isBlank(request.getParameter(param))) {
                throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{param}), null);
            }
        }
        organization.setName(request.getParameter("name"));
        organization.setShortName(request.getParameter("shortName"));
        organization.setId(parseInt(request.getParameter("id")));
        organization.setStatus(request.getParameter("status"));
        organization.setCity(request.getParameter("city"));
        organization.setAddress1(request.getParameter("address1"));
        organization.setAddress2(request.getParameter("address2"));
        organization.setPostalCode(request.getParameter("postalCode"));
        if (!isBlank(request.getParameter("province")))
            organization.setProvince(request.getParameter("province"));
        organization.setCountry(request.getParameter("country"));
        organization.setPhoneMobile(request.getParameter("phoneMobile"));
        organization.setPhoneOffice(request.getParameter("phoneOffice"));
        organization.setPhoneFax(request.getParameter("phoneFax"));
        organization.setPhone800(request.getParameter("phone800"));
        organization.setNotes(request.getParameter("notes"));

        organization.setInvoiceOfficeSeq(parseInt(request.getParameter("invoiceOfficeSeq")));
        organization.setInvoiceOfficeName(request.getParameter("invoiceOfficeName"));
        organization.setInvoiceOfficeLine1(request.getParameter("invoiceOfficeLine1"));
        organization.setInvoiceOfficeLine2(request.getParameter("invoiceOfficeLine2"));
        organization.setInvoiceOfficeLine3(request.getParameter("invoiceOfficeLine3"));
        organization.setDefaultCurrency(request.getParameter("defaultCurrency"));
        organization.setDefaultTimezone(request.getParameter("defaultTimezone"));
        organization.setDefaultEmail(request.getParameter("defaultEmail"));
        organization.setMailHost(request.getParameter("mailHost"));
        organization.setMailUserName(request.getParameter("mailUserName"));
        organization.setMailPassword(request.getParameter("mailPassword"));

        systemMaintenanceService.updateDepartment(organization);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/departmentManager/addDepartment", method = RequestMethod.GET)
    @ResponseBody
    public Object addDepartment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Organization organization = new Organization();
        String[] requiredParams = new String[]{"name", "shortName", "status", "invoiceOfficeLine1", "country", "invoiceOfficeName", "invoiceOfficeSeq", "phoneOffice", "address1", "city"};
        for (String param : requiredParams) {
            if (isBlank(request.getParameter(param))) {
                throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{param}), null);
            }
        }
        organization.setName(request.getParameter("name"));
        organization.setShortName(request.getParameter("shortName"));
        organization.setStatus(request.getParameter("status"));
        organization.setCity(request.getParameter("city"));
        organization.setAddress1(request.getParameter("address1"));
        organization.setAddress2(request.getParameter("address2"));
        organization.setPostalCode(request.getParameter("postalCode"));
        if (!isBlank(request.getParameter("province")))
            organization.setProvince(request.getParameter("province"));
        organization.setCountry(request.getParameter("country"));
        organization.setPhoneMobile(request.getParameter("phoneMobile"));
        organization.setPhoneOffice(request.getParameter("phoneOffice"));
        organization.setPhoneFax(request.getParameter("phoneFax"));
        organization.setPhone800(request.getParameter("phone800"));
        organization.setNotes(request.getParameter("notes"));

        organization.setInvoiceOfficeSeq(parseInt(request.getParameter("invoiceOfficeSeq")));
        organization.setInvoiceOfficeName(request.getParameter("invoiceOfficeName"));
        organization.setInvoiceOfficeLine1(request.getParameter("invoiceOfficeLine1"));
        organization.setInvoiceOfficeLine2(request.getParameter("invoiceOfficeLine2"));
        organization.setInvoiceOfficeLine3(request.getParameter("invoiceOfficeLine3"));
        organization.setDefaultCurrency(request.getParameter("defaultCurrency"));
        organization.setDefaultTimezone(request.getParameter("defaultTimezone"));
        organization.setDefaultEmail(request.getParameter("defaultEmail"));
        organization.setMailHost(request.getParameter("mailHost"));
        organization.setMailUserName(request.getParameter("mailUserName"));
        organization.setMailPassword(request.getParameter("mailPassword"));

        systemMaintenanceService.addDepartment(organization);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/departmentManager/removeDepartments", method = RequestMethod.GET)
    @ResponseBody
    public Object removeDepartments(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String departmentIds = request.getParameter("departmentIds");
        List<String> departIdList = asList(departmentIds.split(","));
        for (String departId : departIdList) {
            try {
                systemMaintenanceService.removeDepartment(parseInt(departId));
            } catch (Exception e) {
                continue;
            }
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/departmentManager/retrieveDepartment", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveDepartment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String departId = request.getParameter("id");
        Map<String, Object> map = newHashMap();
        if (isBlank(departId)) {
            // new department;
            map.put(PARAM_RECORDS, new Organization());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }

        try {
            Organization organization = systemMaintenanceService.retrieveDepartmentById(parseInt(departId));
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            map.put(PARAM_RECORDS, organization);
        } catch (Exception e) {
            map.put(KEY_RESULT, RESPONSE_ERROR);
        }
        return map;
    }

    @RequestMapping(value = "/maintenance/departmentManager/getEpCodeListByType", method = RequestMethod.GET)
    @ResponseBody
    public Object getEpCodeListByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return businessCodeController.getEpCodeListByType(request, response);
    }

    @RequestMapping(value = "/maintenance/departmentManager/getCountry", method = RequestMethod.GET)
    @ResponseBody
    public Object getCountry(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return businessCodeController.getCountry(request, response);
    }

    @RequestMapping(value = "/maintenance/departmentManager/getProvinceByCountry", method = RequestMethod.GET)
    @ResponseBody
    public Object getProvinceByCountry(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return businessCodeController.getProvinceByCountry(request, response);
    }

    @RequestMapping(value = "/maintenance/departmentManager/getEntityContactCountry", method = RequestMethod.GET)
    @ResponseBody
    public Object getEntityContactCountry(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return businessCodeController.getEntityContactCountry(request, response);
    }

    @RequestMapping(value = "/maintenance/departmentManager/setDept", method = RequestMethod.GET)
    @ResponseBody
    public Object setDept(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String deptID = request.getParameter("dept");
        SecureUser user = (SecureUser) request.getSession().getAttribute(ContextConstants.SESSION_USERPROFILE);
        user.setDepId(parseInt(deptID));
        for (Organization organization : user.getDepartments()) {
            if (organization.getId().equals(parseInt(deptID))) {
                user.setCurrentDepCode(organization.getShortName());
            }
        }
        Map<String, Object> map = newHashMap();
        HttpSession session = request.getSession();
        session.setAttribute(SESSION_USERPROFILE, user);
        map.put(PARAM_USER, user);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }
}
