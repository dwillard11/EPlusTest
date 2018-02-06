package ca.manitoulin.mtd.web.controller.maintenance;

import ca.manitoulin.mtd.dto.security.Role;
import ca.manitoulin.mtd.dto.security.RoleAccessRight;
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
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Controller
public class RoleManagementController {

    @Autowired
    private ISystemMaintenanceService systemMaintenanceService;
    @Autowired
    private BusinessCodeController businessCodeController;

    @RequestMapping(value = "/maintenance/roleManager/retrieveRoles", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveRoles(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String advanceSearch = request.getParameter("advanceSearch");
        List<Role> list = systemMaintenanceService.retrieveRoles(advanceSearch);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, list);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/roleManager/updateRole", method = RequestMethod.GET)
    @ResponseBody
    public Object updateRole(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Role role = new Role();
        role.setName(request.getParameter("name"));
        role.setId(parseInt(request.getParameter("id")));
        role.setDescription(request.getParameter("description"));
        role.setSystemDefault(Boolean.parseBoolean(request.getParameter("systemDefault")));
        role.setStatus(request.getParameter("status"));
        role.setAccessRightIds(request.getParameter("accessRightIds"));

        systemMaintenanceService.updateRole(role);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/roleManager/addRole", method = RequestMethod.GET)
    @ResponseBody
    public Object addRole(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Role role = new Role();
        role.setName(request.getParameter("name"));
        role.setDescription(request.getParameter("description"));
        role.setSystemDefault(Boolean.parseBoolean(request.getParameter("systemDefault")));
        role.setStatus(request.getParameter("status"));
        role.setAccessRightIds(request.getParameter("accessRightIds"));

        systemMaintenanceService.addRole(role);// messageCentralService.retrieveMessages(advanceSearch);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/roleManager/removeRole", method = RequestMethod.GET)
    @ResponseBody
    public Object removeRoles(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String roleIds = request.getParameter("roleIds");
        List<String> roleIdList = asList(roleIds.split(","));
        for (String roleId : roleIdList) {
            try {
                systemMaintenanceService.removeRole(parseInt(roleId));
            } catch (Exception e) {
                continue;
            }
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/roleManager/retrieveRole", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveRole(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String roleId = request.getParameter("id");
        Map<String, Object> map = newHashMap();
        if(isBlank(roleId)) {
            Role role = new Role();
            List<RoleAccessRight> roleAccessRights = systemMaintenanceService.retrieveRoleAccessRights(0);
            role.setRoleAccessRights(roleAccessRights);
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            map.put(PARAM_RECORDS, role);
            return map;
        }

        try {
            Role role = systemMaintenanceService.retrieveRoleById(parseInt(roleId));
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            map.put(PARAM_RECORDS, role);
        } catch (Exception e) {
            map.put(KEY_RESULT, RESPONSE_ERROR);
        }
        return map;
    }

    @RequestMapping(value = "/maintenance/roleManager/getEpCodeListByType", method = RequestMethod.GET)
    @ResponseBody
    public Object getEpCodeListByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return businessCodeController.getEpCodeListByType(request, response);
    }
}
