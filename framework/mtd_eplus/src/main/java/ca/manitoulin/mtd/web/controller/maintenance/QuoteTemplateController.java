package ca.manitoulin.mtd.web.controller.maintenance;

import ca.manitoulin.mtd.dto.maintenance.QuoteTemplate;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.maintenance.ISystemMaintenanceService;
import ca.manitoulin.mtd.util.json.JacksonMapper;
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
import static ca.manitoulin.mtd.util.DateUtil.DATE_TIME_FARMAT_YYYYMMDDHHMM;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.parseInt;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.arrayToList;

@Controller
public class QuoteTemplateController {

    @Autowired
    private ISystemMaintenanceService systemMaintenanceService;
    @Autowired
    private BusinessCodeController businessCodeController;

    @RequestMapping(value = "/maintenance/quoteTemplate/retrieveQuoteTemplateListByType", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveQuoteTemplateListByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripType = request.getParameter("tripType");
        List<QuoteTemplate> list = systemMaintenanceService.retrieveQuoteTemplateListByType(tripType);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, list);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/quoteTemplate/saveQuoteTemplateName", method = RequestMethod.POST)
    @ResponseBody
    public Object saveQuoteTemplateName(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuoteTemplate e = new QuoteTemplate();
        String[] requiredParams = new String[]{"name"};
        for (String param : requiredParams) {
            if (isBlank(request.getParameter(param))) {
                throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{param}), null);
            }
        }

        boolean isDuplicate = false;
        String name;
        String tripType;
        name = request.getParameter("name");
        tripType = request.getParameter("type");

        Map<String, Object> map = newHashMap();
        isDuplicate = systemMaintenanceService.checkDuplicateQuoteTemplateByTypeAndName(tripType, name);
        if (isDuplicate){
            map.put(KEY_RESULT, RESPONSE_DUPLICATE);
        }
        else {

            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FARMAT_YYYYMMDDHHMM);
            String str = sdf.format(new Date());
            e.setName(name);
            e.setCategory("New Category_" + str);
            e.setSequence(1);
            e.setType(tripType);
            e.setItem("New Item_" + str);
            e.setStatus("Active");
            e.setCategorySequence(1);
            systemMaintenanceService.addQuoteTemplate(e);
            map.put(PARAM_RECORDS, e.getId());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
        }
        return map;
    }

    @RequestMapping(value = "/maintenance/quoteTemplate/retrieveQuoteTemplate", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveQuoteTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String eId = request.getParameter("eid");
        Map<String, Object> map = newHashMap();
        try {
            QuoteTemplate event = systemMaintenanceService.retrieveQuoteTemplateById(parseInt(eId));
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            map.put(PARAM_RECORDS, event);
        } catch (Exception e) {
            map.put(KEY_RESULT, RESPONSE_ERROR);
        }
        return map;
    }

    @RequestMapping(value = "/maintenance/quoteTemplate/addQuoteTemplate", method = RequestMethod.POST)
    @ResponseBody
    public Object addQuoteTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuoteTemplate e = new QuoteTemplate();
        e.setName(request.getParameter("name"));
        e.setCategory(request.getParameter("category"));
        e.setSequence(100);
        e.setType(request.getParameter("type"));
        e.setItem(request.getParameter("item"));
        e.setDescription(request.getParameter("description"));
        e.setStatus(request.getParameter("status"));

        systemMaintenanceService.addQuoteTemplate(e);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, e.getId());
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/quoteTemplate/updateQuoteTemplate", method = RequestMethod.POST)
    @ResponseBody
    public Object updateQuoteTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuoteTemplate e = new QuoteTemplate();
        e.setId(parseInt(request.getParameter("id")));
        e.setName(request.getParameter("name"));
        e.setCategory(request.getParameter("category"));
        if (!isBlank(request.getParameter("sequence"))) {
            e.setSequence(Integer.parseInt(request.getParameter("sequence")));
        }
        else {
            e.setSequence(100);
        }
        e.setType(request.getParameter("type"));
        e.setItem(request.getParameter("item"));
        e.setDescription(request.getParameter("description"));
        e.setStatus(request.getParameter("status"));
        systemMaintenanceService.updateQuoteTemplate(e);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, e.getId());
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/quoteTemplate/removeQuoteTemplateByCategory", method = RequestMethod.GET)
    @ResponseBody
    public Object removeQuoteTemplateByCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripType = request.getParameter("tripType");
        String name = request.getParameter("name");
        systemMaintenanceService.removeQuoteTemplateByCategory(tripType, name);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/quoteTemplate/removeQuoteTemplate", method = RequestMethod.GET)
    @ResponseBody
    public Object removeEventTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = Integer.parseInt(request.getParameter("id"));
        systemMaintenanceService.removeQuoteTemplate(id);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/quoteTemplate/getEpCodeListByType", method = RequestMethod.GET)
    @ResponseBody
    public Object getEpCodeListByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return businessCodeController.getEpCodeListByType(request, response);
    }

    @RequestMapping(value = "maintenance/quoteTemplate/getQuoteTree", method = RequestMethod.GET)
    @ResponseBody
    public Object getQuoteTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripType = request.getParameter("tripType");
        String name = request.getParameter("name");
        List<Map<String, Object>> result = systemMaintenanceService.retrieveQuoteTemplateTree(tripType, name);
        Map<String, Object> map = newHashMap();
        if (isNotEmpty(result)) {
            map.put(PARAM_RECORDS, result);
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
        }
        else{
            map.put(KEY_RESULT, RESPONSE_ERROR);
        }
        return map;
    }

    @RequestMapping(value = "/maintenance/quoteTemplate/saveQuoteTree", method = RequestMethod.POST)
    @ResponseBody
    public Object saveQuoteTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String list = request.getParameter("list");

        if (!isBlank(list)) {
            Integer id;
            Integer categorySequence;
            Integer sequence;

            List<Map<String, Object>> maps = JacksonMapper.getInstance().readValue(list, List.class);

            if (null != maps && 0 != maps.size()) {
                for (Map<String, Object> map : maps) {
                    id = Integer.parseInt(map.get("id") + "");
                    if (id > 0){
                        categorySequence = Integer.parseInt(map.get("categorySeq") + "")+ 1;
                        sequence = Integer.parseInt(map.get("itemSeq") + "") + 1;
                        systemMaintenanceService.updateQuoteTemplateSequence(id, categorySequence, sequence);
                    }
                }
            }
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }
}
