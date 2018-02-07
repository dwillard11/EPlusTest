package ca.manitoulin.mtd.web.controller.customer;

import ca.manitoulin.mtd.dto.customer.Customer;
import ca.manitoulin.mtd.dto.customer.CustomerLocation;
import ca.manitoulin.mtd.dto.customer.CustomerContact;
import ca.manitoulin.mtd.dto.customer.CourierVisa;
import ca.manitoulin.mtd.dto.customer.Courier;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.customer.ICustomerService;
import ca.manitoulin.mtd.web.controller.maintenance.BusinessCodeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import ca.manitoulin.mtd.util.json.JacksonMapper;

import static ca.manitoulin.mtd.constant.ContextConstants.*;
import static ca.manitoulin.mtd.util.DateUtil.LONG_DATE_FORMAT_SHOW;
import static ca.manitoulin.mtd.util.DateUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.arrayToList;

@Controller
public class CustomerManagementController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private BusinessCodeController businessCodeController;

    @RequestMapping(value = "/customer/customerManager/retrieveCustomerByType", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveCustomerByType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String customerType = request.getParameter("advanceSearch");
        String keyword = request.getParameter("keyword");

        List<Customer> list = customerService.retrieveCustomerByType(keyword, customerType);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, list);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/updateCustomer", method = RequestMethod.GET)
    @ResponseBody
    public Object updateCustomer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Customer customer = new Customer();
        String[] requiredParams = new String[]{"id","name", "type", "status"};
        for (String param : requiredParams) {
            if (isBlank(request.getParameter(param))) {
                throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{param}), null);
            }
        }
        customer.setId(parseInt(request.getParameter("id")));
        customer.setName(request.getParameter("name"));
        customer.setTypeInString(request.getParameter("type"));
        customer.setQuickName(request.getParameter("quickName"));
        customer.setStatus(request.getParameter("status"));

        customerService.updateCustomer(customer);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/createCustomer", method = RequestMethod.GET)
    @ResponseBody
    public Object createCustomer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Customer customer = new Customer();
        String[] requiredParams = new String[]{"name", "type", "status"};
        for (String param : requiredParams) {
            if (isBlank(request.getParameter(param))) {
                throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{param}), null);
            }
        }
        customer.setName(request.getParameter("name"));
        customer.setTypeInString(request.getParameter("type"));
        customer.setQuickName(request.getParameter("quickName"));
        customer.setStatus(request.getParameter("status"));

        Customer newCustomer = customerService.createCustomer(customer);
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, newCustomer);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/deleteCustomer", method = RequestMethod.GET)
    @ResponseBody
    public Object deleteCustomer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String customerIds = request.getParameter("customerIds");
        List<String> customerIdList = asList(customerIds.split(","));
        for (String customerId : customerIdList) {
            try {
                customerService.deleteCustomer(parseInt(customerId));
            } catch (Exception e) {
                continue;
            }
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/retrieveCustomerProfileById", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveCustomerProfileById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String customerId = request.getParameter("id");
        Map<String, Object> map = newHashMap();
        if (isBlank(customerId)) {
            // new customer;
            map.put(PARAM_RECORDS, new Customer());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        try {
            Customer customer = customerService.retrieveCustomerProfileById(parseInt(customerId));
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            map.put(PARAM_RECORDS, customer);
        } catch (Exception e) {
            map.put(KEY_RESULT, RESPONSE_ERROR);
        }
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/getCountry", method = RequestMethod.GET)
    @ResponseBody
    public Object getCountry(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return businessCodeController.getCountry(request, response);
    }

    @RequestMapping(value = "/customer/customerManager/getProvinceByCountry", method = RequestMethod.GET)
    @ResponseBody
    public Object getProvinceByCountry(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return businessCodeController.getProvinceByCountry(request, response);
    }

    @RequestMapping(value = "/customer/customerManager/loadCustomerTreeData", method = RequestMethod.GET)
    @ResponseBody
    public Object loadCustomerTreeData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String customerId = request.getParameter("id");
        String keyword = request.getParameter("keyword");
        if (isBlank(keyword))
            keyword = "%";
        else
            keyword = "%" + keyword + "%";
        String showOnline = request.getParameter("showOnline");
        Map<String, Object> map = newHashMap();
        List<Customer> list = customerService.loadCustomerTreeData(parseInt(customerId), keyword, Boolean.parseBoolean(showOnline));
        map.put(PARAM_RECORDS, list);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/selectCustomerLocationById", method = RequestMethod.GET)
    @ResponseBody
    public Object selectCustomerLocationById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String locationId = request.getParameter("locationId");
        Map<String, Object> map = newHashMap();
        if (isBlank(locationId)) {
            // new CustomerLocation;
            map.put(PARAM_RECORDS, new CustomerLocation());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        try {
            CustomerLocation location = customerService.selectCustomerLocationById(parseInt(locationId)-1000000);
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            map.put(PARAM_RECORDS, location);

            CustomerLocation customer;
            if(location.getBillingCompany() != null)
            {
                customer = customerService.selectCustomerLocationById(location.getBillingCompany());
                map.put("billingCompany", customer);
            }
            if(location.getBrokerUs() != null)
            {
                customer = customerService.selectCustomerLocationById(location.getBrokerUs());
                map.put("brokerUs", customer);
            }
            if(location.getBrokerCa() != null)
            {
                customer = customerService.selectCustomerLocationById(location.getBrokerCa());
                map.put("brokerCa", customer);
            }
            if(location.getAltBrokerUs() != null)
            {
                customer = customerService.selectCustomerLocationById(location.getAltBrokerUs());
                map.put("altBrokerUs", customer);
            }
            if(location.getAltBrokerCa() != null)
            {
                customer = customerService.selectCustomerLocationById(location.getAltBrokerCa());
                map.put("altBrokerCa", customer);
            }
            if(location.getBrokerNat() != null)
            {
                customer = customerService.selectCustomerLocationById(location.getBrokerNat());
                map.put("brokerNat", customer);
            }
        } catch (Exception e) {
            map.put(KEY_RESULT, RESPONSE_ERROR);
        }
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/retrieveCustomerLocationByEntityId", method = RequestMethod.GET)
    @ResponseBody
    public Object retrieveCustomerLocationByEntityId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String entityId = request.getParameter("entityId");
        if (isBlank(entityId))
            entityId = "0";
        List<CustomerLocation> list = customerService.selectCustomerLocationByEntityId(parseInt(entityId));
        Map<String, Object> map = newHashMap();
        map.put(PARAM_RECORDS, list);
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/updateCustomerLocation", method = RequestMethod.GET)
    @ResponseBody
    public Object updateCustomerLocation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerLocation location = new CustomerLocation();
        String[] requiredParams = new String[]{"id","code", "status"};
        for (String param : requiredParams) {
            if (isBlank(request.getParameter(param))) {
                throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{param}), null);
            }
        }
        location.setId(parseInt(request.getParameter("id")));
        location.setCustomerId(parseInt(request.getParameter("customerId")));
        location.setCode(request.getParameter("code"));
        location.setQuickName(request.getParameter("quickName"));
        location.setStatus(request.getParameter("status"));
        location.setAddress1(request.getParameter("address1"));
        location.setAddress2(request.getParameter("address2"));
        location.setCity(request.getParameter("city"));
        if (!isBlank(request.getParameter("stateProvince")))
            location.setStateProvince(request.getParameter("stateProvince"));
        location.setPostalCode(request.getParameter("postalCode"));
        location.setCountry(request.getParameter("country"));
        location.setBusPhone1(request.getParameter("busPhone1"));
        location.setBusPhone2(request.getParameter("busPhone2"));
        location.setAfterHoursPhone(request.getParameter("afterHoursPhone"));
        location.setFax(request.getParameter("fax"));
        location.setEmail(request.getParameter("email"));
        location.setOpenHours(request.getParameter("openHours"));
        String tempStr ;
        tempStr = request.getParameter("locationUsage");
        if (!isBlank(tempStr))
            location.setLocationUsage(parseInt(tempStr));
        location.setMapUrl(request.getParameter("mapUrl"));

        tempStr = request.getParameter("billingCompany");
        if (!isBlank(tempStr))
            location.setBillingCompany(parseInt(tempStr));

        tempStr = request.getParameter("brokerUs");
        if (!isBlank(tempStr))
            location.setBrokerUs(parseInt(tempStr));
        tempStr = request.getParameter("brokerCa");
        if (!isBlank(tempStr))
            location.setBrokerCa(parseInt(tempStr));
        tempStr = request.getParameter("altBrokerUs");
        if (!isBlank(tempStr))
            location.setAltBrokerUs(parseInt(tempStr));
        tempStr = request.getParameter("altBrokerCa");
        if (!isBlank(tempStr))
            location.setAltBrokerCa(parseInt(tempStr));
        tempStr = request.getParameter("brokerNat");
        if (!isBlank(tempStr))
            location.setBrokerNat(parseInt(tempStr));
        tempStr = request.getParameter("agentId");
        if (!isBlank(tempStr))
            location.setAgentId(parseInt(tempStr));
        location.setNotes(request.getParameter("notes"));
        location.setKnown(request.getParameter("known"));
        tempStr = request.getParameter("knownStatusIndex");
        if (!isBlank(tempStr))
            location.setKnownStatusIndex(parseInt(tempStr));
        location.setTsa(request.getParameter("tsa"));
        location.setTsaAirport(request.getParameter("tsaAirport"));
        location.setTsaContact(request.getParameter("tsaContact"));
        location.setCsa(request.getParameter("csa"));

        customerService.updateCustomerLocation(location);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/createCustomerLocation", method = RequestMethod.GET)
    @ResponseBody
    public Object createCustomerLocation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerLocation location = new CustomerLocation();
        String[] requiredParams = new String[]{"code", "status"};
        for (String param : requiredParams) {
            if (isBlank(request.getParameter(param))) {
                throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{param}), null);
            }
        }
        location.setCustomerId(parseInt(request.getParameter("customerId")));
        location.setCode(request.getParameter("code"));
        location.setQuickName(request.getParameter("quickName"));
        location.setStatus(request.getParameter("status"));
        location.setAddress1(request.getParameter("address1"));
        location.setAddress2(request.getParameter("address2"));
        location.setCity(request.getParameter("city"));
        if (!isBlank(request.getParameter("stateProvince")))
            location.setStateProvince(request.getParameter("stateProvince"));
        location.setPostalCode(request.getParameter("postalCode"));
        location.setCountry(request.getParameter("country"));
        location.setBusPhone1(request.getParameter("busPhone1"));
        location.setBusPhone2(request.getParameter("busPhone2"));
        location.setAfterHoursPhone(request.getParameter("afterHoursPhone"));
        location.setFax(request.getParameter("fax"));
        location.setEmail(request.getParameter("email"));
        location.setOpenHours(request.getParameter("openHours"));
        String tempStr ;
        tempStr = request.getParameter("locationUsage");
        if (!isBlank(tempStr))
            location.setLocationUsage(parseInt(tempStr));
        location.setMapUrl(request.getParameter("mapUrl"));

        tempStr = request.getParameter("billingCompany");
        if (!isBlank(tempStr))
            location.setBillingCompany(parseInt(tempStr));

        tempStr = request.getParameter("brokerUs");
        if (!isBlank(tempStr))
            location.setBrokerUs(parseInt(tempStr));
        tempStr = request.getParameter("brokerCa");
        if (!isBlank(tempStr))
            location.setBrokerCa(parseInt(tempStr));
        tempStr = request.getParameter("altBrokerUs");
        if (!isBlank(tempStr))
            location.setAltBrokerUs(parseInt(tempStr));
        tempStr = request.getParameter("altBrokerCa");
        if (!isBlank(tempStr))
            location.setAltBrokerCa(parseInt(tempStr));
        tempStr = request.getParameter("brokerNat");
        if (!isBlank(tempStr))
            location.setBrokerNat(parseInt(tempStr));
        tempStr = request.getParameter("agentId");
        if (!isBlank(tempStr))
            location.setAgentId(parseInt(tempStr));
        location.setNotes(request.getParameter("notes"));
        location.setKnown(request.getParameter("known"));
        tempStr = request.getParameter("knownStatusIndex");
        if (!isBlank(tempStr))
            location.setKnownStatusIndex(parseInt(tempStr));
        location.setTsa(request.getParameter("tsa"));
        location.setTsaAirport(request.getParameter("tsaAirport"));
        location.setTsaContact(request.getParameter("tsaContact"));
        location.setCsa(request.getParameter("csa"));

        customerService.createCustomerLocation(location);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        Integer locationId = location.getId() + 1000000;
        map.put(PARAM_RECORDS, locationId);
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/deleteCustomerLocation", method = RequestMethod.GET)
    @ResponseBody
    public Object deleteCustomerLocation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String locationIds = request.getParameter("ids");
        List<String> locationIdList = asList(locationIds.split(","));
        for (String locationId : locationIdList) {
            try {
                customerService.deleteCustomerLocation(parseInt(locationId)-1000000);
            } catch (Exception e) {
                continue;
            }
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }


    @RequestMapping(value = "/customer/customerManager/selectCustomerContactById", method = RequestMethod.GET)
    @ResponseBody
    public Object selectCustomerContactById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String contactId = request.getParameter("contactId");
        Map<String, Object> map = newHashMap();
        if (isBlank(contactId)) {
            // new CustomerContact;
            map.put(PARAM_RECORDS, new CustomerContact());
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            return map;
        }
        try {
            CustomerContact contact = customerService.selectCustomerContactById(parseInt(contactId) - 1000000000);
            map.put(KEY_RESULT, RESPONSE_SUCCESS);
            map.put(PARAM_RECORDS, contact);
        } catch (Exception e) {
            map.put(KEY_RESULT, RESPONSE_ERROR);
        }
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/updateCustomerContact", method = RequestMethod.GET)
    @ResponseBody
    public Object updateCustomerContact(HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerContact contact = new CustomerContact();
        String[] requiredParams = new String[]{"id","locationId", "firstName", "lastName"};
        for (String param : requiredParams) {
            if (isBlank(request.getParameter(param))) {
                throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{param}), null);
            }
        }
        Integer contactId = parseInt(request.getParameter("id"));
        contact.setId(contactId);
        contact.setLocationId(parseInt(request.getParameter("locationId")));
        contact.setStatus(request.getParameter("status"));
        if (!isBlank(request.getParameter("contactType")))
            contact.setContactType(request.getParameter("contactType"));
        contact.setNotes(request.getParameter("notes"));
        contact.setFirstName(request.getParameter("firstName"));
        contact.setLastName(request.getParameter("lastName"));
        contact.setAddress1(request.getParameter("address1"));
        contact.setAddress2(request.getParameter("address2"));
        contact.setCity(request.getParameter("city"));
        if (!isBlank(request.getParameter("stateProvince")))
            contact.setStateProvince(request.getParameter("stateProvince"));
        contact.setCountry(request.getParameter("country"));
        contact.setPostalCode(request.getParameter("postalCode"));
        contact.setPhoneOffice(request.getParameter("phoneOffice"));
        contact.setPhoneMobile(request.getParameter("phoneMobile"));
        contact.setPhonePager(request.getParameter("phonePager"));
        contact.setPhoneFax(request.getParameter("phoneFax"));
        contact.setPhoneHome(request.getParameter("phoneHome"));
        contact.setEmail(request.getParameter("email"));
        contact.setMapUrl(request.getParameter("mapUrl"));
        if (!isBlank(request.getParameter("onlineStatus")))
            contact.setOnlineStatus(request.getParameter("onlineStatus"));
        if (!isBlank(request.getParameter("onlineCountry")))
            contact.setOnlineCountry(request.getParameter("onlineCountry"));
        contact.setCurrentFlight(request.getParameter("currentFlight"));
        contact.setCourierAirport(request.getParameter("courierAirport"));
        contact.setCourierCity(request.getParameter("courierCity"));
        contact.setNexus(request.getParameter("nexus"));
        contact.setCompanyCreditCard(request.getParameter("companyCreditCard"));
        contact.setTsaPreClearance(request.getParameter("tsaPreClearance"));
        contact.setGlobalEntry(request.getParameter("globalEntry"));

        String courierVisaList = request.getParameter("courierVisa");
        if (!isBlank(courierVisaList)) {
            List<Map<String, Object>> maps = JacksonMapper.getInstance().readValue(courierVisaList, List.class);

            if (null != maps && 0 != maps.size()) {
                List<CourierVisa> courierVisas = newArrayList();
                for (Map<String, Object> map : maps) {
                    CourierVisa courierVisa = new CourierVisa();

                    courierVisa.setContactId(contactId);
                    courierVisa.setCountry((String) map.get("country"));
                    courierVisa.setVisaNo((String) map.get("visaNo"));
                    courierVisa.setName((String) map.get("name"));
                    courierVisa.setNationality((String) map.get("nationality"));
                    courierVisa.setPassportNo((String) map.get("passportNo"));
                    courierVisa.setValidFrom(toDate((String) map.get("validFrom"), LONG_DATE_FORMAT_SHOW));
                    courierVisa.setValidTo(toDate((String) map.get("validTo"), LONG_DATE_FORMAT_SHOW));

                    courierVisa.setSector((String) map.get("sector"));
                    courierVisa.setNote((String) map.get("note"));
                    courierVisas.add(courierVisa);
                }
                contact.setCourierVisa(courierVisas);
            }
        }

        customerService.updateCustomerContact(contact);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/createCustomerContact", method = RequestMethod.GET)
    @ResponseBody
    public Object createCustomerContact(HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerContact contact = new CustomerContact();
        String[] requiredParams = new String[]{"locationId", "firstName", "lastName"};
        for (String param : requiredParams) {
            if (isBlank(request.getParameter(param))) {
                throw new BusinessException(LAKEY_ERROR_COMMON_REQUIED, arrayToList(new String[]{param}), null);
            }
        }
        contact.setLocationId(parseInt(request.getParameter("locationId"))-1000000);
        contact.setStatus(request.getParameter("status"));
        if (!isBlank(request.getParameter("contactType")))
            contact.setContactType(request.getParameter("contactType"));
        contact.setNotes(request.getParameter("notes"));
        contact.setFirstName(request.getParameter("firstName"));
        contact.setLastName(request.getParameter("lastName"));
        contact.setAddress1(request.getParameter("address1"));
        contact.setAddress2(request.getParameter("address2"));
        contact.setCity(request.getParameter("city"));
        if (!isBlank(request.getParameter("stateProvince")))
            contact.setStateProvince(request.getParameter("stateProvince"));
        contact.setCountry(request.getParameter("country"));
        contact.setPostalCode(request.getParameter("postalCode"));
        contact.setPhoneOffice(request.getParameter("phoneOffice"));
        contact.setPhoneMobile(request.getParameter("phoneMobile"));
        contact.setPhonePager(request.getParameter("phonePager"));
        contact.setPhoneFax(request.getParameter("phoneFax"));
        contact.setPhoneHome(request.getParameter("phoneHome"));
        contact.setEmail(request.getParameter("email"));
        contact.setMapUrl(request.getParameter("mapUrl"));
        if (!isBlank(request.getParameter("onlineStatus")))
            contact.setOnlineStatus(request.getParameter("onlineStatus"));
        if (!isBlank(request.getParameter("onlineCountry")))
            contact.setOnlineCountry(request.getParameter("onlineCountry"));
        contact.setCurrentFlight(request.getParameter("currentFlight"));
        contact.setCourierAirport(request.getParameter("courierAirport"));
        contact.setCurrentFlight(request.getParameter("currentFlight"));
        contact.setNexus(request.getParameter("nexus"));
        contact.setCompanyCreditCard(request.getParameter("companyCreditCard"));
        contact.setTsaPreClearance(request.getParameter("tsaPreClearance"));
        contact.setGlobalEntry(request.getParameter("globalEntry"));

        customerService.createCustomerContact(contact);
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        Integer contactId = contact.getId() + 1000000000;
        map.put(PARAM_RECORDS, contactId);
        return map;
    }

    @RequestMapping(value = "/customer/customerManager/deleteCustomerContact", method = RequestMethod.GET)
    @ResponseBody
    public Object deleteCustomerContact(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String contactIds = request.getParameter("ids");
        List<String> contactIdList = asList(contactIds.split(","));
        for (String contactId : contactIdList) {
            try {
                customerService.deleteCustomerContact((parseInt(contactId) - 1000000000));
            } catch (Exception e) {
                continue;
            }
        }
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }
    
    public Object testMethod(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //just adding for testing merge               
        Map<String, Object> map = newHashMap();
        map.put(KEY_RESULT, RESPONSE_SUCCESS);
        return map;
    }
}
