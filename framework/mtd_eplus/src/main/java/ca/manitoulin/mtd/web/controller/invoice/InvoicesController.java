package ca.manitoulin.mtd.web.controller.invoice;

import ca.manitoulin.mtd.dto.operationconsole.Invoice;
import ca.manitoulin.mtd.dto.security.Organization;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.operationconsole.ITripService;
import ca.manitoulin.mtd.util.ApplicationSession;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ca.manitoulin.mtd.constant.ContextConstants.*;
import static ca.manitoulin.mtd.util.DateUtil.LONG_DATE_FORMAT_SHOW;
import static ca.manitoulin.mtd.util.DateUtil.toDate;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by zhuiz on 2017/4/6.
 *
 * @Description
 */
@Controller
@RequestMapping(value = "/invoiceManagement/invoices")
public class InvoicesController {
    @Autowired
    private ITripService tripService;

    @RequestMapping(value = "/retrieveInvoices", method = GET)
    @ResponseBody
    public Object retrieveInvoices(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        if (isEmpty(request.getParameter("departmentIds"))) {
            map.put(PARAM_RECORDS, newArrayList());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        Iterable<String> departmentIds = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(request.getParameter("departmentIds"));
        List<String> deptIdStrs = newArrayList(departmentIds);
        List<Integer> deptIds = newArrayList();
        for (String deptId : deptIdStrs) {
            deptIds.add(parseInt(deptId));
        }

        Date from = toDate(request.getParameter("from"), LONG_DATE_FORMAT_SHOW);
        Date to = toDate(request.getParameter("to"), LONG_DATE_FORMAT_SHOW);

        List<Invoice> invoices = tripService.retrieveInvoices(deptIds, from, to);
        map.put(PARAM_RECORDS, invoices);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/getUserDepts", method = GET)
    @ResponseBody
    public Object getUserDepts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Organization> organizations = ApplicationSession.get().getDepartments();
        if (isEmpty(organizations)) throw new BusinessException("department list is empty!");
        Map<String, Object> map = newHashMap();
        List<Map<String, String>> result = newArrayList();
        for (Organization organization : organizations) {
            Map<String, String> item = newHashMap();
            item.put("id", valueOf(organization.getId()));
            item.put("name", organization.getName());
            result.add(item);
            if (equal(organization.getId(), ApplicationSession.get().getDepId())) {
                map.put(PARAM_RECORDS_EXTRA, item);
            }
        }
        if (organizations.size() > 1) {
            Map<String, String> extra = newHashMap();
            extra.put("id", EMPTY);
            extra.put("name", "ALL");
            result.add(extra);
        }


        map.put(PARAM_RECORDS, result);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

}
