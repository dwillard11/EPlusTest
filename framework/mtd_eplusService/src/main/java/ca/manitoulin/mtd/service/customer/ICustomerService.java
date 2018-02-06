package ca.manitoulin.mtd.service.customer;

import ca.manitoulin.mtd.dto.customer.Courier;
import ca.manitoulin.mtd.dto.customer.Customer;
import ca.manitoulin.mtd.dto.customer.CustomerContact;
import ca.manitoulin.mtd.dto.customer.CustomerLocation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Customer service interface
 * @author Bob Yu
 *
 */
public interface ICustomerService {
	/**
	 * Search customer by name, address, phone number
	 * @param keyword
	 * @return
	 */
    List<CustomerLocation> retrieveCustomerLocationByFuzzySearch(String keyword, String customerType);

	public List<CustomerLocation> retrieveCustomerLocationContactByFuzzySearch(String keyword, String customerType);

    List<Customer> retrieveCustomerByType(String keyword, String customerType) throws Exception;
	
	/**
	 * Retrieve customer profile, can choose with divisions or contacts
	 * @param id
	 * @return
	 */
	Customer retrieveCustomerProfileById(Integer id) throws Exception;

	Customer createCustomer(Customer company) throws Exception;

	void deleteCustomer(Integer customerId) throws Exception;

	void updateCustomer(Customer company) throws Exception;

	List<Customer> loadCustomerTreeData(Integer entityId, String keyword, Boolean showOnline) throws Exception;

	List<CustomerLocation> selectCustomerLocationByEntityId(Integer entityId) throws Exception;

	CustomerLocation selectCustomerLocationById(Integer locationId) throws Exception;

	CustomerLocation selectCustomerLocationByIdForTrip(Integer locationId) throws Exception;

	CustomerLocation selectCustomerLocationByContactId(Integer locationId) throws Exception;

	CustomerLocation createCustomerLocation(CustomerLocation location) throws Exception;

	CustomerLocation updateCustomerLocation(CustomerLocation customerLocation) throws Exception;

	void deleteCustomerLocation(@Param("id") Integer locationId) throws Exception;

	List<CustomerContact> selectCustomerContactByLocationId(Integer locationId) throws Exception;

	CustomerContact selectCustomerContactById(Integer contactId) throws Exception;

    List<CustomerContact> selectAllCustomerContact(String searchNameOrEmail, String searchName, String searchEmail) ;

	CustomerContact createCustomerContact(CustomerContact customerContact) throws Exception;

	CustomerContact updateCustomerContact(CustomerContact customerContact) throws Exception;

	void deleteCustomerContact(@Param("id") Integer contactId) throws Exception;

	List<CustomerLocation> retrieveCustomerLocationByCustomerType(String customerType);

	List<Courier> retrieveCourierList(Integer companyId,  Integer locationId, String status, String country, String airport, String city);
	
	List<CustomerContact> retrieveCustomerContactForEmail(String keyword);
}
