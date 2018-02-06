package ca.manitoulin.mtd.service.customer.impl;

import ca.manitoulin.mtd.dao.customer.CourierVisaMapper;
import ca.manitoulin.mtd.dao.customer.CustomerMapper;
import ca.manitoulin.mtd.dto.customer.*;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.service.customer.ICustomerService;
import ca.manitoulin.mtd.util.ApplicationSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static ca.manitoulin.mtd.util.ApplicationSession.get;
import static com.google.common.base.Optional.of;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class CustomerService implements ICustomerService {

	private static Logger logger = Logger.getLogger(CustomerService.class);


	@Autowired
	private CustomerMapper customerMapper;


	@Autowired
	private CourierVisaMapper courierVisaMapper;

	@Override
    public List<CustomerLocation> retrieveCustomerLocationByFuzzySearch(String keyword, String customerType) {
        //of(advanceSearch);
		String leftStr;
		String rightStr;
		String qucikName = "";
		String locationKeyword = "";
		String firstName = "";
		String lastName = "";

		if(keyword.indexOf("+") != -1){
			leftStr = keyword.substring(0, keyword.indexOf("+"));
			rightStr = keyword.substring(keyword.indexOf("+") + 1, keyword.length());
		}else{
			leftStr = keyword;
			rightStr = "";
		}

		if(leftStr.indexOf(",") != -1){
			qucikName = leftStr.substring(0, leftStr.indexOf(","));
			locationKeyword = leftStr.substring(leftStr.indexOf(",") + 1, leftStr.length());
		}
		else{
			qucikName = leftStr;
		}
		if (!isBlank(rightStr)) {
			if (rightStr.indexOf(",") != -1) {
				firstName = rightStr.substring(0, rightStr.indexOf(","));
				lastName = rightStr.substring(rightStr.indexOf(",") + 1, rightStr.length());
			} else {
				firstName = rightStr;
			}
		}

		return customerMapper.retrieveCustomerLocationByFuzzySearch(customerType, qucikName, locationKeyword, firstName, lastName);
	}

	@Override
	public List<CustomerLocation> retrieveCustomerLocationContactByFuzzySearch(String keyword, String customerType) {
		//of(advanceSearch);
		String leftStr;
		String rightStr;
		String qucikName = "";
		String locationKeyword = "";
		String firstName = "";
		String lastName = "";

		if(keyword.indexOf("+") != -1){
			leftStr = keyword.substring(0, keyword.indexOf("+"));
			rightStr = keyword.substring(keyword.indexOf("+") + 1, keyword.length());
		}else{
			leftStr = keyword;
			rightStr = "";
		}

		if(leftStr.indexOf(",") != -1){
			qucikName = leftStr.substring(0, leftStr.indexOf(","));
			locationKeyword = leftStr.substring(leftStr.indexOf(",") + 1, leftStr.length());
		}
		else{
			qucikName = leftStr;
		}
		if (!isBlank(rightStr)) {
			if (rightStr.indexOf(",") != -1) {
				firstName = rightStr.substring(0, rightStr.indexOf(","));
				lastName = rightStr.substring(rightStr.indexOf(",") + 1, rightStr.length());
			} else {
				firstName = rightStr;
			}
			return customerMapper.retrieveCustomerContactByFuzzySearch(customerType, qucikName, locationKeyword, firstName, lastName);
		}
		else{
			return customerMapper.retrieveCustomerLocationByFuzzySearch(customerType, qucikName, locationKeyword, firstName, lastName);
		}
	}

	@Override
	public List<Customer> retrieveCustomerByType(String keyword, String customerType) throws Exception {
		String leftStr;
		String rightStr;
		String qucikName = "";
		String locationKeyword = "";
		String firstName = "";
		String lastName = "";
		if (keyword == null)
			keyword = "";

		if(keyword.indexOf("+") != -1){
			leftStr = keyword.substring(0, keyword.indexOf("+"));
			rightStr = keyword.substring(keyword.indexOf("+") + 1, keyword.length());
		}else{
			leftStr = keyword;
			rightStr = "";
		}

		if(leftStr.indexOf(",") != -1){
			qucikName = leftStr.substring(0, leftStr.indexOf(","));
			locationKeyword = leftStr.substring(leftStr.indexOf(",") + 1, leftStr.length());
		}
		else{
			qucikName = leftStr;
		}
		if (!isBlank(rightStr)) {
			if (rightStr.indexOf(",") != -1) {
				firstName = rightStr.substring(0, rightStr.indexOf(","));
				lastName = rightStr.substring(rightStr.indexOf(",") + 1, rightStr.length());
			} else {
				firstName = rightStr;
			}
		}

		SecureUser currentUser = ApplicationSession.get();
		return customerMapper.retrieveCustomerByType(customerType, qucikName, locationKeyword, firstName, lastName, currentUser.getReferLanguage());
	}

    @Override
    public Customer retrieveCustomerProfileById(Integer entityId) throws Exception {
        SecureUser currentUser = ApplicationSession.get();
        return customerMapper.retrieveCustomerProfileById(entityId, currentUser.getReferLanguage());
    }

    @Override
    public Customer createCustomer(Customer customer) throws Exception {
        of(customer);
        of(customer.getName());

		addTagToCustomer(customer);
		customerMapper.insertCustomer(customer);
		return customer;
    }

    private void addTagToCustomer(Customer customer) {
        SecureUser currentUser = get();
		customer.setUpdatedBy(currentUser.getUid());
		customer.setCurrentCompany(currentUser.getCompany());
		customer.setCurrentCustomer(currentUser.getCompany());
    }

    @Override
    public void deleteCustomer(Integer entityId) throws Exception {
        of(entityId);
		customerMapper.deleteCustomer(entityId);
    }

    @Override
    public void updateCustomer(Customer customer) throws Exception {
        of(customer);
        of(customer.getId());

        if (customer.getStatus().equals("Inactive"))
		{
			customerMapper.updateCustomerLocationInactiveByEntityId(customer.getId());

			customerMapper.updateCustomerContactInactiveByEntityId(customer.getId());
		}

		addTagToCustomer(customer);
		customerMapper.updateCustomer(customer);
    }

	@Override
	public List<Customer> loadCustomerTreeData(Integer entityId, String keyword, Boolean showOnline) throws Exception {
		return customerMapper.loadCustomerTreeData(entityId, keyword, showOnline);
	}

	@Override
	public List<CustomerLocation> selectCustomerLocationByEntityId(Integer entityId) throws Exception {
		of(entityId);
		return customerMapper.selectCustomerLocationByEntityId(entityId);
	}

	@Override
	public CustomerLocation selectCustomerLocationById(Integer locationId) throws Exception {
		of(locationId);
		return customerMapper.selectCustomerLocationById(locationId);
	}

	@Override
	public CustomerLocation selectCustomerLocationByIdForTrip(Integer locationId) throws Exception {
		of(locationId);
		return customerMapper.selectCustomerLocationByIdForTrip(locationId);
	}

	@Override
	public CustomerLocation selectCustomerLocationByContactId(Integer locationId) throws Exception {
		of(locationId);
		return customerMapper.selectCustomerLocationByContactId(locationId);
	}

	@Override
	public CustomerLocation createCustomerLocation(CustomerLocation location) throws Exception {
		of(location);
		of(location.getCode());


		addTagToCustomerLocation(location);
		customerMapper.insertCustomerLocation(location);
		return location;
	}

	private void addTagToCustomerLocation(CustomerLocation location) {
		SecureUser currentUser = get();
		location.setUpdatedBy(currentUser.getUid());
		location.setCurrentCompany(currentUser.getCompany());
		location.setCurrentCustomer(currentUser.getCompany());
	}

	@Override
	public void deleteCustomerLocation(Integer locationId) throws Exception {
		of(locationId);
		customerMapper.deleteCustomerLocation(locationId);
	}

	@Override
	public CustomerLocation updateCustomerLocation(CustomerLocation location) throws Exception {
		of(location);
		of(location.getId());

		if (location.getStatus().equals("Inactive"))
		{
			customerMapper.updateCustomerContactInactiveByLocationId(location.getId());
		}

		addTagToCustomerLocation(location);
		customerMapper.updateCustomerLocation(location);
		return location;
	}

	@Override
	public List<CustomerContact> selectCustomerContactByLocationId(Integer locationId) throws Exception {
		of(locationId);
		return customerMapper.selectCustomerContactByLocationId(locationId);
	}

	@Override
	public CustomerContact selectCustomerContactById(Integer contactId) throws Exception {
		of(contactId);
		CustomerContact CustomerContact = customerMapper.selectCustomerContactById(contactId);

		List<CourierVisa> courierVisas = courierVisaMapper.selectCourierVisaByContactId(contactId);
		CustomerContact.setCourierVisa(courierVisas);
		return CustomerContact;
	}

    @Override
    public List<CustomerContact> selectAllCustomerContact(String searchNameOrEmail, String searchName, String searchEmail)  {
        return customerMapper.selectAllCustomerContact(searchNameOrEmail, searchName, searchName, searchEmail, false);
    }

    @Override
	public CustomerContact createCustomerContact(CustomerContact contact) throws Exception {
		of(contact);

		addTagToCustomerContact(contact);
		customerMapper.insertCustomerContact(contact);
		return contact;
	}

	private void addTagToCustomerContact(CustomerContact contact) {
		SecureUser currentUser = get();
		contact.setUpdatedBy(currentUser.getUid());
		contact.setCurrentCompany(currentUser.getCompany());
		contact.setCurrentCustomer(currentUser.getCompany());
	}

	@Override
	public void deleteCustomerContact(Integer contactId) throws Exception {
		of(contactId);
		customerMapper.deleteCustomerContact(contactId);
	}

	@Override
	public List<CustomerLocation> retrieveCustomerLocationByCustomerType(String customerType) {
		return customerMapper.selectCustomerLocationByCustomerType(customerType);
	}

	@Override
	public CustomerContact updateCustomerContact(CustomerContact contact) throws Exception {
		of(contact);
		of(contact.getId());
		//Clean Courier Visa
		courierVisaMapper.deleteCourierVisaByContactId(contact.getId());

		//save Courier Visa
		List<CourierVisa> courierVisas = contact.getCourierVisa() ;
		if (courierVisas!=null) {
			for (CourierVisa courierVisa : contact.getCourierVisa()) {
				courierVisa.setContactId(contact.getId());
				addTagToCourierVisa(courierVisa);
				courierVisaMapper.insertCourierVisa(courierVisa);
			}
		}

		addTagToCustomerContact(contact);
		customerMapper.updateCustomerContact(contact);
		return contact;
	}

	private void addTagToCourierVisa(CourierVisa courierVisa) {
		SecureUser currentUser = get();
		courierVisa.setUpdatedBy(currentUser.getUid());
		courierVisa.setCurrentCompany(currentUser.getCompany());
		courierVisa.setCurrentCustomer(currentUser.getCompany());
	}

	@Override
	public List<Courier> retrieveCourierList(Integer companyId,
											Integer locationId,
											String status,
											String country,
											String airport,
											String city) {
		return customerMapper.selectCourierList(companyId, locationId, status, country, airport, city);
	}

	@Override
	public List<CustomerContact> retrieveCustomerContactForEmail(String keyword) {
		String firstName = "";
		String lastName = "";

		if(keyword.indexOf(",") != -1){
			firstName = keyword.substring(0, keyword.indexOf(","));
			lastName = keyword.substring(keyword.indexOf(",") + 1, keyword.length());
			keyword = "";
		}
		return customerMapper.selectAllCustomerContact(keyword, firstName, lastName, null, true);
	}
}
