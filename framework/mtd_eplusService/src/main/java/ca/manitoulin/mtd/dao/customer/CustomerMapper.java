package ca.manitoulin.mtd.dao.customer;

import ca.manitoulin.mtd.dto.customer.Courier;
import ca.manitoulin.mtd.dto.customer.Customer;
import ca.manitoulin.mtd.dto.customer.CustomerContact;
import ca.manitoulin.mtd.dto.customer.CustomerLocation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerMapper {
    List<Customer> retrieveCustomerByType(@Param("customerType")String customerType,
                                          @Param("qucikName")String qucikName,
                                          @Param("locationKeyword")String locationKeyword,
                                          @Param("firstName")String firstName,
                                          @Param("lastName")String lastName,
                                          @Param("language") String lang);

    Customer retrieveCustomerProfileById(@Param("id")Integer entityId, @Param("language") String lang);

    void insertCustomer(Customer customer);

    void updateCustomer(Customer customer);

    void deleteCustomer(@Param("id") Integer entityId);

    List<Customer> loadCustomerTreeData(@Param("id") Integer entityId, @Param("keyword")String keyword, @Param("showOnline")Boolean showOnline);

    List<CustomerLocation> retrieveCustomerLocationByFuzzySearch(@Param("customerType")String customerType,
                                                                 @Param("qucikName")String qucikName,
                                                                 @Param("locationKeyword")String locationKeyword,
                                                                 @Param("firstName")String firstName,
                                                                 @Param("lastName")String lastName);

    List<CustomerLocation> retrieveCustomerContactByFuzzySearch(@Param("customerType")String customerType,
                                                                 @Param("qucikName")String qucikName,
                                                                 @Param("locationKeyword")String locationKeyword,
                                                                 @Param("firstName")String firstName,
                                                                 @Param("lastName")String lastName);

    List<CustomerLocation> selectCustomerLocationByEntityId(Integer entityId);

    CustomerLocation selectCustomerLocationByIdForTrip(Integer locationId);

    CustomerLocation selectCustomerLocationById(Integer locationId);

    CustomerLocation selectCustomerLocationByContactId(Integer contactId);

    void insertCustomerLocation(CustomerLocation customerLocation);

    void updateCustomerLocation(CustomerLocation customerLocation);

    void updateCustomerLocationInactiveByEntityId(Integer entityId);

    void updateCustomerContactInactiveByEntityId(Integer entityId);

    void updateCustomerContactInactiveByLocationId(Integer entityId);

    void deleteCustomerLocation(Integer locationId);

    List<CustomerContact> selectCustomerContactByLocationId(Integer locationId);

    List<CustomerContact> selectAllCustomerContact(@Param("keyword")String matchNameOrEmail,
                                                   @Param("firstName")String firstName,
                                                   @Param("lastName")String lastName,
                                                   @Param("email") String matchEmail,
    		                                        @Param("needEmail") Boolean needEmail);
    
    CustomerContact selectCustomerContactById(Integer contactId);

    void insertCustomerContact(CustomerContact customerContact);

    void updateCustomerContact(CustomerContact customerContact);

    void deleteCustomerContact(Integer contactId);

    List<CustomerLocation> selectCustomerLocationByCustomerType(@Param("customerType") String customerType);

    List<Courier> selectCourierList(@Param("companyId") Integer companyId,
                                    @Param("locationId") Integer locationId,
                                    @Param("status") String status,
                                    @Param("country") String country,
                                    @Param("airport") String airport,
                                    @Param("city") String city);

}
