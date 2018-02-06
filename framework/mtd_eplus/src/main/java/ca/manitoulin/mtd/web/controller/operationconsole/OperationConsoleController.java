package ca.manitoulin.mtd.web.controller.operationconsole;

import ca.manitoulin.mtd.dto.customer.Courier;
import ca.manitoulin.mtd.dto.customer.CustomerContact;
import ca.manitoulin.mtd.dto.customer.CustomerLocation;
import ca.manitoulin.mtd.dto.maintenance.TripEventTemplate;
import ca.manitoulin.mtd.dto.operationconsole.*;
import ca.manitoulin.mtd.dto.security.Organization;
import ca.manitoulin.mtd.dto.security.Role;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.exception.ConcurrentException;
import ca.manitoulin.mtd.service.customer.ICustomerService;
import ca.manitoulin.mtd.service.maintenance.ICurrencyService;
import ca.manitoulin.mtd.service.maintenance.ISystemMaintenanceService;
import ca.manitoulin.mtd.service.operationconsole.ITripService;
import ca.manitoulin.mtd.util.ApplicationSession;
import ca.manitoulin.mtd.util.DateUtil;
import ca.manitoulin.mtd.util.IDocumentManager;
import ca.manitoulin.mtd.util.json.JacksonMapper;
import ca.manitoulin.mtd.web.controller.maintenance.BusinessCodeController;
import ca.manitoulin.mtd.web.controller.maintenance.DepartmentManagementController;
import ca.manitoulin.mtd.web.controller.maintenance.TripEventTemplateController;
import ca.manitoulin.mtd.web.utils.Field;
import ca.manitoulin.mtd.web.utils.QuoteGenerator;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.lowagie.text.pdf.PdfPCell;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ca.manitoulin.mtd.NumberUtils.*;
import static ca.manitoulin.mtd.constant.ContextConstants.*;
import static ca.manitoulin.mtd.util.DateUtil.*;
import static ca.manitoulin.mtd.web.utils.QuoteGenerator.*;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.math.BigDecimal.ROUND_HALF_DOWN;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.util.CollectionUtils.arrayToList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by zhuiz on 2017/3/2.
 */
@Controller
@RequestMapping(value = "/operationconsole/operationconsole")
public class OperationConsoleController {
    private static final Logger log = Logger.getLogger(OperationConsoleController.class);

    @Autowired
    private ITripService tripService;
    @Autowired
    private DepartmentManagementController departmentManagementController;
    @Autowired
    private ICustomerService customerService;
    @Autowired
    private BusinessCodeController businessCodeController;
    @Autowired
    private ISystemMaintenanceService systemMaintenanceService;
    @Autowired
    private TripEventTemplateController tripEventTemplateController;
    @Autowired
    private ICurrencyService currencyService;
    @Autowired
    private IDocumentManager docManager;

    @RequestMapping(value = "/retrieveQuoteList", method = POST)
    @ResponseBody
    public Object retrieveQuoteList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String departmentId = request.getParameter("departmentId");
        String status = request.getParameter("tripStatus");
        String searchKey = request.getParameter("searchKey");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        List<Trip> quoteList = tripService.retrieveQuoteListByDepartmentAndStatus(parseInt(departmentId), status, searchKey, startDate, endDate);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, quoteList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveTripList", method = POST)
    @ResponseBody
    public Object retrieveTripList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String departmentId = request.getParameter("departmentId");
        String category = request.getParameter("tripCategory");
        String searchKey = request.getParameter("searchKey");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String searchAWB = request.getParameter("searchAWB");
        String eventDesc = request.getParameter("eventDesc");
        String chargeCode = request.getParameter("chargeCode");
        String chargeDesc = request.getParameter("chargeDesc");
        List<Trip> tripList = tripService.retrieveTripList(parseInt(departmentId), category, searchKey, startDate, endDate, searchAWB, eventDesc, chargeCode, chargeDesc);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, tripList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveCourierList", method = POST)
    @ResponseBody
    public Object retrieveCourierList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String companyId = request.getParameter("companyId");
        if (isBlank(companyId))
            companyId = "0";
        String locationId = request.getParameter("locationId");
        if (isBlank(locationId))
            locationId = "0";
        String status = request.getParameter("status");
        String country = request.getParameter("country");
        String airport = request.getParameter("airport");
        String city = request.getParameter("city");
        List<Courier> courierList = customerService.retrieveCourierList(parseInt(companyId), parseInt(locationId), status, country, airport, city);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, courierList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/selectLockedTrips", method = POST)
    @ResponseBody
    public Object selectLockedTrips(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        List<Trip> tripList = tripService.selectLockedTripAndQuotes(deptIds);

        map.put(PARAM_RECORDS, tripList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/releaseTripLockById", method = GET)
    @ResponseBody
    public Object releaseTripLockById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripId = request.getParameter("tripid");
        tripService.deleteTripLockStatusByID(parseInt(tripId));
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveQuoteListByDepartmentAndStatus", method = POST)
    @ResponseBody
    public Object retrieveQuoteListByDepartmentAndStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = newHashMap();
        if (StringUtils.isEmpty(request.getParameter("departmentIds"))) {
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
        String status = request.getParameter("tripStatus");

        List<Trip> quoteList = tripService.retrieveQuotes(deptIds, status);

        map.put(PARAM_RECORDS, quoteList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveTrips", method = POST)
    @ResponseBody
    public Object retrieveTrips(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        String category = request.getParameter("tripCategory");
        List<Trip> tripList = tripService.retrieveTrips(deptIds, category);

        map.put(PARAM_RECORDS, tripList);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveInvoice", method = POST)
    @ResponseBody
    public Object retrieveInvoice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String invoiceId = request.getParameter("invoiceid");
        Map<String, Object> map = newHashMap();
        Invoice invoice = tripService.retrieveInvoice(parseInt(invoiceId));

        return fillInvoiceExtra(map, invoice);
    }

    @RequestMapping(value = "/releaseTripLock", method = GET)
    @ResponseBody
    public Object releaseTripLock(HttpServletRequest request, HttpServletResponse response) throws Exception {
        tripService.releaseAllConcurrencyLock(ApplicationSession.get().getUid());
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/loadTrip", method = POST)
    @ResponseBody
    public Object loadTrip(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripId = request.getParameter("tripid");
        String tripMode = request.getParameter("tripmode");
        String tripType = request.getParameter("triptype");
        Integer departmentId = 0;
        if (!isBlank(request.getParameter("deptid")))
            departmentId = Integer.parseInt(request.getParameter("deptid"));

        Map<String, Object> map = newHashMap();
        if ("quote".equals(tripType)) {
            return loadQuote(tripType, tripId, tripMode, departmentId, map);
        }
        if ("trip".equals(tripType)) {
            return loadTrip(tripType, tripId, tripMode, departmentId, map);
        }
        if ("pickup".equals(tripType)) {
            return loadTrip(tripType, tripId, tripMode, departmentId, map);
        }
        if ("bol".equals(tripType)) {
            return loadTrip(tripType, tripId, tripMode, departmentId, map);
        }
        if ("invoice".equals(tripType)) {
            return loadInvoice(tripId, tripMode, map);
        }
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    private Object loadInvoice(String tripId, String tripMode, Map<String, Object> map) throws Exception {
        Invoice invoice = tripService.prepareBlankInvoice(parseInt(tripId));
        List<Invoice> list = tripService.retrieveInvoicesByTrip(parseInt(tripId));
        if (null != list && 0 != list.size()) {
            if (StringUtils.isNotEmpty(list.get(0).getBillingCurrency())) {
                invoice.setBillingCurrency(list.get(0).getBillingCurrency());
                map.put("keepCurrency", true);
            } else {
                map.put("keepCurrency", false);
            }
        } else {
            map.put("keepCurrency", false);
        }

        return fillInvoiceExtra(map, invoice);
    }

    private Object fillInvoiceExtra(Map<String, Object> map, Invoice invoice) throws Exception {
        if (equal(invoice, null)) throw new BusinessException("Invoice is empty");

        // client list
        List<CustomerLocation> clients = customerService.retrieveCustomerLocationByFuzzySearch(EMPTY, CUSTOMER_TYPE_CLIENT);
        // shipper list
        List<CustomerLocation> shippers = customerService.retrieveCustomerLocationByFuzzySearch(EMPTY, CUSTOMER_TYPE_SHIPPER);

        // selected invoice to
        CustomerLocation billedClient = new CustomerLocation();
        if (!equal(null, invoice.getBilledClientId()))
            billedClient = customerService.selectCustomerLocationByIdForTrip(invoice.getBilledClientId());


        // selected shipper
        CustomerLocation shipper = new CustomerLocation();
        if (!equal(null, invoice.getShipperId()))
            shipper = customerService.selectCustomerLocationByIdForTrip(invoice.getShipperId());

        map.put(PARAM_SELECTED_SHIPPER, shipper);
        map.put(PARAM_SELECTED_BILLED_CLIENT, billedClient);
        map.put(PARAM_CLIENTS_RECORDS, clients);
        map.put(PARAM_SHIPPERS_RECORDS, shippers);
        map.put(PARAM_RECORDS, invoice);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    private Object loadTrip(String loadType, String tripId, String tripMode, Integer departmentId, Map<String, Object> map) throws Exception {
        // trip info / freight list / T & C
        Trip trip = new Trip();
        try {
            trip = loadTrip(loadType, tripId, tripMode, departmentId);
        } catch (Exception e) {
            if (e instanceof ConcurrentException) {
                trip = loadTrip(loadType, tripId, "read", 0);
                map.put(KEY_RESULT, RESPONSE_SUCCESS);
                map.put("readMsg", e.getMessage());
                map.put("readonly", true);
            } else {
                e.printStackTrace();
                throw new BusinessException("Load Trip error");
            }
        }
        if ((equal(trip.getStatus(), "ARC") || equal(trip.getStatus(), "CLO")) && !ApplicationSession.get().isAdmin()) {
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            map.put("readonly", true);
        }
        // all list
        List<CustomerLocation> clients = customerService.retrieveCustomerLocationByFuzzySearch(EMPTY, CUSTOMER_TYPE_ALL);
        // broker list
        List<CustomerLocation> brokers = customerService.retrieveCustomerLocationByFuzzySearch(EMPTY, CUSTOMER_TYPE_BROKER);

        boolean showEvents = true;
        // selected client
        CustomerLocation client = new CustomerLocation();
        if (null != trip && null != trip.getClientId()) {
            client = customerService.selectCustomerLocationByIdForTrip(trip.getClientId());
        } else {
            showEvents = false;
        }
        // selected consignee
        CustomerLocation consignee = new CustomerLocation();
        if (null != trip && null != trip.getConsigneeId()) {
            consignee = customerService.selectCustomerLocationByIdForTrip(trip.getConsigneeId());
        } else {
            showEvents = false;
        }
        // selected consignee2
        CustomerLocation consignee2 = new CustomerLocation();
        if (null != trip && null != trip.getConsigneeId2()) {
            consignee2 = customerService.selectCustomerLocationByIdForTrip(trip.getConsigneeId2());
        }
        // selected consignee3
        CustomerLocation consignee3 = new CustomerLocation();
        if (null != trip && null != trip.getConsigneeId3()) {
            consignee3 = customerService.selectCustomerLocationByIdForTrip(trip.getConsigneeId3());
        }
        // selected shipper
        CustomerLocation shipper = new CustomerLocation();
        if (null != trip && null != trip.getShipperId()) {
            shipper = customerService.selectCustomerLocationByIdForTrip(trip.getShipperId());
        } else {
            showEvents = false;
        }
        // selected shipper2
        CustomerLocation shipper2 = new CustomerLocation();
        if (null != trip && null != trip.getShipperId2()) {
            shipper2 = customerService.selectCustomerLocationByIdForTrip(trip.getShipperId2());
        }
        // selected shipper3
        CustomerLocation shipper3 = new CustomerLocation();
        if (null != trip && null != trip.getShipperId3()) {
            shipper3 = customerService.selectCustomerLocationByIdForTrip(trip.getShipperId3());
        }
        // selected broker
        CustomerLocation broker = new CustomerLocation();
        if (null != trip && null != trip.getBrokerId()) {
            broker = customerService.selectCustomerLocationByIdForTrip(trip.getBrokerId());
        }
        CustomerLocation broker2 = new CustomerLocation();
        if (null != trip && null != trip.getBrokerId2()) {
            broker2 = customerService.selectCustomerLocationByIdForTrip(trip.getBrokerId2());
        }
        // selected billed client
        CustomerLocation billedClient = new CustomerLocation();
        if (null != trip && null != trip.getBilledClientId()) {
            billedClient = customerService.selectCustomerLocationByIdForTrip(trip.getBilledClientId());
        }
        // selected agent
        CustomerLocation agent = new CustomerLocation();
        if (null != trip && null != trip.getAgentId()) {
            agent = customerService.selectCustomerLocationByIdForTrip(trip.getAgentId());
        }
        map.put(PARAM_SELECTED_CLIENT, client);
        map.put(PARAM_SELECTED_CONSIGNEE, consignee);
        map.put(PARAM_SELECTED_CONSIGNEE2, consignee2);
        map.put(PARAM_SELECTED_CONSIGNEE3, consignee3);
        map.put(PARAM_SELECTED_SHIPPER, shipper);
        map.put(PARAM_SELECTED_SHIPPER2, shipper2);
        map.put(PARAM_SELECTED_SHIPPER3, shipper3);
        map.put(PARAM_SELECTED_BROKER, broker);
        map.put(PARAM_SELECTED_BROKER2, broker2);
        map.put(PARAM_SELECTED_BILLED_CLIENT, billedClient);
        map.put(PARAM_SELECTED_AGENT, agent);
        map.put(PARAM_RECORDS, trip);
        map.put(PARAM_CLIENTS_RECORDS, clients);
        map.put(PARAM_CONSIGNEES_RECORDS, clients);
        map.put(PARAM_SHIPPERS_RECORDS, clients);
        map.put(PARAM_BROKERS_RECORDS, brokers);
        map.put(PARAM_AGENTS_RECORDS, clients);
        map.put("showEvents", showEvents);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/loadContacts", method = POST)
    @ResponseBody
    public Object loadContacts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String contactid = request.getParameter("contact");
        String keyword = request.getParameter("keyword");
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        CustomerContact selected = new CustomerContact();
        //TODO
        List<CustomerContact> contacts = customerService.retrieveCustomerContactForEmail(keyword);
        if (isNotEmpty(contactid)) {
            selected = customerService.selectCustomerContactById(parseInt(contactid));
        }
        map.put(PARAM_SELECTED_CONTACT, selected);
        map.put(PARAM_CONTACTS_RECORDS, contacts);
        return map;
    }

    @RequestMapping(value = "/loadLocations", method = POST)
    @ResponseBody
    public Object loadLocations(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String locationId = request.getParameter("locationId");
        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        CustomerLocation selected = new CustomerLocation();
        //TODO
        List<CustomerLocation> clients = newArrayList();
        List<CustomerLocation> brokers = newArrayList();
        if (isBlank(type))
            clients = customerService.retrieveCustomerLocationContactByFuzzySearch(keyword, CUSTOMER_TYPE_ALL);
        else
            brokers = customerService.retrieveCustomerLocationContactByFuzzySearch(keyword, CUSTOMER_TYPE_BROKER);

        if (isNotEmpty(locationId)) {
            selected = customerService.selectCustomerLocationByIdForTrip(parseInt(locationId));
        }
        map.put("selected_location", selected);
        map.put("clients", clients);
        map.put("brokers", brokers);
        return map;
    }

    @RequestMapping(value = "/loadLocationOrContacts", method = POST)
    @ResponseBody
    public Object loadLocationOrContacts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String locationId = request.getParameter("locationId");
        String contactId = request.getParameter("contactId");
        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        CustomerLocation selected = new CustomerLocation();
        //TODO
        List<CustomerLocation> clients = newArrayList();
        List<CustomerLocation> brokers = newArrayList();
        clients = customerService.retrieveCustomerLocationContactByFuzzySearch(keyword, CUSTOMER_TYPE_ALL);

        if (isNotEmpty(contactId) && !contactId.equals("0")) {
            selected = customerService.selectCustomerLocationByContactId(parseInt(contactId));
        } else if (isNotEmpty(locationId)) {
            selected = customerService.selectCustomerLocationByIdForTrip(parseInt(locationId));
        }
        map.put("selected_location", selected);
        map.put("clients", clients);
        return map;
    }

    @RequestMapping(value = "/loadLocation", method = POST)
    @ResponseBody
    public Object loadLocation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String locationId = request.getParameter("locationId");

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        CustomerLocation selected = new CustomerLocation();
        if (isNotEmpty(locationId)) {
            selected = customerService.selectCustomerLocationByIdForTrip(parseInt(locationId));
        }
        map.put("selected_location", selected);
        return map;
    }

    private Trip loadTrip(String loadType, String tripId, String tripMode, Integer departmentId) throws Exception {
        if ("add".equals(tripMode)) {
            // new Trip
            return tripService.touchTrip(departmentId);
        }
        if ("fromTripTemplate".equals(tripMode)) {
            return tripService.createTripFromTemplate(parseInt(tripId));
        }
        if ("edit".equals(tripMode) || "readnote".equals(tripMode) || "focusnote".equals(tripMode) || "commcenter".equals(tripMode)
                || "fromQuote".equals(tripMode)) {
            // find a trip by id
            return tripService.retrieveTripForEditing(loadType, parseInt(tripId), true);
        }
        if ("read".equals(tripMode)) {
            return tripService.retrieveTripById(loadType, parseInt(tripId), true);
        }
        return new Trip();
    }

    private Object loadQuote(String loadType, String tripId, String tripMode, Integer departmentId, Map<String, Object> map) throws Exception {
        // trip info / freight list / T & C
        Trip quote = new Trip();
        try {
            quote = loadQuote(loadType, tripId, tripMode, departmentId);
        } catch (Exception e) {
            if (e instanceof ConcurrentException) {
                quote = loadQuote(loadType, tripId, "read", 0);
                map.put(KEY_RESULT, RESPONSE_SUCCESS);
                map.put("readMsg", e.getMessage());
                map.put("readonly", true);
            } else {
                e.printStackTrace();
                throw new BusinessException("Load Trip error");
            }
        }
        // client list
        List<CustomerLocation> clients = customerService.retrieveCustomerLocationByFuzzySearch(EMPTY, CUSTOMER_TYPE_ALL);
        // selected client
        CustomerLocation client = new CustomerLocation();
        if (null != quote && null != quote.getClientId()) {
            client = customerService.selectCustomerLocationByIdForTrip(quote.getClientId());
        }
        // selected consignee
        CustomerLocation consignee = new CustomerLocation();
        if (null != quote && null != quote.getConsigneeId()) {
            consignee = customerService.selectCustomerLocationByIdForTrip(quote.getConsigneeId());
        }
        // selected shipper
        CustomerLocation shipper = new CustomerLocation();
        if (null != quote && null != quote.getShipperId()) {
            shipper = customerService.selectCustomerLocationByIdForTrip(quote.getShipperId());
        }
        map.put(PARAM_SELECTED_CLIENT, client);
        map.put(PARAM_CLIENTS_RECORDS, clients);
        map.put(PARAM_SELECTED_CONSIGNEE, consignee);
        map.put(PARAM_CONSIGNEES_RECORDS, clients);
        map.put(PARAM_SELECTED_SHIPPER, shipper);
        map.put(PARAM_SHIPPERS_RECORDS, clients);
        map.put(PARAM_RECORDS, quote);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    private Trip loadQuote(String loadType, String tripId, String tripMode, Integer departmentId) throws Exception {
        if ("add".equals(tripMode)) {
            // new Quote
            return tripService.touchQuote(departmentId);
        }
        if ("edit".equals(tripMode)) {
            // find a quote by id
            return tripService.retrieveTripForEditing(loadType, parseInt(tripId), true);
        }
        if ("read".equals(tripMode) || "commcenter".equals(tripMode)) {
            // find a quote by id
            return tripService.retrieveTripById(loadType, parseInt(tripId), true);
        }
        return new Trip();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @RequestMapping(value = "/loadCosts", method = POST)
    @ResponseBody
    public Object loadCosts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        List<TripCost> findAll = tripService.retrieveTripCostsByQuoteId(parseInt(request.getParameter("tripid")));
        map.put(PARAM_RECORDS, findAll);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/loadFreights", method = POST)
    @ResponseBody
    public Object loadFreights(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        List<Freight> findAll = tripService.retrieveFreightsByQuoteId(parseInt(request.getParameter("tripid")));
        map.put(PARAM_RECORDS, findAll);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/loadNotes", method = POST)
    @ResponseBody
    public Object loadNotes(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        List<TripNote> findAll = tripService.retrieveNotes(parseInt(request.getParameter("tripid")));
        map.put(PARAM_RECORDS, findAll);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/loadInvoices", method = POST)
    @ResponseBody
    public Object loadInvoices(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        List<Invoice> findAll = tripService.retrieveInvoicesByTrip(parseInt(request.getParameter("tripid")));
        map.put(PARAM_RECORDS, findAll);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/saveNote", method = POST)
    @ResponseBody
    public Object saveNote(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> result = newHashMap();
        String id = request.getParameter("id");
        String note = request.getParameter("desc");
        Integer tripid = Integer.parseInt(request.getParameter("tripid"));
        if (isBlank(id)) {
            tripService.createNote(note, tripid);
        } else {
            TripNote tripNote = new TripNote();
            tripNote.setId(Integer.parseInt(id));
            tripNote.setContent(note);
            tripNote.setStatus(null);
            tripNote.setTripId(tripid);
            tripService.updateNote(tripNote);
        }

        result.put(KEY_RESULT, RESPONSE_SUCCESS);
        return result;
    }

    @RequestMapping(value = "/deleteNote", method = RequestMethod.GET)
    @ResponseBody
    public Object deleteNote(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> result = newHashMap();
        Integer id = Integer.parseInt(request.getParameter("id"));
        tripService.deleteNote(id);
        result.put(KEY_RESULT, RESPONSE_SUCCESS);
        return result;
    }

    @RequestMapping(value = "/loadDocs", method = POST)
    @ResponseBody
    public Object loadDocs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        String type = request.getParameter("fileType");

        if (StringUtils.isNotEmpty(type) && !type.equals("NoEmail")) {
            List<TripDocument> list = tripService.retrieveTripDocumentsByType(parseInt(request.getParameter("tripid")), type);
            map.put(PARAM_RECORDS, list);
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }

        List<TripDocument> findAll = tripService.retrieveDocuments(parseInt(request.getParameter("tripid")), type);
        map.put(PARAM_RECORDS, findAll);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;

    }

    @RequestMapping(value = "/retrieveQuoteTreeByTripID", method = POST)
    @ResponseBody
    public Object retrieveQuoteTreeByTripID(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        List<Condition> findAll = tripService.retrieveQuoteTreeByTripID(parseInt(request.getParameter("tripId")));
        List<Map<String, Object>> result = systemMaintenanceService.retrieveQuoteTemplateTree(findAll);
        map.put(PARAM_RECORDS, result);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/resetQuoteTreeByTripID", method = POST)
    @ResponseBody
    public Object resetQuoteTreeByTripID(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        List<Condition> findAll = tripService.resetConditions(parseInt(request.getParameter("tripId")));
        List<Map<String, Object>> result = systemMaintenanceService.retrieveQuoteTemplateTree(findAll);
        map.put(PARAM_RECORDS, result);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/importEventByTripID", method = POST)
    @ResponseBody
    public Object importEventByTripID(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        String templateIds = request.getParameter("entityIds");
        List<Integer> templates = newArrayList();
        if (!isBlank(templateIds)) {
            for (String id : templateIds.split(",")) {
                if (id != null && parseInt(id) > 0) {
                    templates.add(parseInt(id));
                }
            }
        }
        List<TripEvent> result = tripService.addEvents(parseInt(request.getParameter("tripId")), templates);

        List<Map<String, Object>> tree = tripService.retrieveTripEventTree(result);
        map.put(PARAM_RECORDS, tree);
        map.put("export", result);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/saveConditionTree", method = POST)
    @ResponseBody
    public Object saveConditionTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String list = request.getParameter("list");

        if (!isBlank(list)) {
            Integer id;
            Integer categorySequence;
            Integer sequence;

            List<Map<String, Object>> maps = JacksonMapper.getInstance().readValue(list, List.class);

            if (null != maps && 0 != maps.size()) {
                for (Map<String, Object> map : maps) {
                    id = Integer.parseInt(map.get("id") + "");
                    if (id > 0) {
                        categorySequence = Integer.parseInt(map.get("categorySeq") + "") + 1;
                        sequence = Integer.parseInt(map.get("itemSeq") + "") + 1;
                        tripService.updateConditionSequence(id, categorySequence, sequence);
                    }
                }
            }
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/saveEventTree", method = POST)
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
                    if (id > 0) {
                        categorySequence = Integer.parseInt(map.get("categorySeq") + "") + 1;
                        sequence = Integer.parseInt(map.get("itemSeq") + "") + 1;
                        tripService.updateTripEventSequence(id, categorySequence, sequence);
                    }
                }
            }
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/removeEventCost", method = GET)
    @ResponseBody
    public Object removeEventCost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String costid = request.getParameter("id");
        if (isEmpty(costid)) throw new BusinessException("cost id is empty");
        tripService.removeTripCost(parseInt(costid));
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveEvents", method = POST)
    @ResponseBody
    public Object retrieveEvents(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        List<TripEvent> result = tripService.retrieveEvents(parseInt(request.getParameter("tripId")));
        List<Map<String, Object>> tree = tripService.retrieveTripEventTree(result);
        map.put("export", result);
        map.put(PARAM_RECORDS, tree);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveEventNotifiesByEventId", method = POST)
    @ResponseBody
    public Object retrieveEventNotifiesByEventId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        List<TripEventNotify> findAll = tripService.retrieveEventNotifiesByEventId(parseInt(request.getParameter("eventId")));
        map.put(PARAM_RECORDS, findAll);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/updateEventNotify", method = POST)
    @ResponseBody
    public Object updateEventNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        String eventId = request.getParameter("eventId");
        TripEventNotify tripEventNotify = new TripEventNotify();
        tripEventNotify.setId(parseInt(id));
        tripEventNotify.setEventId(parseInt(eventId));
        tripEventNotify.setName(trimToEmpty(request.getParameter("name")));
        tripEventNotify.setEmail(trimToEmpty(request.getParameter("email")));
        Map<String, Object> map = newHashMap();
        tripService.updateEventNotify(tripEventNotify);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/createEventNotify", method = POST)
    @ResponseBody
    public Object createEventNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String eventId = request.getParameter("eventId");
        TripEventNotify tripEventNotify = new TripEventNotify();
        tripEventNotify.setEventId(parseInt(eventId));
        tripEventNotify.setName(trimToEmpty(request.getParameter("name")));
        tripEventNotify.setEmail(trimToEmpty(request.getParameter("email")));
        Map<String, Object> map = newHashMap();
        tripService.createEventNotify(tripEventNotify);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/removeEventNotify", method = GET)
    @ResponseBody
    public Object removeEventNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        tripService.removeEventNotify(parseInt(request.getParameter("notifyId")));
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/updateTripCost", method = POST)
    @ResponseBody
    public Object updateTripCost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TripCost cost = new TripCost();
        cost.setId(parseInt(request.getParameter("id")));

        cost.setTripId(parseInt(request.getParameter("tripId")));

        cost.setChargeCode(request.getParameter("chargeCode"));
        if (StringUtils.isNotEmpty(request.getParameter("estCost"))) {
            cost.setEstCost(new BigDecimal(parseDouble(request.getParameter("estCost"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actCost"))) {
            cost.setActCost(new BigDecimal(parseDouble(request.getParameter("actCost"))));
        }
        /*
        if (StringUtils.isNotEmpty(request.getParameter("actUsedCost"))) {
            cost.setActUsedCost(new BigDecimal(parseDouble(request.getParameter("actUsedCost"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actUsedRate"))) {
            cost.setActUsedRate(new BigDecimal(parseDouble(request.getParameter("actUsedRate"))));
        }
        */
        String currencyCode;
        currencyCode = request.getParameter("actCurrency");
        cost.setActCurrency(currencyCode);
        BigDecimal usdRate = currencyService.retrieveUSDRate(currencyCode);
        BigDecimal usdCost = BigDecimal.ZERO;
        if (equal(null, usdRate)) {
            usdRate = BigDecimal.ZERO;
        } else {
            usdCost = new BigDecimal(parseDouble(request.getParameter("actCost")));
            usdCost = (usdRate.multiply(usdCost)).setScale(4, BigDecimal.ROUND_UP);
        }
        cost.setActUsedCost(usdCost);
        cost.setActUsedRate(usdRate);

        cost.setEstDate(toDate(request.getParameter("estDateStr"), LONG_DATE_FORMAT_SHOW));
        cost.setActDate(toDate(request.getParameter("actDateStr"), LONG_DATE_FORMAT_SHOW));
        cost.setEstCurrency(request.getParameter("estCurrency"));
        cost.setDescription(request.getParameter("description"));
        if (StringUtils.isNotEmpty(request.getParameter("eventId"))) {
            cost.setEventId(parseInt(request.getParameter("eventId")));
        }
        cost.setEventItem(request.getParameter("eventItem"));
        cost.setVisible(request.getParameter("visible"));
        if (StringUtils.isNotEmpty(request.getParameter("linkedEntity"))) {
            cost.setLinkedEntity(parseInt(trimToEmpty(request.getParameter("linkedEntity"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("linkedEntityContact"))) {
            cost.setLinkedEntityContact(parseInt(trimToEmpty(request.getParameter("linkedEntityContact"))));
        }
        tripService.updateTripCost(cost);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/createTripCost", method = POST)
    @ResponseBody
    public Object createTripCost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TripCost cost = new TripCost();

        cost.setTripId(parseInt(request.getParameter("tripId")));
        cost.setChargeCode(request.getParameter("chargeCode"));
        if (StringUtils.isNotEmpty(request.getParameter("estCost"))) {
            cost.setEstCost(new BigDecimal(parseDouble(request.getParameter("estCost"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actCost"))) {
            cost.setActCost(new BigDecimal(parseDouble(request.getParameter("actCost"))));
        }
        /*
        if (StringUtils.isNotEmpty(request.getParameter("actUsedCost"))) {
            cost.setActUsedCost(new BigDecimal(parseDouble(request.getParameter("actUsedCost"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actUsedRate"))) {
            cost.setActUsedRate(new BigDecimal(parseDouble(request.getParameter("actUsedRate"))));
        }
        */
        String currencyCode;
        currencyCode = request.getParameter("actCurrency");
        cost.setActCurrency(currencyCode);
        BigDecimal usdRate = currencyService.retrieveUSDRate(currencyCode);
        BigDecimal usdCost = BigDecimal.ZERO;
        if (equal(null, usdRate)) {
            usdRate = BigDecimal.ZERO;
        } else {
            if (StringUtils.isNotEmpty(request.getParameter("actCost"))) {
                usdCost = new BigDecimal(parseDouble(request.getParameter("actCost")));
                usdCost = (usdRate.multiply(usdCost)).setScale(4, BigDecimal.ROUND_UP);
            }
        }
        cost.setActUsedCost(usdCost);
        cost.setActUsedRate(usdRate);

        cost.setEstDate(toDate(request.getParameter("estDateStr"), LONG_DATE_FORMAT_SHOW));
        cost.setActDate(toDate(request.getParameter("actDateStr"), LONG_DATE_FORMAT_SHOW));
        cost.setEstCurrency(request.getParameter("estCurrency"));
        cost.setDescription(request.getParameter("description"));
        if (StringUtils.isNotEmpty(request.getParameter("eventId"))) {
            cost.setEventId(parseInt(request.getParameter("eventId")));
        }

        cost.setEventItem(request.getParameter("eventItem"));
        cost.setVisible(request.getParameter("visible"));
        if (StringUtils.isNotEmpty(request.getParameter("linkedEntity"))) {
            cost.setLinkedEntity(parseInt(trimToEmpty(request.getParameter("linkedEntity"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("linkedEntityContact"))) {
            cost.setLinkedEntityContact(parseInt(trimToEmpty(request.getParameter("linkedEntityContact"))));
        }
        Map<String, Object> map = newHashMap();
        tripService.createTripCost(cost);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/validTripCostEntity", method = GET)
    @ResponseBody
    public Object validTripCostEntity(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isNotEmpty(request.getParameter("eventId"))) {
            int eventId = Integer.parseInt(request.getParameter("eventId"));

        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/removeTripCost", method = GET)
    @ResponseBody
    public Object removeTripCost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        tripService.removeTripCost(parseInt(request.getParameter("costId")));
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/resetEvents", method = POST)
    @ResponseBody
    public Object resetEvents(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        List<TripEvent> findAll = tripService.resetEvents(parseInt(request.getParameter("tripId")));
        List<Map<String, Object>> result = tripService.retrieveTripEventTree(findAll);
        map.put(PARAM_RECORDS, result);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/importTemplateByTripID", method = POST)
    @ResponseBody
    public Object importTemplateByTripID(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        String templateIds = request.getParameter("entityIds");
        ArrayList templates = new ArrayList();
        if (!isBlank(templateIds)) {
            for (String id : templateIds.split(",")) {
                if (id != null && parseInt(id) > 0) {
                    templates.add(parseInt(id));
                }
            }
        }
        List<Condition> findAll = tripService.addConditions(parseInt(request.getParameter("tripId")), templates);

        List<Map<String, Object>> result = systemMaintenanceService.retrieveQuoteTemplateTree(findAll);
        map.put(PARAM_RECORDS, result);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/removeEvent", method = GET)
    @ResponseBody
    public Object removeEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = Integer.parseInt(request.getParameter("id"));
        tripService.removeEvent(id);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/removeEvents", method = GET)
    @ResponseBody
    public Object removeEvents(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        String eventIds = request.getParameter("ids");
        if (StringUtils.isEmpty(eventIds)) {
            map.put(PARAM_RECORDS, newArrayList());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }

        if (tripService.hasCostDataByEventIds(eventIds + ",") > 0) {
            throw new BusinessException("There are cost(s) associated with the selected event(s), Remove all the cost(s) before deleting event(s)!");
        }

        Iterable<String> ids = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(eventIds);
        List<String> idsStr = newArrayList(ids);
        Integer[] idsArray = new Integer[idsStr.size()];
        for (int i = 0; i < idsArray.length; i++) {
            idsArray[i] = parseInt(idsStr.get(i));
        }
        tripService.removeEvent(idsArray);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/markComplete", method = GET)
    @ResponseBody
    public Object markComplete(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = newHashMap();
        if (StringUtils.isEmpty(request.getParameter("ids"))) {
            map.put(PARAM_RECORDS, newArrayList());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        if (StringUtils.isEmpty(request.getParameter("markedComplete"))) {
            map.put(PARAM_RECORDS, newArrayList());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        Iterable<String> ids = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split(request.getParameter("ids"));
        List<String> idsStr = newArrayList(ids);
        Integer[] idsArray = new Integer[idsStr.size()];
        for (int i = 0; i < idsArray.length; i++) {
            idsArray[i] = parseInt(idsStr.get(i));
        }
        Integer markedComplete = Integer.parseInt(request.getParameter("markedComplete"));

        tripService.updateEventToComplte(idsArray, markedComplete);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/replicateEmailAddressForAllEvents", method = POST)
    @ResponseBody
    public Object replicateEmailAddressForAllEvents(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer tripId = parseInt(request.getParameter("tripId"));
        Integer eventId = parseInt(request.getParameter("eventId"));
        String notifiesStr = request.getParameter("notifies");
        List<TripEventNotify> notifies = newArrayList();
        if (StringUtils.isNotEmpty(notifiesStr)) {
            List<Map<String, Object>> notifyList = JacksonMapper.getInstance().readValue(notifiesStr, List.class);
            if (!CollectionUtils.isEmpty(notifyList)) {
                for (Map<String, Object> map : notifyList) {
                    TripEventNotify notify = new TripEventNotify();
                    if (!equal(null, map.get("email")) && isNotEmpty(String.valueOf(map.get("email")))) {
                        notify.setEmail(String.valueOf(map.get("email")));
                    }
                    if (!equal(null, map.get("eventId")) && isNotEmpty(String.valueOf(map.get("eventId")))) {
                        notify.setEventId(parseInt(String.valueOf(map.get("eventId"))));
                    }
                    if (!equal(null, map.get("name")) && isNotEmpty(String.valueOf(map.get("name")))) {
                        notify.setName(String.valueOf(map.get("name")));
                    }
                    notifies.add(notify);

                }
            }
        }
        tripService.replicateEmailAddressForAllEvents(notifies, eventId, tripId);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/updateEvent", method = POST)
    @ResponseBody
    public Object updateEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TripEvent e = new TripEvent();
        e.setTripId(parseInt(request.getParameter("tripID")));
        e.setId(parseInt(request.getParameter("id")));
        e.setName(request.getParameter("name"));
        e.setCategory(request.getParameter("category"));
        if (StringUtils.isNotEmpty(request.getParameter("sequence"))) {
            e.setSequence(parseInt(request.getParameter("sequence")));
        }
        if (!isBlank(request.getParameter("categorySequence"))) {
            e.setCategorySequence(Integer.parseInt(request.getParameter("categorySequence")));
        }
        e.setType(request.getParameter("type"));
        e.setItem(request.getParameter("item"));
        e.setDescription(request.getParameter("description"));
        e.setCode(request.getParameter("code"));
        if (equalsIgnoreCase(trimToEmpty(request.getParameter("code")), "POD") && equalsIgnoreCase(trimToEmpty(request.getParameter("category")), "Delivery")) {
            e.setPodDate(toDate(request.getParameter("podDate"), DATE_TIME_FARMAT_YYYYMMDDHHMM));
            e.setPodName(request.getParameter("podName"));
        }
        if (StringUtils.isNotEmpty(request.getParameter("linkEntity"))) {
            e.setLinkedEntity(parseInt(trimToEmpty(request.getParameter("linkEntity"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("linkEntityContact"))) {
            e.setLinkedEntityContact(parseInt(trimToEmpty(request.getParameter("linkEntityContact"))));
        }
        e.setCustomerNotify(request.getParameter("customerNotify"));
        e.setEventClass(request.getParameter("eventClass"));
        e.setEstimatedDate(toDate(request.getParameter("estimatedDate"), DATE_TIME_FARMAT_YYYYMMDDHHMM));
        e.setActualDate(toDate(request.getParameter("actualDate"), DATE_TIME_FARMAT_YYYYMMDDHHMM));
        TripEvent event = tripService.updateEvent(e);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, event);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/createEvent", method = POST)
    @ResponseBody
    public Object createEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TripEvent e = new TripEvent();
        e.setTripId(parseInt(request.getParameter("tripID")));
        e.setId(parseInt(request.getParameter("id")));
        e.setName(request.getParameter("name"));
        e.setCategory(request.getParameter("category"));
        if (isNotEmpty(request.getParameter("sequence"))) {
            e.setSequence(parseInt(request.getParameter("sequence")));
        }
        if (isNotEmpty(request.getParameter("categorySequence"))) {
            e.setCategorySequence(parseInt(request.getParameter("categorySequence")));
        }
        e.setType(request.getParameter("type"));
        e.setItem(request.getParameter("item"));
        e.setDescription(request.getParameter("description"));
        e.setCode(request.getParameter("code"));
        e.setEstimatedDate(toDate(request.getParameter("estimatedDate"), DATE_TIME_FARMAT_YYYYMMDDHHMM));
        e.setActualDate(toDate(request.getParameter("actualDate"), DATE_TIME_FARMAT_YYYYMMDDHHMM));
        if (StringUtils.isNotEmpty(request.getParameter("linkEntity"))) {
            e.setLinkedEntity(parseInt(trimToEmpty(request.getParameter("linkEntity"))));
        }
        if (equalsIgnoreCase(trimToEmpty(request.getParameter("code")), "POD") && equalsIgnoreCase(trimToEmpty(request.getParameter("code")), "Delivery")) {
            e.setPodDate(toDate(request.getParameter("podDate"), DATE_TIME_FARMAT_YYYYMMDDHHMM));
            e.setPodName(request.getParameter("podName"));
        }
        e.setCustomerNotify(request.getParameter("customerNotify"));
        e.setEventClass(request.getParameter("eventClass"));
        String costStr = request.getParameter("costs");
        if (StringUtils.isNotEmpty(costStr)) {
            List<Map<String, Object>> costList = JacksonMapper.getInstance().readValue(costStr, List.class);
            if (!CollectionUtils.isEmpty(costList)) {
                List<TripCost> costs = newArrayList();
                for (Map<String, Object> map : costList) {
                    TripCost cost = new TripCost();
                    cost.setId(null);
                    if (!equal(null, map.get("tripId")) && isNotEmpty(String.valueOf(map.get("tripId")))) {
                        cost.setTripId(parseInt(String.valueOf(map.get("tripId"))));
                    }
                    if (!equal(null, map.get("chargeCode")) && isNotEmpty(String.valueOf(map.get("chargeCode")))) {
                        cost.setChargeCode(String.valueOf(map.get("chargeCode")));
                    }
                    if (!equal(null, map.get("estCost")) && isNotEmpty(String.valueOf(map.get("estCost")))) {
                        cost.setEstCost(new BigDecimal(parseDouble(String.valueOf(map.get("estCost")))));
                    }
                    if (!equal(null, map.get("actCost")) && isNotEmpty(String.valueOf(map.get("actCost")))) {
                        cost.setActCost(new BigDecimal(parseDouble(String.valueOf(map.get("actCost")))));
                    }
                    /*
                    if (!equal(null, map.get("actUsedCost")) && isNotEmpty(String.valueOf(map.get("actUsedCost")))) {
                        cost.setActUsedCost(new BigDecimal(parseDouble(String.valueOf(map.get("actUsedCost")))));
                    }
                    if (!equal(null, map.get("actUsedRate")) && isNotEmpty(String.valueOf(map.get("actUsedRate")))) {
                        cost.setActUsedRate(new BigDecimal(parseDouble(String.valueOf(map.get("actUsedRate")))));
                    }
                    */
                    BigDecimal usdRate = BigDecimal.ZERO;
                    BigDecimal usdCost = BigDecimal.ZERO;
                    if (!equal(null, map.get("actCurrency")) && isNotEmpty(String.valueOf(map.get("actCurrency")))) {
                        String currencyCode;
                        currencyCode = String.valueOf(map.get("actCurrency"));
                        cost.setActCurrency(currencyCode);
                        usdRate = currencyService.retrieveUSDRate(currencyCode);
                        if (equal(null, usdRate)) {
                            usdRate = BigDecimal.ZERO;
                        } else {
                            if (!equal(null, map.get("actCost")) && isNotEmpty(String.valueOf(map.get("actCost")))) {
                                usdCost = new BigDecimal(parseDouble(String.valueOf(map.get("actCost"))));
                                usdCost = (usdRate.multiply(usdCost)).setScale(4, BigDecimal.ROUND_UP);
                            }
                        }
                    }
                    cost.setActUsedCost(usdCost);
                    cost.setActUsedRate(usdRate);

                    if (!equal(null, map.get("estDate")) && isNotEmpty(String.valueOf(map.get("estDate")))) {
                        cost.setEstDate(toDate(String.valueOf(map.get("estDate")), LONG_DATE_FORMAT_SHOW));
                    }
                    if (!equal(null, map.get("actDate")) && isNotEmpty(String.valueOf(map.get("actDate")))) {
                        cost.setActDate(toDate(String.valueOf(map.get("actDate")), LONG_DATE_FORMAT_SHOW));
                    }

                    if (!equal(null, map.get("estCurrency")) && isNotEmpty(String.valueOf("estCurrency"))) {
                        cost.setEstCurrency(String.valueOf(map.get("estCurrency")));
                    }
                    if (!equal(null, map.get("description")) && isNotEmpty(String.valueOf("description"))) {
                        cost.setDescription(String.valueOf(map.get("description")));
                    }
                    if (!equal(null, map.get("eventId")) && isNotEmpty(String.valueOf(map.get("eventId")))) {
                        cost.setEventId(parseInt(String.valueOf(map.get("eventId"))));
                    }
                    if (!equal(null, map.get("eventItem")) && isNotEmpty(String.valueOf(map.get("eventItem")))) {
                        cost.setEventItem(String.valueOf(map.get("eventItem")));
                    }
                    if (!equal(null, map.get("visible")) && isNotEmpty(String.valueOf(map.get("visible")))) {
                        cost.setVisible(String.valueOf(map.get("visible")));
                    }
                    if (!equal(null, map.get("linkEntity")) && isNotEmpty(String.valueOf(map.get("linkEntity")))) {
                        cost.setLinkedEntity(parseInt(String.valueOf(map.get("linkEntity"))));
                    }
                    if (!equal(null, map.get("linkEntityContact")) && isNotEmpty(String.valueOf(map.get("linkEntityContact")))) {
                        cost.setLinkedEntityContact(parseInt(String.valueOf(map.get("linkEntityContact"))));
                    }
                    costs.add(cost);
                }
                e.setCosts(costs);
            }
        }

        String notifiesStr = request.getParameter("notifies");
        if (StringUtils.isNotEmpty(notifiesStr)) {
            List<Map<String, Object>> notifyList = JacksonMapper.getInstance().readValue(notifiesStr, List.class);
            if (!CollectionUtils.isEmpty(notifyList)) {
                List<TripEventNotify> notifies = newArrayList();
                for (Map<String, Object> map : notifyList) {
                    TripEventNotify notify = new TripEventNotify();

                    if (!equal(null, map.get("email")) && isNotEmpty(String.valueOf(map.get("email")))) {
                        notify.setEmail(String.valueOf(map.get("email")));
                    }
                    if (!equal(null, map.get("eventId")) && isNotEmpty(String.valueOf(map.get("eventId")))) {
                        notify.setEventId(parseInt(String.valueOf(map.get("eventId"))));
                    }

                    if (!equal(null, map.get("name")) && isNotEmpty(String.valueOf(map.get("name")))) {
                        notify.setName(String.valueOf(map.get("name")));
                    }
                    notifies.add(notify);

                }
                e.setNotifies(notifies);
            }
        }

        TripEvent event = tripService.createEvent(e);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        map.put(PARAM_RECORDS, event);
        return map;
    }

    @RequestMapping(value = "/retrieveEventById", method = POST)
    @ResponseBody
    public Object retrieveEventById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String eId = request.getParameter("eid");
        Map<String, Object> map = newHashMap();
        try {
            TripEvent event = tripService.retrieveEventById(parseInt(eId));
            List<TripEventNotify> tripEventNotifies = tripService.retrieveEventNotifiesByEventId(parseInt(eId));
            List<TripCost> tripCosts = tripService.retrieveTripCostsByEventId(parseInt(eId));
            map.put("notifies", tripEventNotifies);
            map.put("costs", tripCosts);
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            map.put(PARAM_RECORDS, event);
        } catch (Exception e) {
            map.put(KEY_RESULT, RESPONSE_ERROR);
        }
        return map;
    }

    @RequestMapping(value = "/getDepartmentDropDownList", method = GET)
    @ResponseBody
    public Object getDepartmentDropDownList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return departmentManagementController.getDepartmentDropDownList(request, response);
    }

    @RequestMapping(value = "/getCurrentUserDepts", method = GET)
    @ResponseBody
    public Object getCurrentUserDepts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Organization> list = ApplicationSession.get().getDepartments();
        List<Map<String, String>> mapList = newArrayList();
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

    @RequestMapping(value = "/getEpCodeListByType", method = GET)
    @ResponseBody
    public Object getEpCodeListByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("type").equals("Email Label"))
            return businessCodeController.getEmailLabelList(request, response);
        else
            return businessCodeController.getEpCodeListByType(request, response);
    }

    @RequestMapping(value = "/getEpCodeById", method = GET)
    @ResponseBody
    public Object getEpCodeById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return businessCodeController.getEpCodeById(request, response);
    }

    @RequestMapping(value = "/retrieveConditionById", method = POST)
    @ResponseBody
    public Object retrieveConditionById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String eId = request.getParameter("eid");
        Map<String, Object> map = newHashMap();
        try {
            Condition event = tripService.retrieveConditionById(parseInt(eId));
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            map.put(PARAM_RECORDS, event);
        } catch (Exception e) {
            map.put(KEY_RESULT, RESPONSE_ERROR);
        }
        return map;
    }

    @RequestMapping(value = "/removeCondition", method = GET)
    @ResponseBody
    public Object removeCondition(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer id = Integer.parseInt(request.getParameter("id"));
        tripService.removeCondition(id);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/updateCondition", method = POST)
    @ResponseBody
    public Object updateCondition(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Condition e = new Condition();
        e.setTripId(parseInt(request.getParameter("tripID")));
        e.setId(parseInt(request.getParameter("id")));
        e.setName(request.getParameter("name"));
        e.setCategory(request.getParameter("category"));
        e.setSequence(Integer.parseInt(request.getParameter("sequence")));
        e.setType(request.getParameter("type"));
        e.setItem(request.getParameter("item"));
        e.setDescription(request.getParameter("description"));
        // e.setStatus(request.getParameter("status"));
        tripService.updateCondition(e);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, e.getId());
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/addCondition", method = POST)
    @ResponseBody
    public Object addCondition(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Condition e = new Condition();
        e.setTripId(parseInt(request.getParameter("tripID")));
        e.setName(request.getParameter("name"));
        e.setCategory(request.getParameter("category"));
        e.setSequence(Integer.parseInt(request.getParameter("sequence")));
        e.setType(request.getParameter("type"));
        e.setItem(request.getParameter("item"));
        e.setDescription(request.getParameter("description"));
        // e.setStatus(request.getParameter("status"));

        tripService.addCondition(e);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, e.getId());
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/createQuote", method = POST)
    @ResponseBody
    public Object createQuote(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Trip trip = new Trip();
        String expireDate = request.getParameter("expireDate");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date d = sdf.parse(expireDate);
            trip.setExpireDate(d);
        } catch (Exception e) {
            // TODO handle exception
        }
        Integer tripID = parseInt(request.getParameter("tripID"));
        String tripType = request.getParameter("tripType");
        String tripStatus = request.getParameter("tripStatus");
        if (StringUtils.isEmpty(tripStatus)) {
            throw new BusinessException("Quote Status can not be null");
        }
        trip.setId(tripID);
        trip.setType(tripType);
        trip.setStatus(tripStatus);
        Integer dept = null;
        if (isNotEmpty(request.getParameter("departmentId"))) dept = parseInt(request.getParameter("departmentId"));
        trip.setDepartmentId(dept);
        if (StringUtils.isNotEmpty(request.getParameter("clientID"))) {
            trip.setClientId(parseInt(request.getParameter("clientID")));
        }
        if (!isBlank(request.getParameter("consigneeID")))
            trip.setConsigneeId(parseInt(request.getParameter("consigneeID")));
        if (!isBlank(request.getParameter("shipperID")))
            trip.setShipperId(parseInt(request.getParameter("shipperID")));
        if (StringUtils.isNotEmpty(request.getParameter("billedClientID")))
            trip.setBilledClientId(parseInt(request.getParameter("billedClientID")));
        trip.setDropCosigneeName(trimToEmpty(request.getParameter("dropCosigneeName")));
        trip.setDropShipperName(trimToEmpty(request.getParameter("dropShipperName")));
        trip.setAuthorizedBy(request.getParameter("authorizedBy"));
        trip.setAuthorizationNo(request.getParameter("authorizationNo"));
        tripService.createQuote(trip);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/updateQuote", method = POST)
    @ResponseBody
    public Object updateQuote(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Trip trip = new Trip();
        Integer tripID = parseInt(request.getParameter("tripID"));
        String expireDate = request.getParameter("expireDate");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date d = sdf.parse(expireDate);
            trip.setExpireDate(d);
        } catch (Exception e) {
            // TODO handle exception
        }

        String tripType = request.getParameter("tripType");
        String tripStatus = request.getParameter("tripStatus");
        Integer dept = null;
        if (isNotEmpty(request.getParameter("departmentId"))) dept = parseInt(request.getParameter("departmentId"));
        trip.setDepartmentId(dept);
        if (StringUtils.isEmpty(tripStatus)) {
            throw new BusinessException("Quote Status can not be null");
        }
        trip.setId(tripID);
        trip.setType(tripType);
        trip.setStatus(tripStatus);

        if (StringUtils.isNotEmpty(request.getParameter("clientID")))
            trip.setClientId(parseInt(request.getParameter("clientID")));
        if (StringUtils.isNotEmpty(request.getParameter("consigneeID")))
            trip.setConsigneeId(parseInt(request.getParameter("consigneeID")));
        if (StringUtils.isNotEmpty(request.getParameter("shipperID")))
            trip.setShipperId(parseInt(request.getParameter("shipperID")));
        if (StringUtils.isNotEmpty(request.getParameter("billedClientID")))
            trip.setBilledClientId(parseInt(request.getParameter("billedClientID")));
        trip.setDropCosigneeName(trimToEmpty(request.getParameter("dropCosigneeName")));
        trip.setDropShipperName(trimToEmpty(request.getParameter("dropShipperName")));
        trip.setAuthorizedBy(request.getParameter("authorizedBy"));
        trip.setAuthorizationNo(request.getParameter("authorizationNo"));

        tripService.updateQuote(trip);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/getEventTree", method = GET)
    @ResponseBody
    public Object getEventTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return tripEventTemplateController.getEventTree(request, response);
    }

    @RequestMapping(value = "/updateFreight", method = POST)
    @ResponseBody
    public Object updateFreight(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Freight freight = new Freight();
        freight.setTripId(parseInt(request.getParameter("tripId")));
        if (StringUtils.isEmpty(request.getParameter("id"))) {
            freight.setId(null);
        } else {
            freight.setId(parseInt(request.getParameter("id")));
        }
        freight.setItem(request.getParameter("item"));
        if (StringUtils.isNotEmpty(request.getParameter("estimatedCost"))) {
            freight.setEstimatedCost(new BigDecimal(parseDouble(request.getParameter("estimatedCost"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("estimatedWeight"))) {
            freight.setEstimatedWeight(new BigDecimal(parseDouble(request.getParameter("estimatedWeight"))));
        }
        freight.setDescription(request.getParameter("description"));
        freight.setEstimatedCurrency(request.getParameter("estimatedCurrency"));
        freight.setEstimatedDimension(request.getParameter("estimatedDimension"));
        freight.setEstimatedUOM(request.getParameter("estimatedUOM"));
        freight.setEstimatedCurrency(request.getParameter("estimatedCurrency"));
        freight.setBagtag(request.getParameter("bagtag"));
        if (StringUtils.isNotEmpty(request.getParameter("estimatedPieces"))) {
            freight.setEstimatedPieces(Integer.parseInt(request.getParameter("estimatedPieces")));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualDimension"))) {
            freight.setActualDimension(request.getParameter("actualDimension"));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualCurrency"))) {
            freight.setActualCurrency(request.getParameter("actualCurrency"));
        }
        if (StringUtils.isNotEmpty(request.getParameter("usdCost"))) {
            freight.setUsdCost(new BigDecimal(parseDouble(request.getParameter("usdCost"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("usdRate"))) {
            freight.setUsdRate(new BigDecimal(parseDouble(request.getParameter("usdRate"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualWeight"))) {
            freight.setActualWeight(new BigDecimal(parseDouble(request.getParameter("actualWeight"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("estimatedChargeWt"))) {
            freight.setEstimatedChargeWt(new BigDecimal(parseDouble(request.getParameter("estimatedChargeWt"))).setScale(4, ROUND_HALF_DOWN));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualChargeWt"))) {
            freight.setActualChargeWt(new BigDecimal(parseDouble(request.getParameter("actualChargeWt"))).setScale(4, ROUND_HALF_DOWN));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualCost"))) {
            freight.setActualCost(new BigDecimal(parseDouble(request.getParameter("actualCost"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualPieces"))) {
            freight.setActualPieces((parseInt(request.getParameter("actualPieces"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualUOM"))) {
            freight.setActualUOM(request.getParameter("actualUOM"));
        }
        tripService.updateFreight(freight);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/createFreight", method = POST)
    @ResponseBody
    public Object createFreight(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Freight freight = new Freight();
        freight.setTripId(parseInt(request.getParameter("tripId")));
        if (StringUtils.isEmpty(request.getParameter("id"))) {
            freight.setId(null);
        } else {
            freight.setId(parseInt(request.getParameter("id")));
        }
        freight.setItem(request.getParameter("item"));
        freight.setDescription(request.getParameter("description"));
        if (StringUtils.isNotEmpty(request.getParameter("estimatedCost"))) {
            freight.setEstimatedCost(new BigDecimal(parseDouble(request.getParameter("estimatedCost"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("estimatedWeight"))) {
            freight.setEstimatedWeight(new BigDecimal(parseDouble(request.getParameter("estimatedWeight"))));
        }
        freight.setEstimatedCurrency(request.getParameter("estimatedCurrency"));
        freight.setEstimatedDimension(request.getParameter("estimatedDimension"));
        freight.setEstimatedUOM(request.getParameter("estimatedUOM"));
        freight.setEstimatedCurrency(request.getParameter("estimatedCurrency"));
        freight.setBagtag(request.getParameter("bagtag"));
        if (StringUtils.isNotEmpty(request.getParameter("estimatedPieces"))) {
            freight.setEstimatedPieces(Integer.parseInt(request.getParameter("estimatedPieces")));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualDimension"))) {
            freight.setActualDimension(request.getParameter("actualDimension"));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualCurrency"))) {
            freight.setActualCurrency(request.getParameter("actualCurrency") + "");
        }
        if (StringUtils.isNotEmpty(request.getParameter("usdCost"))) {
            freight.setUsdCost(new BigDecimal(parseDouble(request.getParameter("usdCost"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("usdRate"))) {
            freight.setUsdRate(new BigDecimal(parseDouble(request.getParameter("usdRate"))));
        }

        if (StringUtils.isNotEmpty(request.getParameter("actualWeight"))) {
            freight.setActualWeight(new BigDecimal(parseDouble(request.getParameter("actualWeight"))));
        }

        if (StringUtils.isNotEmpty(request.getParameter("estimatedChargeWt"))) {
            freight.setEstimatedChargeWt(new BigDecimal(parseDouble(request.getParameter("estimatedChargeWt"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualChargeWt"))) {
            freight.setActualChargeWt(new BigDecimal(parseDouble(request.getParameter("actualChargeWt"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualCost"))) {
            freight.setActualCost(new BigDecimal(parseDouble(request.getParameter("actualCost"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualPieces"))) {
            freight.setActualPieces((parseInt(request.getParameter("actualPieces"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("actualUOM"))) {
            freight.setActualUOM(request.getParameter("actualUOM"));
        }
        tripService.createFreight(freight);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/removeFreight", method = GET)
    @ResponseBody
    public Object removeFreight(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = newHashMap();
        tripService.removeFreight(parseInt(request.getParameter("freightId")));
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/saveTrip", method = POST)
    @ResponseBody
    public Object saveTrip(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Trip trip = new Trip();

        Integer tripID = parseInt(request.getParameter("tripID"));
        String tripType = request.getParameter("tripType");
        String tripStatus = request.getParameter("tripStatus");
        String departmentId = request.getParameter("departmentId");
        if (StringUtils.isEmpty(tripStatus)) {
            throw new BusinessException("Trip Status can not be null");
        }
        trip.setId(tripID);
        trip.setType(tripType);
        trip.setStatus(tripStatus);
        Integer dept = null;
        // if (isNotEmpty(request.getParameter("dept"))) dept = parseInt(request.getParameter("dept"));
        if (isNotEmpty(request.getParameter("departmentId"))) dept = parseInt(request.getParameter("departmentId"));
        trip.setDepartmentId(dept);
        if (StringUtils.isNotEmpty(request.getParameter("totalPieces"))) {
            trip.setTotalPieces(Integer.parseInt((request.getParameter("totalPieces"))));
        }
        if (StringUtils.isNotEmpty(request.getParameter("totalWeight"))) {
            trip.setTotalWeight(new BigDecimal(parseDouble(request.getParameter("totalWeight"))));
        }
        trip.setStatus(request.getParameter("tripStatus"));
        trip.setType(request.getParameter("tripType"));
        if (StringUtils.isNotEmpty(request.getParameter("clientID"))) {
            trip.setClientId(parseInt(request.getParameter("clientID")));
        }
        if (StringUtils.isNotEmpty(request.getParameter("consigneeID"))) {
            trip.setConsigneeId(parseInt(request.getParameter("consigneeID")));
        }
        if (StringUtils.isNotEmpty(request.getParameter("consigneeID2"))) {
            trip.setConsigneeId2(parseInt(request.getParameter("consigneeID2")));
        }
        if (StringUtils.isNotEmpty(request.getParameter("consigneeID3"))) {
            trip.setConsigneeId3(parseInt(request.getParameter("consigneeID3")));
        }
        if (StringUtils.isNotEmpty(request.getParameter("shipperID"))) {
            trip.setShipperId(parseInt(request.getParameter("shipperID")));
        }
        if (StringUtils.isNotEmpty(request.getParameter("shipperID2"))) {
            trip.setShipperId2(parseInt(request.getParameter("shipperID2")));
        }
        if (StringUtils.isNotEmpty(request.getParameter("shipperID3"))) {
            trip.setShipperId3(parseInt(request.getParameter("shipperID3")));
        }
        if (StringUtils.isNotBlank(request.getParameter("brokerID"))) {
            trip.setBrokerId(parseInt(request.getParameter("brokerID")));
        }
        if (StringUtils.isNotBlank(request.getParameter("brokerID2"))) {
            trip.setBrokerId2(parseInt(request.getParameter("brokerID2")));
        }
        if (StringUtils.isNotBlank(request.getParameter("billedClientID"))) {
            trip.setBilledClientId(parseInt(request.getParameter("billedClientID")));
        }
        if (StringUtils.isNotBlank(request.getParameter("dropConsigneeName"))) {
            trip.setDropCosigneeName(trimToEmpty(request.getParameter("dropConsigneeName")));
        }
        if (StringUtils.isNotBlank(request.getParameter("dropShipperName"))) {
            trip.setDropShipperName(trimToEmpty(request.getParameter("dropShipperName")));
        }
        String tempDateStr = request.getParameter("criticalTime");
        if (StringUtils.isNotBlank(tempDateStr)) {
            trip.setCriticalTime(DateUtil.toDate(tempDateStr, DATE_TIME_FARMAT_YYYYMMDDHHMM));
        }
        tempDateStr = request.getParameter("pickupDate");
        if (StringUtils.isNotBlank(tempDateStr)) {
            trip.setPickupDate(DateUtil.toDate(tempDateStr, LONG_DATE_FORMAT_SHOW));
        }
        tempDateStr = request.getParameter("deliveryDate");
        if (StringUtils.isNotBlank(tempDateStr)) {
            trip.setDeliveryDate(DateUtil.toDate(tempDateStr, LONG_DATE_FORMAT_SHOW));
        }
        tempDateStr = request.getParameter("readyTime");
        if (StringUtils.isNotBlank(tempDateStr)) {
            trip.setReadyTime(DateUtil.toDate(tempDateStr, DATE_TIME_FARMAT_YYYYMMDDHHMM));
        }
        if (StringUtils.isNotBlank(request.getParameter("note"))) {
            trip.setNote(trimToEmpty(request.getParameter("note")));
        }

        trip.setAuthorizationNo(request.getParameter("authorizationNo"));
        trip.setAuthorizedBy(request.getParameter("authorizedBy"));
        tripService.saveTrip(trip);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveUSDRate", method = GET)
    @ResponseBody
    public Object retrieveUSDRate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String currencyCode = request.getParameter("currencyCode");
        BigDecimal usdRate = currencyService.retrieveUSDRate(currencyCode);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, usdRate);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveEventsByTripId", method = GET)
    @ResponseBody
    public Object retrieveEventsByTripId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripid = request.getParameter("tripid");
        List<TripEvent> events = tripService.retrieveEvents(parseInt(tripid));
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, events);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/getPDF", method = POST)
    @ResponseBody
    public Object getPDF(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = request.getParameter("url");
        String tripId = request.getParameter("tripId");
        String quoteNum = request.getParameter("quoteNum");
        String version = request.getParameter("version");
        String expireDate = request.getParameter("expireDate");
        String freightids = request.getParameter("freightids");
        String refId = request.getParameter("refId");
        String quoteCurrency = request.getParameter("quoteCurrency");

        if (isBlank(tripId)) {
            throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{tripId}), null);
        }
        if (isBlank(quoteNum)) {
            throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{quoteNum}), null);
        }
        if (isBlank(version)) {
            version = "1";
        }

        //generate URL
        StringBuilder sb = new StringBuilder();
        sb.append(url).append(request.getContextPath()).append("/run?__report=quote_report.rptdesign&__format=pdf")
                .append("&tripid=").append(tripId)
                .append("&quoteNo=").append(URLEncoder.encode(trimToEmpty(quoteNum), "utf-8"))
                .append("&versionNo=").append(URLEncoder.encode(trimToEmpty(version), "utf-8"))
                .append("&expireDate=").append(URLEncoder.encode(trimToEmpty(expireDate), "utf-8"))
                .append("&quoteCurrency=").append(URLEncoder.encode(trimToEmpty(quoteCurrency), "utf-8"));

        String encodedUrl = sb.toString();

        log.info("-> PREPARE TO GEN REPORT FROM " + encodedUrl);


        //save file
        long timebegin = System.currentTimeMillis();
        String divisionCode = tripService.getDivisionCodeByTripId(parseInt(tripId));
        String filename = (new StringBuilder()).append(divisionCode).append("-").append(refId).append("-").append(version).append(".pdf").toString();
        String filepath = docManager.getFilePathToSave(parseInt(tripId), filename);
        File file = new File(filepath);
        FileUtils.copyURLToFile(new URL(encodedUrl), file);
        log.info("-> Quote SAVE AT " + filepath);
        log.info("-> TIME TO GENERATE REPORT IS " + (System.currentTimeMillis() - timebegin) / 1000);

        //save record
        TripDocument doc = new TripDocument();
        doc.setOriginalFileName(filename);
        doc.setFilename(file.getName());
        doc.setFilesize((int) file.length() / 1024);
        doc.setTripId(parseInt(tripId));
        doc.setFileType("Quote");
        tripService.createDocument(doc, false);
        log.info("-> Quote RECORD IS SAVED IN EPDOCUMENT: " + doc.getId());

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        map.put(PARAM_RECORDS, encodedUrl);
        return map;
    }

    @RequestMapping(value = "/getQuoteBuildPDF", method = POST)
    @ResponseBody
    public Object getQuoteBuildPDF(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = request.getParameter("url");
        String tripId = request.getParameter("tripId");
        String quoteNum = request.getParameter("quoteNum");
        String version = request.getParameter("version");
        String expireDate = request.getParameter("expireDate");
        String freightids = request.getParameter("freightids");
        String refId = request.getParameter("refId");
        String quoteCurrency = request.getParameter("quoteCurrency");

        String readyTime = request.getParameter("readyTime");
        String pickupCity = request.getParameter("pickupCity");
        String destination = request.getParameter("destination");
        String cost = "$ " + request.getParameter("cost");
        String duties = request.getParameter("duties");
        String schedule = request.getParameter("schedule");
        String typeDesp = request.getParameter("typeDesp");
        String freights = request.getParameter("freights");
        List<Map<String, Object>> freightsList = JacksonMapper.getInstance().readValue(freights, List.class);
        for (Map<String, Object> freight : freightsList) {
            System.out.println(freight);
        }
        String termsList = request.getParameter("termsList");
        List<Map<String, Object>> terms = JacksonMapper.getInstance().readValue(termsList, List.class);

        if (isBlank(tripId)) {
            throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{tripId}), null);
        }
        if (isBlank(quoteNum)) {
            throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{quoteNum}), null);
        }
        if (isBlank(version)) {
            version = "1";
        }

        /*
        //generate URL
        StringBuilder sb = new StringBuilder();
        sb.append(url).append(request.getContextPath()).append("/run?__report=quote_report.rptdesign&__format=pdf")
                .append("&tripid=").append(tripId)
                .append("&quoteNo=").append(URLEncoder.encode(trimToEmpty(quoteNum), "utf-8"))
                .append("&versionNo=").append(URLEncoder.encode(trimToEmpty(version), "utf-8"))
                .append("&expireDate=").append(URLEncoder.encode(trimToEmpty(expireDate), "utf-8"))
                .append("&quoteCurrency=").append(URLEncoder.encode(trimToEmpty(quoteCurrency), "utf-8"));

        String encodedUrl = sb.toString();

        log.info("-> PREPARE TO GEN REPORT FROM " + encodedUrl);
        */

        //save file
        long timebegin = System.currentTimeMillis();
        String divisionCode = tripService.getDivisionCodeByTripId(parseInt(tripId));
        String filename = (new StringBuilder()).append(divisionCode).append("-").append(refId).append("-").append(version).append(".pdf").toString();
        String filepath = docManager.getFilePathToSave(parseInt(tripId), filename);
        File file = new File(filepath);

        // re render pdf
        Map<String, String> headers = initHeaders(quoteNum, version, expireDate, typeDesp);
        Map<String, Field> fields = initFields(readyTime, pickupCity, destination, cost, duties, schedule, terms);
        QuoteGenerator.generate(file, headers, fields, freightsList);

        log.info("-> Quote SAVE AT " + filepath);
        log.info("-> TIME TO GENERATE REPORT IS " + (System.currentTimeMillis() - timebegin) / 1000);

        //save record
        TripDocument doc = new TripDocument();
        doc.setOriginalFileName(filename);
        doc.setFilename(file.getName());
        doc.setFilesize((int) file.length() / 1024);
        doc.setTripId(parseInt(tripId));
        doc.setFileType("Quote");
        tripService.createDocument(doc, false);
        log.info("-> Quote RECORD IS SAVED IN EPDOCUMENT: " + doc.getId());

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        //map.put(PARAM_RECORDS, encodedUrl);
        return map;
    }

    public static void main(String[] args) {
        Object o = null;
        String s = String.valueOf(o);
    }

    @RequestMapping(value = "/getPickupPDF", method = POST)
    @ResponseBody
    public Object getPickupPDF(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripId = request.getParameter("tripId");
        String quoteNum = request.getParameter("quoteNum");
        String tripNumber = request.getParameter("tripNumber");
        String agentId = "0";
        if (!isBlank(request.getParameter("agentId"))) {
            agentId = request.getParameter("agentId");
        }
        String consigneeId = "0";
        if (!isBlank(request.getParameter("consigneeId"))) {
            consigneeId = request.getParameter("consigneeId");
        }
        String shipperId = "0";
        if (!isBlank(request.getParameter("shipperId"))) {
            shipperId = request.getParameter("shipperId");
        }
        String brokerId = "0";
        if (!isBlank(request.getParameter("brokerId"))) {
            brokerId = request.getParameter("brokerId");
        }
        String pickupDate = request.getParameter("pickupDate");
        String deliveryDate = request.getParameter("deliveryDate");
        String pickupAt = request.getParameter("pickupAt");
        String deliveryTo = request.getParameter("deliveryTo");
        String pieces = "";
        if (!isBlank(request.getParameter("pieces"))) {
            pieces = request.getParameter("pieces");
        }
        String chargedWeight = "";
        if (!isBlank(request.getParameter("chargedWeight"))) {
            chargedWeight = request.getParameter("chargedWeight");
        }
        String wt = "";
        if (!isBlank(request.getParameter("wt"))) {
            wt = request.getParameter("wt");
        }
        String dims = request.getParameter("dims");
        String pickupInstruction = "";
        if (!isBlank(request.getParameter("pickupInstruction"))) {
            pickupInstruction = request.getParameter("pickupInstruction");
        }

        String costids = request.getParameter("costids");
        String url = request.getParameter("url");

        if (isBlank(tripId)) {
            throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{tripId}), null);
        }
        if (isBlank(quoteNum)) {
            throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{quoteNum}), null);
        }

        //generate URL
        //TODO append protectedTime
        StringBuilder sb = new StringBuilder();
        sb.append(url).append(request.getContextPath()).append("/run?__report=pickup_report.rptdesign&__format=pdf&tripid=").append(tripId)
                .append("&tripNumber=").append(URLEncoder.encode(trimToEmpty(tripNumber), "utf-8"))
                .append("&agentId=").append(URLEncoder.encode(trimToEmpty(agentId), "utf-8"))
                .append("&consigneeId=").append(URLEncoder.encode(trimToEmpty(consigneeId), "utf-8"))
                .append("&shipperId=").append(URLEncoder.encode(trimToEmpty(shipperId), "utf-8"))
                .append("&brokerId=").append(URLEncoder.encode(trimToEmpty(brokerId), "utf-8"))
                .append("&pickupDate=").append(URLEncoder.encode(trimToEmpty(pickupDate), "utf-8"))
                .append("&deliveryDate=").append(URLEncoder.encode(trimToEmpty(deliveryDate), "utf-8"))
                .append("&pickAt=").append(URLEncoder.encode(trimToEmpty(pickupAt), "utf-8"))
                .append("&deliverTo=").append(URLEncoder.encode(trimToEmpty(deliveryTo), "utf-8"))
                .append("&pieces=").append(URLEncoder.encode(trimToEmpty(pieces), "utf-8"))
                .append("&chargedWeight=").append(URLEncoder.encode(trimToEmpty(chargedWeight), "utf-8"))
                .append("&wt=").append(URLEncoder.encode(trimToEmpty(wt), "utf-8"))
                .append("&dims=").append(URLEncoder.encode(trimToEmpty(dims), "utf-8"))
                .append("&pickupInstruction=").append(URLEncoder.encode(trimToEmpty(pickupInstruction), "utf-8"))
                .append("&costids=").append(costids);

        String encodedUrl = sb.toString();

        log.info("-> PREPARE TO GEN REPORT FROM " + encodedUrl);

        //save file
        long timebegin = System.currentTimeMillis();
        String filename = (new StringBuilder()).
                append(quoteNum).
                append("-Pickup-").
                append(tripService.getDocVersionByType(parseInt(tripId), "Pickup")).
                append(".pdf").toString();
        String filepath = docManager.getFilePathToSave(parseInt(tripId), filename);
        File file = new File(filepath);
        FileUtils.copyURLToFile(new URL(encodedUrl), file);
        log.info("-> PICKUP SAVE AT " + filepath);
        log.info("-> TIME TO GENERATE REPORT IS " + (System.currentTimeMillis() - timebegin) / 1000);

        //save record
        TripDocument doc = new TripDocument();
        doc.setOriginalFileName(filename);
        doc.setFilename(file.getName());
        doc.setFilesize((int) file.length() / 1024);
        doc.setTripId(parseInt(tripId));
        doc.setFileType("Pickup");
        tripService.createDocument(doc, false);
        log.info("-> PICKUP RECORD IS SAVED IN EPDOCUMENT: " + doc.getId());

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        map.put(PARAM_RECORDS, encodedUrl);
        return map;
    }

    @RequestMapping(value = "/getBolPDF", method = POST)
    @ResponseBody
    public Object getBolPDF(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = request.getParameter("url");
        String tripRefNo = request.getParameter("tripRefNo");
        String refId = request.getParameter("refId");
        String tripId = request.getParameter("tripId");
        String thirdPartId = request.getParameter("thirdPartId");
        String shipperId = request.getParameter("shipperId");
        String consigineeId = request.getParameter("consigineeId");
        String dims = request.getParameter("dims");
        String poNumber = request.getParameter("poNumber");
        String refNum = request.getParameter("refNum");
        String prepaid = request.getParameter("prepaid");
        String collect = request.getParameter("collect");
        String routingCarrier = request.getParameter("routingCarrier");
        String transferPoint = request.getParameter("transferPoint");
        String declaredValue = request.getParameter("declaredValue");
        String systemOfMeasure = request.getParameter("systemOfMeasure");
        String bolDate = request.getParameter("bolDate");
        String bolDay = "";
        String bolYear = "";
        String bolMonth = "";
        if (isNotEmpty(bolDate)) {
            String[] bolDateArray = bolDate.split("-");
            bolYear = bolDateArray[0];
            bolMonth = bolDateArray[1];
            bolDay = bolDateArray[2];
        }
        String fw = "";
        String fl = "";
        String fh = "";

        if (StringUtils.isNotBlank(dims) && !dims.equals("null")) {
            String[] dimsArray = dims.split("X");
            fw = dimsArray[0];
            if (dimsArray.length >= 2) {
                fl = dimsArray[1];
            }
            if (dimsArray.length >= 3) {
                fh = dimsArray[2].split(" ")[0];
            }
        }


        String totalCubicFeet = request.getParameter("totalCubicFeet");
        String totalWeight = request.getParameter("totalWeight");
        String totalNoOfPieces = request.getParameter("totalNoOfPieces");


        if (isBlank(tripId)) {
            throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{tripId}), null);
        }
        Integer w = 0;
        Integer l = 0;
        Integer h = 0;
        Integer num = 0;
        if (StringUtils.isNotEmpty(fw)) {
            w = Integer.parseInt(fw);
        }
        if (StringUtils.isNotEmpty(fl)) {
            l = Integer.parseInt(fl);
        }
        if (StringUtils.isNotEmpty(fh)) {
            h = Integer.parseInt(fh);
        }
        if (StringUtils.isNotEmpty(totalNoOfPieces)) {
            num = Integer.parseInt(totalNoOfPieces);
        }
        String chargableWeight = "0.00";
        DecimalFormat df = new DecimalFormat("0.0000");
        if (num != null && num != 0 && w != null && w != 0 && l != null && l != 0 && h != 0 && h != null) {
            if (StringUtils.isNotEmpty(systemOfMeasure) && Objects.equals(systemOfMeasure, "M")) {
                chargableWeight = df.format(w * h * l * num / 366.00) + "KG";
            }
            if (StringUtils.isNotEmpty(systemOfMeasure) && Objects.equals(systemOfMeasure, "I")) {
                chargableWeight = df.format(w * h * l * num / 166.00) + "LBS";
            }
        }

        // chargableWeight = 1.0d;
        //generate URL
        StringBuilder sb = new StringBuilder();
        sb.append(url).append(request.getContextPath()).append("/run?__report=bol_report.rptdesign&__format=pdf&tripid=")
                .append(tripId)
                .append("&thirdpartid=").append(URLEncoder.encode(trimToEmpty(thirdPartId), "utf-8"))
                .append("&shipperid=").append(URLEncoder.encode(trimToEmpty(shipperId), "utf-8"))
                .append("&consigneeid=").append(URLEncoder.encode(trimToEmpty(consigineeId), "utf-8"))
                .append("&fw=").append(URLEncoder.encode(trimToEmpty(fw), "utf-8"))
                .append("&fl=").append(URLEncoder.encode(trimToEmpty(fl), "utf-8"))
                .append("&fh=").append(URLEncoder.encode(trimToEmpty(fh), "utf-8"))
                .append("&totalcubicfeet=").append(URLEncoder.encode(trimToEmpty(totalCubicFeet), "utf-8"))
                .append("&chargableweight=").append(chargableWeight)
                .append("&totalweight=").append(URLEncoder.encode(trimToEmpty(totalWeight), "utf-8"))
                .append("&totalnoofpieces=").append(URLEncoder.encode(trimToEmpty(totalNoOfPieces), "utf-8"))
                .append("&ponumber=").append(URLEncoder.encode(trimToEmpty(poNumber), "utf-8"))
                .append("&refnum=").append(URLEncoder.encode(trimToEmpty(refNum), "utf-8"))
                .append("&prepaid=").append(URLEncoder.encode(trimToEmpty(prepaid), "utf-8"))
                .append("&collect=").append(URLEncoder.encode(trimToEmpty(collect), "utf-8"))
                .append("&routingcarrier=").append(URLEncoder.encode(trimToEmpty(routingCarrier), "utf-8"))
                .append("&transferpoint=").append(URLEncoder.encode(trimToEmpty(transferPoint), "utf-8"))
                .append("&declaredvalue=").append(URLEncoder.encode(trimToEmpty(declaredValue), "utf-8"))
                .append("&bolyear=").append(URLEncoder.encode(trimToEmpty(bolYear), "utf-8"))
                .append("&bolmonth=").append(URLEncoder.encode(trimToEmpty(bolMonth), "utf-8"))
                .append("&bolday=").append(URLEncoder.encode(trimToEmpty(bolDay), "utf-8"))
                .append("&triprefno=").append(URLEncoder.encode(trimToEmpty(tripRefNo), "utf-8"));

        String encodedUrl = sb.toString();

        log.info("-> PREPARE TO GEN REPORT FROM " + encodedUrl);

        String divisionCode = tripService.getDivisionCodeByTripId(parseInt(tripId));
        //save file
        long timebegin = System.currentTimeMillis();
        String filename = (new StringBuilder()).
                append(divisionCode).
                append("-").append(refId).append("-BOL-").
                append(tripService.getDocVersionByType(parseInt(tripId), "BOL")).
                append(".pdf").toString();
        String filepath = docManager.getFilePathToSave(parseInt(tripId), filename);
        File file = new File(filepath);
        FileUtils.copyURLToFile(new URL(encodedUrl), file);
        log.info("-> BOL SAVE AT " + filepath);
        log.info("-> TIME TO GENERATE REPORT IS " + (System.currentTimeMillis() - timebegin) / 1000);

        //save record
        TripDocument doc = new TripDocument();
        doc.setOriginalFileName(filename);
        doc.setFilename(file.getName());
        doc.setFilesize((int) file.length() / 1024);
        doc.setTripId(parseInt(tripId));
        doc.setFileType("BOL");
        tripService.createDocument(doc, false);
        log.info("-> BOL RECORD IS SAVED IN EPDOCUMENT: " + doc.getId());

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        map.put(PARAM_RECORDS, encodedUrl);
        return map;
    }

    @RequestMapping(value = "/savePickup", method = POST)
    @ResponseBody
    public Object savePickup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Trip trip = new Trip();

        String tripID = request.getParameter("tripID");
        if (isBlank(tripID)) {
            throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{tripID}), null);
        }
        trip.setId(parseInt(tripID));


        String agentId = request.getParameter("agentId");
        if (isNotBlank(agentId)) {
            trip.setAgentId(parseInt(agentId));
        }
        String consigneeId = request.getParameter("consigneeId");
        if (isNotBlank(consigneeId)) {
            trip.setConsigneeId(parseInt(consigneeId));
        }
        String shipperId = request.getParameter("shipperId");
        if (isNotBlank(shipperId)) {
            trip.setShipperId(parseInt(shipperId));
        }
        String brokerId = request.getParameter("brokerId");
        if (isNotBlank(brokerId)) {
            trip.setBrokerId(parseInt(brokerId));
        }
        String pickupDate = request.getParameter("pickupDate");
        if (isNotBlank(pickupDate)) {
            trip.setPickupDate(toDate(pickupDate, LONG_DATE_FORMAT_SHOW));
        }
        String deliveryDate = request.getParameter("deliveryDate");
        if (isNotBlank(deliveryDate)) {
            trip.setDeliveryDate(toDate(deliveryDate, LONG_DATE_FORMAT_SHOW));
        }
        String chargedWeight = request.getParameter("chargedWeight");
        if (isNotBlank(chargedWeight)) {
            trip.setChargedWeight(new BigDecimal(parseDouble(chargedWeight)));
        }
        String pickupInstruction = request.getParameter("pickupInstruction");
        trip.setPickupInstruction(pickupInstruction);
        //#105 - pu/del builder •	User’s modification on ANY of the fields on the Pickup / Delivery Order build will NOT update the database.
        //tripService.savePickUp(trip);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/loadQuotePdfData", method = POST)
    @ResponseBody
    public Object loadQuotePdfData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripId = request.getParameter("tripid");


        Trip trip = tripService.retrieveTripById("", parseInt(tripId), true);

        // selected consignee
        CustomerLocation consignee = new CustomerLocation();
        if (null != trip && null != trip.getConsigneeId()) {
            consignee = customerService.selectCustomerLocationByIdForTrip(trip.getConsigneeId());
        }
        // selected shipper
        CustomerLocation shipper = new CustomerLocation();
        if (null != trip && null != trip.getShipperId()) {
            shipper = customerService.selectCustomerLocationByIdForTrip(trip.getShipperId());
        }

        List<Condition> findAll = tripService.retrieveQuoteTreeByTripID(parseInt(tripId));

        Map<String, Object> map = newHashMap();
        map.put(PARAM_SELECTED_SHIPPER, shipper);
        map.put(PARAM_SELECTED_CONSIGNEE, consignee);
        map.put(PARAM_RECORDS, trip);
        map.put("TERM_CONDITIONS", findAll);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/saveQuotePdfData", method = POST)
    @ResponseBody
    public Object saveQuotePdfData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Trip trip = new Trip();
        String tripID = request.getParameter("tripID");
        if (isBlank(tripID)) {
            throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{tripID}), null);
        }
        trip.setId(parseInt(tripID));
        String readyTime = request.getParameter("readyTime");
        if (isNotBlank(readyTime)) {
            trip.setReadyTime(toDate(readyTime, DATE_TIME_FARMAT_YYYYMMDDHHMM));
        }
        String expireDate = request.getParameter("expireDate");
        if (isNotBlank(expireDate)) {
            trip.setExpireDate(toDate(expireDate, DATE_TIME_FARMAT_YYYYMMDDHHMM));
        }
        String pickupCity = request.getParameter("pickupCity");
        trip.setPickupCity(pickupCity);
        String destinationCity = request.getParameter("destinationCity");
        trip.setDestinationCity(destinationCity);
        String quoteTotal = request.getParameter("quoteTotal");
        if (StringUtils.isNotEmpty(quoteTotal)) {
            trip.setQuoteTotal(new BigDecimal(parseDouble(quoteTotal)));
        }
        String dutiesTax = request.getParameter("dutiesTax");
        trip.setDutiesTax(dutiesTax);
        String quoteCurrency = request.getParameter("quoteCurrency");
        trip.setQuoteCurrency(quoteCurrency);
        String flightSchedule = request.getParameter("flightSchedule");
        trip.setFlightSchedule(flightSchedule);

        String termsList = request.getParameter("termsList");
        List<Map<String, Object>> maps = JacksonMapper.getInstance().readValue(termsList, List.class);
        if (null != maps && 0 != maps.size()) {
            for (Map<String, Object> map : maps) {

                Integer id = (Integer) map.get("id");
                String description = (String) map.get("desc");

                Condition e = new Condition();
                e.setId(id);
                e.setDescription(description);
                tripService.updateConditionForQuotePdf(e);
            }
        }
        tripService.saveQuotePdfData(trip);

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/saveBol", method = POST)
    @ResponseBody
    public Object saveBol(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Trip trip = new Trip();
        String tripID = request.getParameter("tripID");
        String poNum = request.getParameter("authorizationNo");
        if (isBlank(tripID)) {
            throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{tripID}), null);
        }
        trip.setId(parseInt(tripID));
        trip.setAuthorizationNo(poNum);
        tripService.saveBOL(trip);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/removeInvoice", method = GET)
    @ResponseBody
    public Object removeInvoice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String invoiceId = request.getParameter("id");
        if (isEmpty(invoiceId)) throw new BusinessException("invoice id is empty");
        tripService.removeInvoice(parseInt(invoiceId));
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/removeFile", method = GET)
    @ResponseBody
    public Object removeFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileId = request.getParameter("id");
        if (isEmpty(fileId)) throw new BusinessException("file id is empty");
        tripService.removeTripDocument(parseInt(fileId));
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/saveInvoice", method = POST)
    @ResponseBody
    public Object saveInvoice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Invoice invoice = new Invoice();
        String mode = request.getParameter("mode");
        invoice.setTripId(parseInt(trimToEmpty(request.getParameter("tripid"))));
        if ("edit".equals(mode)) {
            invoice.setId(parseInt(trimToEmpty(request.getParameter("invoiceid"))));
        }

        invoice.setBilledClientId(parseInt(trimToEmpty(request.getParameter("billedClientID"))));
        invoice.setShipperId(parseInt(trimToEmpty(request.getParameter("shipperID"))));
        invoice.setRefNum(trimToEmpty(request.getParameter("refNum")));
        invoice.setServiceType(trimToEmpty(request.getParameter("serviceType")));
        invoice.setBillingCurrency(trimToEmpty(request.getParameter("billingCurrency")));
        invoice.setInvoiceDate(toDate(trimToEmpty(request.getParameter("invoiceDate")), LONG_DATE_FORMAT_SHOW));
        invoice.setInvoiceFrom(trimToEmpty(request.getParameter("invoiceFrom")));
        invoice.setInvoiceTo(trimToEmpty(request.getParameter("invoiceTo")));
        invoice.setPickupDate(toDate(trimToEmpty(request.getParameter("pickupDate")), LONG_DATE_FORMAT_SHOW));
        invoice.setDeliveryDate(toDate(trimToEmpty(request.getParameter("deliveryDate")), LONG_DATE_FORMAT_SHOW));
        invoice.setAuthBy(trimToEmpty(request.getParameter("authBy")));
        invoice.setAuthNo(trimToEmpty(request.getParameter("authNo")));
        String miles = request.getParameter("miles");
        if (isNotEmpty(miles)) {
            invoice.setMiles(keep(trimToEmpty(miles), 2));
        }
        String rate = request.getParameter("rate");
        if (isNotEmpty(rate)) {
            invoice.setRate(keep(trimToEmpty(rate), 2));
        }
        invoice.setTotalPieces(parseInt(trimToEmpty(request.getParameter("totalPieces"))));
        invoice.setActualWeight(keep(trimToEmpty(request.getParameter("actualWeight")), 2));
        invoice.setChargeableWeight(keep(trimToEmpty(request.getParameter("chargeableWeight")), 2));
        invoice.setDetails(buildInvoiceDetails(trimToEmpty(request.getParameter("details"))));
        invoice.setInvoiceSubtotal(keep(trimToEmpty(request.getParameter("subtotal")), 2));
        invoice.setTotalAmount(keep(trimToEmpty(request.getParameter("totalAmount")), 2));
        tripService.saveInvoice(invoice);


        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, invoice.getId());
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;

    }

    private List<InvoiceDetail> buildInvoiceDetails(String items) throws Exception {
        if (isEmpty(items)) return newArrayList();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> details = objectMapper.readValue(items, List.class);

        if (!CollectionUtils.isEmpty(details)) {
            List<InvoiceDetail> invoiceDetails = newArrayList();
            for (Map<String, Object> item : details) {
                InvoiceDetail invoiceDetail = new InvoiceDetail();
                if (null != item.get("amount")) {
                    invoiceDetail.setAmount(keep(trimToEmpty(String.valueOf(item.get("amount"))), 2));
                }
                if (null != item.get("sequence")) {
                    invoiceDetail.setSequence(parseInt(String.valueOf(item.get("sequence"))));
                }
                if (null != item.get("item")) {
                    invoiceDetail.setItem(String.valueOf(item.get("item")));
                }
                if (null != item.get("chargeCode")) {
                    invoiceDetail.setChargeCode(String.valueOf(item.get("chargeCode")));
                }
                if (null != item.get("taxCodeValue")) {
                    invoiceDetail.setTaxCodeValue(keep(String.valueOf(item.get("taxCodeValue")), 4));
                }
                if (null != item.get("taxCode")) {
                    invoiceDetail.setTaxCode(String.valueOf(item.get("taxCode")));
                }

                invoiceDetails.add(invoiceDetail);
            }
            return invoiceDetails;
        }
        return newArrayList();
    }


    @RequestMapping(value = "/getInvoicePDF", method = POST)
    @ResponseBody
    public Object getInvoicePDF(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String invoiceId = trimToEmpty(request.getParameter("id"));
        String pdfURL = trimToEmpty(request.getParameter("pdfURL"));
        String pickupAt = trimToEmpty(request.getParameter("pickupAt"));
        Invoice invoice = tripService.retrieveInvoice(parseInt(invoiceId));

        if (equal(null, invoice))
            throw new BusinessException("invoice record is empty!");
        if (equal(null, invoice.getTripId()))
            throw new BusinessException("trip id should not be empty");
        if (StringUtils.isEmpty(pdfURL))
            throw new BusinessException("PDF URL Should be define!");

        List<InvoiceDetail> details = invoice.getDetails();
        Map<String, BigDecimal> taxs = newHashMap();
        String hst = EMPTY;
        boolean findTag = false;
        for (InvoiceDetail item : details) {
            if (equal("HST", item.getTaxCode()) && !findTag) {
                hst = Joiner.on(" ").skipNulls().join(item.getTaxCode(), percentFormat(item.getTaxCodeValue().doubleValue(), 0));
                findTag = true;
            }
            fillTax(taxs, item);
        }
        String hstValue = EMPTY;
        if (!equal(null, taxs.get(hst))) {
            hstValue = currencyFormat(parseDouble(String.valueOf(taxs.get(hst))), CURRENCY_PATTERN_DEFAULT);
        }

        //generate URL
        StringBuilder sb = new StringBuilder();
        sb.append(pdfURL).append(request.getContextPath()).append("/run?__report=invoice_report.rptdesign&__format=pdf&tripid=")
                .append(invoice.getTripId())
                .append("&invoiceid=").append(invoice.getId())
                .append("&invoicenumber=").append(URLEncoder.encode(trimToEmpty(invoice.getRefNum()), "utf-8"))
                .append("&totalpieces=").append(trimToEmpty(String.valueOf(invoice.getTotalPieces())))
                .append("&actualweight=").append(URLEncoder.encode(trimToEmpty(invoice.getActualWeightWithUOM()), "utf-8"))
                .append("&hst=").append(URLEncoder.encode(trimToEmpty(hstValue), "utf-8"))
                .append("&chargeableweight=").append(URLEncoder.encode(trimToEmpty(invoice.getChargeableWeightWithUOM()), "utf-8"))
                .append("&pickupat=").append(URLEncoder.encode(trimToEmpty(pickupAt), "utf-8"));

        String encodedUrl = sb.toString();

        log.info("-> PREPARE TO GEN REPORT FROM " + encodedUrl);


        //save file
        long timebegin = System.currentTimeMillis();
        //String filename = (new StringBuilder()).append(invoice.getRefNum()).append("-").append(invoice.getVersion()).append(".pdf").toString();
        String filename = invoice.getRefNum() + ".pdf";
        String filepath = docManager.getFilePathToSave(invoice.getTripId(), filename);
        File file = new File(filepath);
        FileUtils.copyURLToFile(new URL(encodedUrl), file);
        log.info("-> Invoice SAVE AT " + filepath);
        log.info("-> TIME TO GENERATE REPORT IS " + (System.currentTimeMillis() - timebegin) / 1000);

        //save record
        TripDocument doc = new TripDocument();
        doc.setOriginalFileName(filename);
        doc.setFilename(file.getName());
        doc.setFilesize((int) file.length() / 1024);
        doc.setTripId(invoice.getTripId());
        doc.setFileType("Invoice");
        doc.setRefName(invoiceId);
        tripService.createDocument(doc, false);
        log.info("-> Invoice RECORD IS SAVED IN EPDOCUMENT: " + doc.getId());

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        map.put(PARAM_RECORDS, encodedUrl);
        return map;
    }

    @RequestMapping(value = "/printTripPDF", method = POST)
    @ResponseBody
    public Object printTripPDF(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer tripid = parseInt(trimToEmpty(request.getParameter("id")));
        String pdfURL = trimToEmpty(request.getParameter("pdfURL"));

        //generate URL
        StringBuilder sb = new StringBuilder();
        sb.append(pdfURL).append(request.getContextPath()).append("/run?__report=ticket_report.rptdesign&__format=pdf&tripid=")
                .append(tripid);

        String encodedUrl = sb.toString();

        log.info("-> PREPARE TO GEN REPORT FROM " + encodedUrl);

        Trip detail = tripService.retrieveTripById("", tripid, false);

        //save file
        long timebegin = System.currentTimeMillis();
        String filename = (new StringBuilder()).
                append(detail.getTripRefNo()).append("-").
                append(tripService.getDocVersionByType(tripid, "Trip")).
                append(".pdf").toString();
        String filepath = docManager.getFilePathToSave(tripid, filename);
        File file = new File(filepath);
        FileUtils.copyURLToFile(new URL(encodedUrl), file);
        log.info("-> Trip SAVE AT " + filepath);
        log.info("-> TIME TO GENERATE REPORT IS " + (System.currentTimeMillis() - timebegin) / 1000);

        //save record
        TripDocument doc = new TripDocument();
        doc.setOriginalFileName(filename);
        doc.setFilename(file.getName());
        doc.setFilesize((int) file.length() / 1024);
        doc.setTripId(tripid);
        doc.setFileType("Trip");
        tripService.createDocument(doc, false);
        log.info("-> Invoice RECORD IS SAVED IN EPDOCUMENT: " + doc.getId());

        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        map.put(PARAM_RECORDS, encodedUrl);
        return map;
    }

    @RequestMapping(value = "/calculateSummary", method = GET)
    @ResponseBody
    public Object calculateSummary(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String currencyCode = request.getParameter("currencyCode");
        if (isEmpty(currencyCode)) throw new BusinessException("Currency is empty, please select one!");
        BigDecimal usdRate = currencyService.retrieveUSDRate(currencyCode);
        if (equal(null, usdRate))
            usdRate = BigDecimal.ZERO;

        List<InvoiceDetail> details = buildInvoiceDetails(trimToEmpty(request.getParameter("details")));

        Map<String, BigDecimal> taxs = newHashMap();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (InvoiceDetail item : details) {
            if (item.getAmount() != null && item.getTaxAmount() != null) {
                totalAmount = totalAmount.add(item.getAmount()).add(item.getTaxAmount());
                subtotal = subtotal.add(item.getAmount());
                fillTax(taxs, item);
            }
        }


        subtotal = subtotal.setScale(2, BigDecimal.ROUND_HALF_UP);
        totalAmount = totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalInUSD = totalAmount.multiply(usdRate).setScale(2, BigDecimal.ROUND_HALF_UP);
        Map<String, Object> map = newHashMap();
        map.put("usdRate", usdRate);
        map.put("totalInUSD", totalInUSD);
        map.put("subtotal", subtotal);
        map.put("totalAmount", totalAmount);
        map.put("taxs", taxs);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    private void fillTax(Map<String, BigDecimal> taxs, InvoiceDetail item) {
        if (equal(null, taxs) || equal(item, null) || equal("NOTAX", item.getTaxCode()) || isEmpty(item.getTaxCode()) || equal(null, item.getTaxCodeValue())) {
            return;
        }
        // eg HST 14%
        String key = Joiner.on(" ").skipNulls().join(item.getTaxCode(), percentFormat(item.getTaxCodeValue().doubleValue(), 2));
        BigDecimal tax = taxs.get(key);
        if (equal(null, tax)) {
            if (null != item.getTaxAmount()) {
                taxs.put(key, item.getTaxAmount());
                return;
            }
            return;
        }
        taxs.put(key, tax.add(item.getTaxAmount()));
    }

    @RequestMapping(value = "/removeTripDocument", method = POST)
    @ResponseBody
    public Object removeTripDocument(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("docId");
        String docType = request.getParameter("docType");
        if (isEmpty(id))
            throw new BusinessException("File id is Empty!");

        boolean isAdminOrFinance = ApplicationSession.get().isAdmin();
        if (!isAdminOrFinance) {
            List<Role> roles = ApplicationSession.get().getRoles();
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(roles)) {
                for (Role role : roles) {
                    if (equal("Finance", role.getName())) {
                        isAdminOrFinance = true;
                        break;
                    }
                }
            }
        }

        if (equal(docType, "Invoice") && !isAdminOrFinance)
            throw new BusinessException("Only Admin and Finance roles can delete documents of Invoices!");

        tripService.removeTripDocument(parseInt(id));
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/saveTripTemplateName", method = POST)
    @ResponseBody
    public Object saveTripTemplateName(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripid = request.getParameter("tripid");
        String templateName = request.getParameter("templateName");
        //TODO modify page to get this parameter
        String eventTemplate = request.getParameter("eventTemplate");
        Integer eventTemplateId = null;
        if (!StringUtils.isEmpty(eventTemplate) && StringUtils.isNumeric(eventTemplate))
            eventTemplateId = Integer.parseInt(eventTemplate);

        if (isEmpty(tripid))
            throw new BusinessException("trip id is empty!");
        if (tripService.isExistTripTemplate(templateName))
            throw new BusinessException("Template name is duplicated");

        tripService.saveTripTemplateName(Integer.parseInt(tripid), templateName, eventTemplateId);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveTripEventTemplates", method = POST)
    @ResponseBody
    public Object retrieveTripEventTemplates(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripType = request.getParameter("tripType");
        List<TripEventTemplate> list = systemMaintenanceService.retrieveTripEventTemplatesForTripTemplate(tripType);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, list);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/retrieveTripTemplates", method = POST)
    @ResponseBody
    public Object retrieveTripTemplates(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String departmentId = request.getParameter("departmentId");
        List<Trip> tripTemplates = tripService.retrieveTripTemplates(parseInt(departmentId));
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, tripTemplates);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/updateTripDivision", method = POST)
    @ResponseBody
    public Object updateTripDivision(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripId = request.getParameter("tripId");
        String departmentId = request.getParameter("departmentId");
        if (isEmpty(tripId))
            throw new BusinessException("Trip Id is empty!");
        if (isEmpty(departmentId))
            throw new BusinessException("Division Id is empty!");
        tripService.updateTripDivision(parseInt(tripId), parseInt(departmentId));
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/updateTripMeasureSystem", method = POST)
    @ResponseBody
    public Object updateTripMeasureSystem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripId = request.getParameter("tripId");
        String measureCode = request.getParameter("measureCode");
        if (isEmpty(tripId))
            throw new BusinessException("trip id is empty!");
        tripService.updateTripMeasureSystem(parseInt(tripId), measureCode);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/getTripEventSubject", method = GET)
    @ResponseBody
    public Object getTripEventSubject(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripId = request.getParameter("tripId");
        if (isEmpty(tripId))
            throw new BusinessException("Trip Id is empty!");
        String eventSubject = tripService.getTripEventSubject(parseInt(tripId));
        Map<String, Object> map = newHashMap();
        map.put("eventSubject", eventSubject);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/sendEventNotification", method = POST)
    @ResponseBody
    public Object sendEventNotification(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tripId = request.getParameter("tripId");
        String eventSubject = request.getParameter("eventSubject");
        if (isEmpty(tripId))
            throw new BusinessException("Trip Id is empty!");
        if (isEmpty(eventSubject))
            throw new BusinessException("Event Subject is empty!");
        tripService.sendEventNotification(parseInt(tripId), eventSubject);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }
}
