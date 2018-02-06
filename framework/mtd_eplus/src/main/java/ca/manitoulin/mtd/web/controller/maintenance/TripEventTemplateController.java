package ca.manitoulin.mtd.web.controller.maintenance;

import ca.manitoulin.mtd.dto.maintenance.TripEventTemplate;
import ca.manitoulin.mtd.dto.maintenance.TripEventTemplateCost;
import ca.manitoulin.mtd.dto.maintenance.TripEventTemplateNotify;
import ca.manitoulin.mtd.dto.support.AppEnum;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.maintenance.ISystemMaintenanceService;
import ca.manitoulin.mtd.util.json.JacksonMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ca.manitoulin.mtd.constant.ContextConstants.*;
import static ca.manitoulin.mtd.util.DateUtil.DATE_TIME_FARMAT_YYYYMMDDHHMM;
import static ca.manitoulin.mtd.util.DateUtil.LONG_DATE_FORMAT_SHOW;
import static ca.manitoulin.mtd.util.DateUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.arrayToList;

@Controller
public class TripEventTemplateController {
    @Autowired
    private ISystemMaintenanceService systemMaintenanceService;
    @Autowired
    private BusinessCodeController businessCodeController;

    @RequestMapping(value = "/maintenance/tripEventTemplate/retrieveTripEventTemplateListByType", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveTripEventTemplateListByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripType = request.getParameter("tripType");
        List<TripEventTemplate> list = systemMaintenanceService.retrieveTripEventTemplateListByType(tripType);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, list);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/tripEventTemplate/saveEventName", method = RequestMethod.POST)
    @ResponseBody
    public Object saveEventName(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TripEventTemplate e = new TripEventTemplate();
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
        isDuplicate = systemMaintenanceService.checkDuplicateEventTemplateByTypeAndName(tripType, name);
        if (isDuplicate){
            map.put(KEY_RESULT, RESPONSE_DUPLICATE);
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FARMAT_YYYYMMDDHHMM);
            String str = sdf.format(new Date());
            e.setName(name);
            e.setEventClass("T");
            e.setCode("New Code_" + str);
            e.setCategory("New Category_" + str);
            e.setSequence(1);
            e.setType(tripType);
            e.setItem("New Item_" + str);
            e.setStatus("Active");
            e.setCustomerNotify("1");
            e.setCategorySequence(1);
            e.setDuplicateEmailForAllEvent("N");

            systemMaintenanceService.addTripEventTemplate(e);
            map.put(PARAM_RECORDS, e.getId());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
        }
        return map;
    }

    @RequestMapping(value = "/maintenance/tripEventTemplate/retrieveEvent", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String eId = request.getParameter("eid");
        Map<String, Object> map = newHashMap();
        try {
            TripEventTemplate event = systemMaintenanceService.retrieveTripEventTemplateById(parseInt(eId));
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            map.put(PARAM_RECORDS, event);
        } catch (Exception e) {
            map.put(KEY_RESULT, RESPONSE_ERROR);
        }
        return map;
    }

    @RequestMapping(value = "/maintenance/tripEventTemplate/saveEventTemplate", method = RequestMethod.POST)
    @ResponseBody
    public Object saveEventTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TripEventTemplate e = new TripEventTemplate();
        String[] requiredParams = new String[]{"eventClass", "category", "item", "status"};
        for (String param : requiredParams) {
            if (isBlank(request.getParameter(param))) {
                throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{param}), null);
            }
        }
        String templateId = request.getParameter("id");
        if (!isBlank(templateId) && !templateId.equals("0")) {
            e.setId(parseInt(request.getParameter("id")));
        }
        e.setEventClass(request.getParameter("eventClass"));
        e.setName(request.getParameter("name"));
        e.setCode(request.getParameter("code"));
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
        e.setCustomerNotify(request.getParameter("customerNotify"));
        if (!isBlank(request.getParameter("linkedEntity"))) {
            e.setLinkedEntity(Integer.parseInt(request.getParameter("linkedEntity")));
        }
        if (!isBlank(request.getParameter("linkedEntityContact"))) {
            e.setLinkedEntityContact(Integer.parseInt(request.getParameter("linkedEntityContact")));
        }
        if (!isBlank(request.getParameter("categorySequence"))) {
            e.setCategorySequence(Integer.parseInt(request.getParameter("categorySequence")));
        }
        else {
            e.setCategorySequence(100);
        }
        e.setDuplicateEmailForAllEvent(request.getParameter("duplicateEmailForAllEvent"));

        String templateCostList = request.getParameter("templateCost");
        if (!isBlank(templateCostList)) {
            List<Map<String, Object>> maps = JacksonMapper.getInstance().readValue(templateCostList, List.class);

            if (null != maps && 0 != maps.size()) {
                List<TripEventTemplateCost> templateCosts = newArrayList();
                for (Map<String, Object> map : maps) {
                    TripEventTemplateCost templateCost = new TripEventTemplateCost();

                    templateCost.setEstCost(new BigDecimal(parseDouble(map.get("estCost") + "")));
                    templateCost.setEstDate(toDate(map.get("estDate") + "", LONG_DATE_FORMAT_SHOW));
                    templateCost.setEstCurrency((String) map.get("estCurrency"));
                    templateCost.setChargeCode((String) map.get("chargeCode"));
                    templateCost.setChargeDesc((String) map.get("chargeDesc"));
                    templateCost.setDescription((String) map.get("description"));
                    templateCost.setVisible((String) map.get("visible"));
                    if (null != map.get("linkedEntity") && ! map.get("linkedEntity").equals("") && ! map.get("linkedEntity").equals("0")) {
                        templateCost.setLinkedEntity(Integer.parseInt(map.get("linkedEntity") + ""));
                    }
                    if (null != map.get("linkedEntityContact") && ! map.get("linkedEntityContact").equals("") && ! map.get("linkedEntityContact").equals("0")) {
                        templateCost.setLinkedEntityContact(Integer.parseInt(map.get("linkedEntityContact") + ""));
                    }
                    templateCosts.add(templateCost);
                }
                e.setTemplateCost(templateCosts);
            }
        }

        String templateNotifyList = request.getParameter("templateNotify");
        if (!isBlank(templateNotifyList)) {
            List<Map<String, Object>> maps = JacksonMapper.getInstance().readValue(templateNotifyList, List.class);

            if (null != maps && 0 != maps.size()) {
                List<TripEventTemplateNotify> templateNotifys = newArrayList();
                for (Map<String, Object> map : maps) {
                    TripEventTemplateNotify templateNotify = new TripEventTemplateNotify();

                    templateNotify.setName((String) map.get("name"));
                    templateNotify.setEmail((String) map.get("email"));
                    templateNotifys.add(templateNotify);
                }
                e.setTemplateNotify(templateNotifys);
            }
        }

        if (isBlank(templateId) || templateId.equals("0")) {
            systemMaintenanceService.addTripEventTemplate(e);
        }
        else {
            e.setId(parseInt(request.getParameter("id")));
            systemMaintenanceService.updateTripEventTemplate(e);
        }

        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, e.getId());
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/tripEventTemplate/removeEventTemplateByCategory", method = RequestMethod.GET)
    @ResponseBody
    public Object removeEventTemplateByCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripType = request.getParameter("tripType");
        String name = request.getParameter("name");
        systemMaintenanceService.removeEventTemplateByCategory(tripType, name);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/tripEventTemplate/removeEventTemplate", method = RequestMethod.GET)
    @ResponseBody
    public Object removeEventTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = Integer.parseInt(request.getParameter("id"));
        systemMaintenanceService.removeTripEventTemplate(id);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/maintenance/tripEventTemplate/getEventTree", method = RequestMethod.GET)
    @ResponseBody
    public Object getEventTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripType = request.getParameter("tripType");
        String name = request.getParameter("name");
        List result = new ArrayList();
        String prevLevel1 = "";
        String prevLevel2 = "";
        String thisLevel1;
        String thisLevel2;
        int pid = 0;
        int cpid = 0;
        int categorySeq = 0;
        int itemSeq = 0;

        List<TripEventTemplate> findAll = systemMaintenanceService.retrieveTripEventTemplates(tripType, name);
        Map<String, Object> map = newHashMap();
        if (isNotEmpty(findAll)) {
            for (TripEventTemplate tripEventTemplate : findAll) {
                thisLevel1 = tripEventTemplate.getName();
                Map d = newHashMap();
                d.put("id", tripEventTemplate.getId());
                d.put("pId", 0);
                d.put("name", tripEventTemplate.getName());
                d.put("t", tripEventTemplate.getName());
                d.put("entityId", tripEventTemplate.getId());
                d.put("open", "false");
                d.put("type", "event");
                d.put("TripType", tripEventTemplate.getType());
                d.put("EventName", tripEventTemplate.getName());
                d.put("Category", tripEventTemplate.getCategory());
                if (!prevLevel1.equals(thisLevel1)) {
                    categorySeq = 0;
                    itemSeq = 0;
                    result.add(d);
                    pid = tripEventTemplate.getId();
                }
                thisLevel2 = tripEventTemplate.getCategory();
                Map data = newHashMap();
                data.put("id", tripEventTemplate.getId() + 1000000);
                data.put("pId", pid);
                data.put("name", tripEventTemplate.getCategory());
                data.put("t", tripEventTemplate.getCategory());
                data.put("open", "false");
                data.put("entityId", tripEventTemplate.getId());
                data.put("type", "category");
                data.put("TripType", tripEventTemplate.getType());
                data.put("EventName", tripEventTemplate.getName());
                data.put("Category", tripEventTemplate.getCategory());
                if (!prevLevel2.equals(thisLevel2) || !prevLevel1.equals(thisLevel1)) {
                    itemSeq = 0;
                    categorySeq = categorySeq + 1;
                    data.put("categorySeq", categorySeq);
                    result.add(data);
                    cpid = tripEventTemplate.getId() + 1000000;
                }
                Map item = newHashMap();
                item.put("id", tripEventTemplate.getId() + 100000000);
                item.put("pId", cpid);
                if (StringUtils.isNotEmpty(tripEventTemplate.getLinkedEntityAddress1()))
                    item.put("name", tripEventTemplate.getItem() + " ( " + tripEventTemplate.getLinkedEntityAddress1() + " )");
                else
                    item.put("name", tripEventTemplate.getItem());
                item.put("t", tripEventTemplate.getItem());
                item.put("open", "false");
                item.put("entityId", tripEventTemplate.getId());
                item.put("type", "item");
                item.put("TripType", tripEventTemplate.getType());
                item.put("EventName", tripEventTemplate.getName());
                item.put("Category", tripEventTemplate.getCategory());
                itemSeq = itemSeq + 1;
                item.put("itemSeq", itemSeq);
                result.add(item);
                prevLevel1 = thisLevel1;
                prevLevel2 = thisLevel2;
            }
            map.put(PARAM_RECORDS, result);
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        map.put(KEY_RESULT, RESPONSE_ERROR);
        return map;
    }

    @RequestMapping(value = "/maintenance/tripEventTemplate/saveEventTree", method = RequestMethod.POST)
    @ResponseBody
    public Object saveEventTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
                        systemMaintenanceService.updateTripEventTemplateSequence(id, categorySequence, sequence);
                    }
                }
            }
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }


    @RequestMapping(value = "/maintenance/tripEventTemplate/getEpCodeListByType", method = RequestMethod.GET)
    @ResponseBody
    public Object getEpCodeListByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return businessCodeController.getEpCodeListByType(request, response);
    }
}
