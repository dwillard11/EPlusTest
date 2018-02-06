package ca.manitoulin.mtd.web.controller.operationconsole;

import ca.manitoulin.mtd.dto.misc.CommunicationEmail;
import ca.manitoulin.mtd.dto.operationconsole.TripDocument;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.operationconsole.ITripService;
import ca.manitoulin.mtd.util.EmailUtils;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static ca.manitoulin.mtd.constant.ContextConstants.*;
import static ca.manitoulin.mtd.util.DateUtil.DATE_TIME_FARMAT_YYYYMMDDHHMM;
import static ca.manitoulin.mtd.util.DateUtil.toDate;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by zhuiz on 2017/5/17.
 */
@Controller
@RequestMapping(value = "/operationconsole/commcenter")
public class CommCenterController {

    @Autowired
    private ITripService tripService;

    @RequestMapping(value = "/retrieveAllUnlinkedEmailsByDepts", method = POST)
    @ResponseBody
    public Object retrieveAllUnlinkedEmailsByDepts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        if (StringUtils.isEmpty(request.getParameter("departmentIds"))) {
            map.put(PARAM_RECORDS, tripService.retrieveAllUnlinkedEmails(null));
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
        List<CommunicationEmail> emails = tripService.retrieveAllUnlinkedEmails(deptIds);
        Map<String, Object> map2 = newHashMap();
        map2.put(PARAM_RECORDS, emails);
        map2.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map2;

    }
    @RequestMapping(value = "/retrieveAllUnlinkedEmailsByConditions", method = POST)
    @ResponseBody
    public Object retrieveAllUnlinkedEmailsByConditions(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        String dateFrom = trimToEmpty(request.getParameter("dateFrom"));
        String dateTo = trimToEmpty(request.getParameter("dateTo"));
        String searchKey = trimToEmpty(request.getParameter("searchKey"));
        String searchLabel = trimToEmpty(request.getParameter("searchLabel"));
        String includeDelete = trimToEmpty(request.getParameter("includeDelete"));
        String includeProcessed = trimToEmpty(request.getParameter("includeProcessed"));
        String includeOut = trimToEmpty(request.getParameter("includeOut"));
        /*if (StringUtils.isEmpty(request.getParameter("departmentIds"))) {
            map.put(PARAM_RECORDS, tripService.retrieveAllUnlinkedEmails(null));
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
        List<CommunicationEmail> emails = tripService.retrieveAllUnlinkedEmails(deptIds);*/
        List<Integer> deptIds = newArrayList();
        if (!EMPTY.equals(trimToEmpty(request.getParameter("departmentIds")))) {
            Iterable<String> departmentIds = Splitter.on(',')
                    .trimResults()
                    .omitEmptyStrings()
                    .split(request.getParameter("departmentIds"));
            List<String> deptIdStrs = newArrayList(departmentIds);

            for (String deptId : deptIdStrs) {
                deptIds.add(parseInt(deptId));
            }
        }
        else{
            deptIds.add(0);
        }

        Map<String, Object> map2 = newHashMap();
        List<CommunicationEmail> emails = tripService.retrieveAllUnlinkedEmailsByConditions(
                toDate(dateFrom,DATE_TIME_FARMAT_YYYYMMDDHHMM),
                toDate(dateTo,DATE_TIME_FARMAT_YYYYMMDDHHMM),
        		searchLabel,
        		searchKey,
                includeDelete,
                includeProcessed,
                includeOut,
        		deptIds);
        map2.put(PARAM_RECORDS, emails);
        map2.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map2;

    }

    @RequestMapping(value = "/updateEmailLink", method = POST)
    @ResponseBody
    public Object updateEmailLink(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String emailId = request.getParameter("emailId");
        String tripRefNumber = request.getParameter("tripRefNumber");
        String tag = request.getParameter("tag");
        if (isEmpty(tripRefNumber)) {
            throw new BusinessException("trip id can not be empty!");
        }
        if (isNotEmpty(emailId) && isNotEmpty(tripRefNumber) && isNotEmpty(tag)) {
            if (equal("link", tag)) {
                tripService.linkEmailToTrip(parseLong(emailId), tripRefNumber);
            }
            if (equal("unlink", tag)) {
                tripService.unklinkEmail(parseLong(emailId));
            }
            if (equal("relink", tag)) {
                tripService.relinkEmailToTrip(parseLong(emailId), tripRefNumber);
            }
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveEmail", method = POST)
    @ResponseBody
    public Object retrieveEmail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String emailId = request.getParameter("emailId");
        if (isEmpty(emailId)) {
            throw new BusinessException("Email Id is Empty");
        }
        CommunicationEmail email = tripService.retrieveEmail(parseLong(emailId));
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, email);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/removeEmail", method = POST)
    @ResponseBody
    public Object removeEmail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String emailId = request.getParameter("emailId");
        if (isEmpty(emailId)) {
            throw new BusinessException("Email Id is Empty");
        }
        tripService.removeEmail(parseLong(emailId));
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/unremoveEmail", method = POST)
    @ResponseBody
    public Object unremoveEmail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String emailId = request.getParameter("emailId");
        if (isEmpty(emailId)) {
            throw new BusinessException("Email Id is Empty");
        }
        tripService.unremoveEmail(parseLong(emailId));
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/removeEmails", method = POST)
    @ResponseBody
    public Object removeEmails(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        String emailIds = request.getParameter("emailIds");
        if (StringUtils.isEmpty(emailIds)) {
            map.put(PARAM_RECORDS, newArrayList());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        Iterable<String> ids = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(emailIds);
        for (String emailId : ids) {
            tripService.removeEmail(parseLong(emailId));
        }
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/markEmail", method = POST)
    @ResponseBody
    public Object markEmail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tag = request.getParameter("tag");
        String emailId = request.getParameter("emailId");
        if (isEmpty(emailId)) {
            throw new BusinessException("Email Id is Empty");
        }
        if (equal(tag, "read")) {
            tripService.markEmailAsRead(parseLong(emailId));
        }
        if (equal(tag, "unread")) {
            tripService.markEmailAsUnRead(parseLong(emailId));
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }
    @RequestMapping(value = "/markLabel", method = POST)
    @ResponseBody
    public Object markLabel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String label = request.getParameter("label");
        String emailId = request.getParameter("emailId");
        if (isEmpty(emailId)) {
            throw new BusinessException("Email Id is Empty");
        }
        tripService.updateEmailLabel(parseLong(emailId),label);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }
    @RequestMapping(value = "/processEmail", method = POST)
    @ResponseBody
    public Object processEmail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tag = request.getParameter("tag");
        String emailId = request.getParameter("emailId");
        if (isEmpty(emailId)) {
            throw new BusinessException("Email Id is Empty");
        }
        if (equal(tag, "process")) {
            tripService.markEmailAsProcess(parseLong(emailId));
        }
        if (equal(tag, "unProcess")) {
            tripService.markEmailAsUnProcess(parseLong(emailId));
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveEmailsByTrip", method = POST)
    @ResponseBody
    public Object retrieveEmailsByTrip(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripId = request.getParameter("tripId");
        Map<String, Object> map = newHashMap();
        if (isNotEmpty(tripId)) {
            map.put(PARAM_RECORDS, tripService.retrieveEmailsByTrip(parseInt(tripId)));
        } else {
            throw new BusinessException("Trip id is empty");
        }
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;

    }
    @RequestMapping(value = "/searchEmails", method = POST)
    @ResponseBody
    public Object searchEmails(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripId = request.getParameter("tripId");
        String dateFrom = trimToEmpty(request.getParameter("dateFrom"));
        String dateTo = trimToEmpty(request.getParameter("dateTo"));
        String searchKey = trimToEmpty(request.getParameter("searchKey"));
        String searchLabel = trimToEmpty(request.getParameter("searchLabel"));
        String includeDelete = trimToEmpty(request.getParameter("includeDelete"));
        Map<String, Object> map = newHashMap();
        if (isNotEmpty(tripId)) {
            map.put(PARAM_RECORDS, tripService.searchEmails(parseInt(tripId),
            		toDate(dateFrom,DATE_TIME_FARMAT_YYYYMMDDHHMM),
            		toDate(dateTo,DATE_TIME_FARMAT_YYYYMMDDHHMM),
            		searchLabel,
            		searchKey,
                    includeDelete));
        } else {
            throw new BusinessException("Trip id is empty");
        }
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;

    }

    @RequestMapping(value = "/copyAttach", method = POST)
    @ResponseBody
    public Object copyAttach(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String docId = request.getParameter("docId");
        if(StringUtils.isEmpty(docId)) {
            throw new BusinessException("doc id is empty");
        }
        String newName = request.getParameter("newName");
        String newType = request.getParameter("newType");
        tripService.copyAttachmentToDocument(parseInt(docId),newName, newType);

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;

    }

    @RequestMapping(value = "/sendEmail", method = POST)
    @ResponseBody
    public Object sendEmail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String emailId = request.getParameter("emailId");
        String tripId = request.getParameter("tripId");
        String subject = request.getParameter("subject");
        String content = request.getParameter("content");
        String mailFrom = request.getParameter("mailFrom");
        String mailTo = request.getParameter("mailTo");
        String mailCc = request.getParameter("mailCc");
        String mailBcc = request.getParameter("mailBcc");
        String attachIds = request.getParameter("attachIds");
        String departmentId = request.getParameter("departmentId");
        String processedStatus = request.getParameter("processedStatus");
        String tag = request.getParameter("tag");
        String action = request.getParameter("action");

        /*
        Issue #48: Description: need ability to reply without linked trip.
        if (isEmpty(tripId)) {
            throw new BusinessException("tripid is empty!");
        }
        if (isEmpty(departmentId)) {
            throw new BusinessException("departmentId is empty!");
        }
        */
        if (isEmpty(mailTo)) {
            throw new BusinessException("mail to can not be empty!");
        }
        if (isEmpty(subject)) {
            throw new BusinessException("Subject can not be empty!");
        }
        if (isEmpty(content)) {
            throw new BusinessException("Content can not be empty!");
        }
        if (StringUtils.isNotEmpty(mailFrom) && !EmailUtils.validateBatch(mailFrom)) {
            throw new BusinessException("Mail From is invalidate!");
        }
        if (StringUtils.isNotEmpty(mailTo) && !EmailUtils.validateBatch(mailTo)) {
            throw new BusinessException("Mail To is invalidate!");
        }
        if (StringUtils.isNotEmpty(mailCc) && !EmailUtils.validateBatch(mailCc)) {
            throw new BusinessException("Mail CC is invalidate!");
        }
        if (StringUtils.isNotEmpty(mailBcc) && !EmailUtils.validateBatch(mailBcc)) {
            throw new BusinessException("Mail BCC is invalidate!");
        }
        /*
        CRITICAL Issue #150– New 2018-01-23: When “Reply”, “Reply to All” in global communication center, please don’t assign the status field to “Processed”, keep it “UnProcessed”.
        if (action.equals("send") && !isEmpty(emailId)) {
            tripService.markEmailAsProcess(parseLong(emailId));
        }
        */
        CommunicationEmail email = new CommunicationEmail();

        if (StringUtils.isNotEmpty(attachIds)) {
            Iterable<String> attachStrIds = Splitter.on(',')
                    .trimResults()
                    .omitEmptyStrings()
                    .split(attachIds);
            List<TripDocument> documents = newArrayList();
            for (String idStr : attachStrIds) {
                if (StringUtils.isNotEmpty(idStr)) {
                    TripDocument document = new TripDocument();
                    document.setId(parseInt(idStr));
                    documents.add(document);

                }
            }
            email.setAttachments(documents);
        }

        if (tag.equals("edit") && !isEmpty(emailId)) {
            email.setId(parseLong(emailId));
        }
        if (action.equals("save") && !isEmpty(emailId)) {
            email.setId(parseLong(emailId));
        }
        if (!isEmpty(tripId)) {
            email.setTripId(parseInt(tripId));
        }
        email.setSubject(subject);
        email.setContent(content);
        email.setMailFrom(mailFrom);
        email.setMailTo(mailTo);
        email.setMailCc(mailCc);
        email.setMailBcc(mailBcc);
        email.setCategory(EMPTY);
        email.setProcessedStatus(processedStatus);
        if (!isEmpty(departmentId)) {
            email.setDepartmentId(Integer.parseInt(departmentId));
        }
        tripService.sendEmail(email, action);

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }
}
