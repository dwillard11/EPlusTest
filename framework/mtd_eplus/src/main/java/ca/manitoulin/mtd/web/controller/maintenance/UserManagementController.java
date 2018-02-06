package ca.manitoulin.mtd.web.controller.maintenance;


import ca.manitoulin.mtd.dto.security.Organization;
import ca.manitoulin.mtd.dto.security.Role;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.maintenance.ISystemMaintenanceService;
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
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.arrayToList;

@Controller
public class UserManagementController {
    @Autowired
    private ISystemMaintenanceService systemMaintenanceService;
    @Autowired
    private BusinessCodeController businessCodeController;
    @Autowired
    private DepartmentManagementController departmentManagementController;

    @RequestMapping(value = "/maintenance/userManager/retrieveSecureUsers", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveSecureUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String searchKey = request.getParameter("advanceSearch");
        if (isBlank(searchKey))
            searchKey = "%";
        else
            searchKey = "%"+searchKey+"%";
        List<SecureUser> list = systemMaintenanceService.retrieveSecureUsers(searchKey);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, list);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }


    @RequestMapping(value = "/maintenance/userManager/removeUsers", method = RequestMethod.GET)
    @ResponseBody
    public Object removeUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userIds = request.getParameter("euIds");
        List<String> userIdList = asList(userIds.split(","));
        for (String userId : userIdList) {
            try {
                systemMaintenanceService.removeSecureUser(parseInt(userId));
            } catch (Exception e) {
                continue;
            }
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/userManager/retrieveUserByID", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveUserByID(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (isBlank(id)) {
            // new User
            Map<String, Object> map = newHashMap();
            map.put(PARAM_RECORDS, new SecureUser());
            map.put(PARAM_ROLES_TABLE, getUserRoleTable(new SecureUser()));
            map.put(PARAM_DEPARTMENTS, newArrayList());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        SecureUser secureUser = systemMaintenanceService.retrieveSecureUserByID(parseInt(id));
        List<Map<String, Object>> rolesTable = getUserRoleTable(secureUser);
        List<Organization> userDivisions = systemMaintenanceService.selectDepartmentsByUserID(parseInt(id));
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, secureUser);
        map.put(PARAM_ROLES_TABLE, rolesTable);
        map.put(PARAM_DEPARTMENTS, userDivisions);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/userManager/getEpCodeListByType", method = RequestMethod.GET)
    @ResponseBody
    public Object getEpCodeListByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return businessCodeController.getEpCodeListByType(request, response);
    }

    @RequestMapping(value = "/maintenance/userManager/getDepartmentDropDownList", method = RequestMethod.GET)
    @ResponseBody
    public Object getDepartmentDropDownList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return departmentManagementController.getDepartmentDropDownList(request, response);
    }

    private List<Map<String, Object>> getUserRoleTable(SecureUser secureUser) throws Exception {
        List<Role> userRoles = secureUser.getRoles();
        List<Role> allRoles = systemMaintenanceService.retrieveRoles("");
        List<Map<String, Object>> rolesTable = newArrayList();
        for (Role role : allRoles) {
            if (role.getStatus().equals("Active")) {
                Map<String, Object> roleTableRow = newHashMap();
                roleTableRow.put("id", role.getId());
                roleTableRow.put("name", role.getName());
                roleTableRow.put("description", role.getDescription());
                if (isUserRole(role, userRoles)) {
                    roleTableRow.put("selected", true);
                } else {
                    roleTableRow.put("selected", false);
                }
                rolesTable.add(roleTableRow);
            }
        }
        return rolesTable;
    }

    private boolean isUserRole(Role role,List<Role> userRoles) {
        if (isNotEmpty(userRoles)) {
            for (Role userRole : userRoles) {
                if (userRole.getId() == role.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    @RequestMapping(value = "/maintenance/userManager/updateUser", method = RequestMethod.GET)
    @ResponseBody
    public Object updateUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");

        String roleItems = request.getParameter("roleItems");
        if (isNotBlank(roleItems)) {
            try {
                systemMaintenanceService.deleteUserRole(parseInt(id));
            } catch (Exception e) {

            }
            List<String> roleList = asList(roleItems.split(","));
            for (String roleId : roleList) {
                try {
                    systemMaintenanceService.saveUserRole(parseInt(id), parseInt(roleId));
                } catch (Exception e) {
                    continue;
                }
            }
        }

        String departmentItems = request.getParameter("departmentItems");

        if (isEmpty(departmentItems)) throw new BusinessException("Please select at least one department!");


        try {
            systemMaintenanceService.deleteUserDivision(parseInt(id));
        } catch (Exception e) {

        }
        List<String> departmentList = asList(departmentItems.split(","));
        for (String departmentId : departmentList) {
            try {
                systemMaintenanceService.insertUserDivision(parseInt(id), parseInt(departmentId));
            } catch (Exception e) {
                continue;
            }
        }

        SecureUser secureUser = new SecureUser();
        secureUser.setId(id);
        secureUser.setUid(request.getParameter("uid"));
        secureUser.setCompany(request.getParameter("company"));
        secureUser.setCustomer(request.getParameter("customer"));
        secureUser.setDepId(Integer.parseInt(request.getParameter("department")));
        secureUser.setDefaultCurrency(request.getParameter("defaultcurrency"));
        secureUser.setStatus(request.getParameter("status"));
        systemMaintenanceService.updateSecureUser(secureUser);

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/userManager/addUser", method = RequestMethod.GET)
    @ResponseBody
    public Object addUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SecureUser secureUser = new SecureUser();
        String uid = request.getParameter("uid");
        Map<String, Object> map = newHashMap();
        if (isBlank(uid)) {
            throw new BusinessException("User UID  is empty!");
            //throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{"UID"}), null);
        }

        SecureUser secureUserFromPMTUser = systemMaintenanceService.retrieveSecureUserFromPMTUserByUID(uid);
        if (null == secureUserFromPMTUser) {
            throw new BusinessException("User UID is not found!");
            //throw new BusinessException(LAKEY_ERROR_SEC_USER_NOT_EXIST, arrayToList(new String[]{uid}), null);
        }
        SecureUser secureUserFromEpUser = systemMaintenanceService.retrieveSecureUserByUID(uid);
        if (null != secureUserFromEpUser) {
            throw new BusinessException("User UID has been already created as user, please input a new UID!");
            //throw new BusinessException(LAKEY_ERROR_COMMON_DUPLICATED, arrayToList(new String[]{uid}), null);
        }
        secureUser.setUid(uid);
        secureUser.setCompany(secureUserFromPMTUser.getCompany());
        secureUser.setCustomer(secureUserFromPMTUser.getCustomer());
        secureUser.setDepId(Integer.parseInt(request.getParameter("department")));
        secureUser.setDefaultCurrency(request.getParameter("defaultcurrency"));
        secureUser.setStatus(request.getParameter("status"));
        systemMaintenanceService.addSecureUser(secureUser);


        String accountItems = request.getParameter("accountItems");
        if (isNotBlank(accountItems)) {
            List<String> accountList = asList(accountItems.split(","));
            for (String accountId : accountList) {
                try {
                    // systemMaintenanceService.removeDepartment(parseInt(departId));
                } catch (Exception e) {
                    continue;
                }
            }
        }

        String roleItems = request.getParameter("roleItems");
        if (isNotBlank(roleItems)) {
            List<String> roleList = asList(roleItems.split(","));
            for (String roleId : roleList) {
                try {
                    systemMaintenanceService.saveUserRole(parseInt(secureUser.getId()), parseInt(roleId));
                } catch (Exception e) {
                    continue;
                }
            }
        }

        String departmentItems = request.getParameter("departmentItems");
        if (isNotBlank(departmentItems)) {
            List<String> departmentList = asList(departmentItems.split(","));
            for (String departmentId : departmentList) {
                try {
                    systemMaintenanceService.insertUserDivision(parseInt(secureUser.getId()), parseInt(departmentId));
                } catch (Exception e) {
                    continue;
                }
            }
        }


        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }
}
