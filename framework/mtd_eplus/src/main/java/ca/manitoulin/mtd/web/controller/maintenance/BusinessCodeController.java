package ca.manitoulin.mtd.web.controller.maintenance;

import ca.manitoulin.mtd.dto.maintenance.EmailLabelCode;
import ca.manitoulin.mtd.dto.support.AppEnum;
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

@Controller
public class BusinessCodeController {
    @Autowired
    private ISystemMaintenanceService systemMaintenanceService;

    @RequestMapping(value = "/maintenance/epCodeManager/getEmailLabelList", method = RequestMethod.GET)
    @ResponseBody
    public Object getEmailLabelList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<AppEnum> list = systemMaintenanceService.getEpCodeListByType(request.getParameter("type"));
        List<EmailLabelCode> mapList = newArrayList();
        String desc = "";
        String backColor = "";
        String fontColor = "";
        for (AppEnum s : list) {
            EmailLabelCode map = new EmailLabelCode();
            desc = s.getRemarks();
            if(desc.indexOf(",") != -1){
                backColor = desc.substring(0, desc.indexOf(",")).trim();
                fontColor = desc.substring(desc.indexOf(",") + 1, desc.length()).trim();
            }else{
                backColor = "#FFF";
                fontColor = "#000";
            }

            map.setId(s.getCode());
            map.setName(s.getLabel());
            map.setBackColor(backColor);
            map.setFontColor(fontColor);
            mapList.add(map);
        }
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, mapList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/epCodeManager/getEpCodeListByType", method = RequestMethod.GET)
    @ResponseBody
    public Object getEpCodeListByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<AppEnum> list = systemMaintenanceService.getEpCodeListByType(request.getParameter("type"));
        List<Map<String, String>> mapList = newArrayList();
        for (AppEnum s : list) {
            Map<String, String> map = newHashMap();
            map.put("id", s.getCode());
            map.put("name", s.getLabel());
            mapList.add(map);
        }
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, mapList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/epCodeManager/getEpCodeById", method = RequestMethod.GET)
    @ResponseBody
    public Object getEpCodeById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AppEnum code = systemMaintenanceService.getEpCodeById(request.getParameter("id"));

        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, code);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }
    @RequestMapping(value = "/maintenance/epCodeManager/retrieveEpCodeType", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveEpCodeType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> list = systemMaintenanceService.retrieveCodeCategory();
        List<Map<String, String>> mapList = newArrayList();
        for (String s : list) {
            Map<String, String> map = newHashMap();
            map.put("id", s);
            map.put("name", s);
            mapList.add(map);
        }
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, mapList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/epCodeManager/retrieveBusinessCode", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveBusinessCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<AppEnum> list = systemMaintenanceService.retrieveBusinessCode(request.getParameter("searchType"));
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, list);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }


    @RequestMapping(value = "/maintenance/epCodeManager/addBusinessCode", method = RequestMethod.POST)
    @ResponseBody
    public Object addBusinessCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AppEnum appEnum = new AppEnum();
        appEnum.setCategoryCode(request.getParameter("categoryCode"));
        appEnum.setCode(request.getParameter("code"));
        //appEnum.setLabel(request.getParameter("label"));
        appEnum.setLabel_english(request.getParameter("label_english"));
        appEnum.setLabel_french(request.getParameter("label_french"));
        appEnum.setLabel_chinese(request.getParameter("label_chinese"));
        appEnum.setLabel_german(request.getParameter("label_german"));
        appEnum.setLabel_spanish(request.getParameter("label_spanish"));
        appEnum.setRemarks(request.getParameter("remarks"));
        appEnum.setSortingOrder(parseInt(request.getParameter("sortingOrder")));
        appEnum.setStatus(request.getParameter("status"));
        
        systemMaintenanceService.addBusinessCode(appEnum);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, appEnum.getId());
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/epCodeManager/removeBusinessCode", method = RequestMethod.GET)
    @ResponseBody
    public Object removeBusinessCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = Integer.parseInt(request.getParameter("id"));
        systemMaintenanceService.removeBusinessCode(id);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/epCodeManager/updateBusinessCode", method = RequestMethod.POST)
    @ResponseBody
    public Object updateBusinessCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AppEnum appEnum = new AppEnum();
        appEnum.setId(parseInt(request.getParameter("id")));
        appEnum.setCategoryCode(request.getParameter("categoryCode"));
        appEnum.setCode(request.getParameter("code"));
        // appEnum.setLabel(request.getParameter("label"));
        appEnum.setLabel_english(request.getParameter("label_english"));
        appEnum.setLabel_french(request.getParameter("label_french"));
        appEnum.setLabel_chinese(request.getParameter("label_chinese"));
        appEnum.setLabel_german(request.getParameter("label_german"));
        appEnum.setLabel_spanish(request.getParameter("label_spanish"));
        appEnum.setRemarks(request.getParameter("remarks"));
        appEnum.setSortingOrder(parseInt(request.getParameter("sortingOrder")));
        appEnum.setStatus(request.getParameter("status"));
       
        systemMaintenanceService.updateBusinessCode(appEnum);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, appEnum.getId());
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/epCodeManager/getCountry", method = RequestMethod.GET)
    @ResponseBody
    public Object getCountry(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<AppEnum> list = systemMaintenanceService.getCountry();
        List<Map<String, String>> mapList = newArrayList();
        for (AppEnum s : list) {
            Map<String, String> map = newHashMap();
            map.put("id", s.getCode());
            map.put("name", s.getLabel());
            mapList.add(map);
        }
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, mapList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/epCodeManager/getProvinceByCountry", method = RequestMethod.GET)
    @ResponseBody
    public Object getProvinceByCountry(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<AppEnum> list = systemMaintenanceService.getProvinceByCountry(request.getParameter("country"));
        List<Map<String, String>> mapList = newArrayList();
        for (AppEnum s : list) {
            Map<String, String> map = newHashMap();
            map.put("id", s.getCode());
            map.put("name", s.getLabel());
            mapList.add(map);
        }
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, mapList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/epCodeManager/getEntityContactCountry", method = RequestMethod.GET)
    @ResponseBody
    public Object getEntityContactCountry(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<AppEnum> list = systemMaintenanceService.getEntityContactCountry();
        List<Map<String, String>> mapList = newArrayList();
        for (AppEnum s : list) {
            Map<String, String> map = newHashMap();
            map.put("id", s.getCode());
            map.put("name", s.getLabel());
            mapList.add(map);
        }
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, mapList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/epCodeManager/getMultipileLanguageByText", method = RequestMethod.GET)
    @ResponseBody
    public Object getMultipileLanguageByText(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AppEnum result = systemMaintenanceService.getMultipileLanguageByText(request.getParameter("textStr"));
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        map.put(PARAM_RECORDS, result);
        return map;
    }
}
