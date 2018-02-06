package ca.manitoulin.mtd.service.maintenance;

import ca.manitoulin.mtd.dto.maintenance.QuoteTemplate;
import ca.manitoulin.mtd.dto.maintenance.TripEventTemplate;
import ca.manitoulin.mtd.dto.operationconsole.Condition;
import ca.manitoulin.mtd.dto.operationconsole.TripEvent;
import ca.manitoulin.mtd.dto.security.Organization;
import ca.manitoulin.mtd.dto.security.Role;
import ca.manitoulin.mtd.dto.security.RoleAccessRight;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.support.AppEnum;

import java.util.List;
import java.util.Map;

public interface ISystemMaintenanceService {

    /*
    **department methods
     */
    List<Organization> retrieveDepartments(String advanceSearch) throws Exception;

    Organization retrieveDepartmentById(Integer departmentId) throws Exception;

    void addDepartment(Organization organization) throws Exception;

    void removeDepartment(Integer departmentId) throws Exception;

    void updateDepartment(Organization organization) throws Exception;
    /*
    **user methods
     */
    List<SecureUser> retrieveSecureUsers(String searchKey) throws Exception;

    SecureUser retrieveSecureUserByID(Integer id) throws Exception;

    void addSecureUser(SecureUser secureUser) throws Exception;

    void removeSecureUser(Integer id) throws Exception;

    void updateSecureUser(SecureUser secureUser) throws Exception;

    SecureUser retrieveSecureUserFromPMTUserByUID(String uid) throws Exception;

    SecureUser retrieveSecureUserByUID(String uid);

    void insertUserDivision(Integer userId, Integer departmentId) throws Exception;

    void deleteUserDivision(Integer userId) throws Exception;

    List<Organization> selectDepartmentsByUserID(Integer userId) throws Exception;

    void saveUserRole(Integer userId, Integer roleId) throws Exception;

    void deleteUserRole(Integer userId) throws Exception;

    /*
         **EpCode methods
         */
    List<AppEnum> retrieveBusinessCode(String name) throws Exception;

    List<AppEnum> getEpCodeListByType(String name) throws Exception;
    AppEnum getEpCodeById(String key) throws Exception;
    List<String> retrieveCodeCategory() throws Exception;

    void addBusinessCode(AppEnum appEnum) throws Exception;

    void removeBusinessCode(Integer id) throws Exception;

    void updateBusinessCode(AppEnum appEnum) throws Exception;

    List<AppEnum> getCountry() throws Exception;

    List<AppEnum> getProvinceByCountry(String country) throws Exception;

    AppEnum getMultipileLanguageByText(String textStr) throws Exception;

    List<AppEnum> getEntityContactCountry() throws Exception;

    /*
    **Quote template methods
     */
    List<QuoteTemplate> retrieveQuoteTemplateListByType(String tripType) throws Exception;

    boolean checkDuplicateQuoteTemplateByTypeAndName(String tripType, String name) throws Exception;

    List<QuoteTemplate> retrieveQuoteTemplates(String tripType, String name) throws Exception;

    QuoteTemplate retrieveQuoteTemplateById(Integer id) throws Exception;

    void addQuoteTemplate(QuoteTemplate quoteTemplate) throws Exception;

    void removeQuoteTemplateByCategory(String tripType, String name) throws Exception;

    void removeQuoteTemplate(Integer id) throws Exception;

    void updateQuoteTemplate(QuoteTemplate quoteTemplate) throws Exception;

    List<Map<String, Object>> retrieveQuoteTemplateTree(String tripType, String name) throws Exception;

    List<Map<String, Object>> retrieveQuoteTemplateTree(List<Condition> conditions);

    void updateQuoteTemplateSequence(Integer id, Integer categorySequence, Integer sequence) throws Exception;

    /*
    **trip event template methods
     */
    List<TripEventTemplate> retrieveTripEventTemplateListByType(String tripType) throws Exception;

    boolean checkDuplicateEventTemplateByTypeAndName(String tripType, String name) throws Exception;

    List<TripEventTemplate> retrieveTripEventTemplatesForTripTemplate(String tripType) throws Exception;

    List<TripEventTemplate> retrieveTripEventTemplates(String tripType, String name) throws Exception;

    TripEventTemplate retrieveTripEventTemplateById(Integer id) throws Exception;

    List<Map<String, Object>> retrieveEventTemplateTree(List<TripEvent> conditions);

    void addTripEventTemplate(TripEventTemplate tripEventTemplate) throws Exception;

    void removeEventTemplateByCategory(String tripType, String name) throws Exception;

    void removeTripEventTemplate(Integer id) throws Exception;

    void updateTripEventTemplate(TripEventTemplate tripEventTemplate) throws Exception;

    void updateTripEventTemplateSequence(Integer id, Integer categorySequence, Integer sequence) throws Exception;

    /*
    **Role methods
     */
    List<Role> retrieveRoles(String advanceSearch) throws Exception;

    Role retrieveRoleById(Integer roleId) throws Exception;

    List<RoleAccessRight> retrieveRoleAccessRights(Integer id) throws Exception;

    void addRole(Role role) throws Exception;

    void removeRole(Integer roleId) throws Exception;

    void updateRole(Role role) throws Exception;
}
