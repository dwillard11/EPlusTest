package ca.manitoulin.mtd.service.maintenance.impl;

import ca.manitoulin.mtd.dao.maintenance.*;
import ca.manitoulin.mtd.dao.operationconsole.TripMapper;
import ca.manitoulin.mtd.dao.security.AccountMapper;
import ca.manitoulin.mtd.dao.security.OrganizationMapper;
import ca.manitoulin.mtd.dao.security.SecureUserMapper;
import ca.manitoulin.mtd.dto.maintenance.QuoteTemplate;
import ca.manitoulin.mtd.dto.maintenance.TripEventTemplate;
import ca.manitoulin.mtd.dto.maintenance.TripEventTemplateCost;
import ca.manitoulin.mtd.dto.maintenance.TripEventTemplateNotify;
import ca.manitoulin.mtd.dto.operationconsole.Condition;
import ca.manitoulin.mtd.dto.operationconsole.TripEvent;
import ca.manitoulin.mtd.dto.security.*;
import ca.manitoulin.mtd.dto.support.AppEnum;
import ca.manitoulin.mtd.service.maintenance.ISystemMaintenanceService;
import ca.manitoulin.mtd.util.ApplicationSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static ca.manitoulin.mtd.util.ApplicationSession.get;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Integer.parseInt;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class SystemMaintenanceService implements ISystemMaintenanceService {

	private static Logger logger = Logger.getLogger(SystemMaintenanceService.class);
	
	@Autowired
	private QuoteTemplateMapper quoteTemplateMapper;
	
	@Autowired
	private TripEventTemplateMapper tripEventTemplateMapper;

	@Autowired
	private TripEventTemplateCostMapper tripEventTemplateCostMapper;

	@Autowired
	private TripEventTemplateNotifyMapper tripEventTemplateNotifyMapper;
	
	@Autowired
	private BusinessCodeMapper businessCodeMapper;
	
	@Autowired
	private SecureUserMapper secureUserMapper;
	
	@Autowired
	private OrganizationMapper organizationMapper;
	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private AccountMapper accountMapper;
	
	@Autowired
	private TripMapper tripMapper;

	/*
    **department methods
     */
	@Override
	public List<Organization> retrieveDepartments(String advanceSearch) throws Exception {
		//of(advanceSearch);
		return organizationMapper.selectDepartment(ApplicationSession.get().getReferLanguage());
	}

    @Override
    public Organization retrieveDepartmentById(Integer departmentId) throws Exception {
        of(departmentId);
        return organizationMapper.selectDepartmentById(departmentId);
    }

    @Override
    public void addDepartment(Organization organization) throws Exception {
        of(organization);
        of(organization.getName());
		addTagToDepartment(organization);
        organizationMapper.insertDepartment(organization);
    }

    @Override
    public void removeDepartment(Integer dpId) throws Exception {
        of(dpId);
        organizationMapper.deleteDepartment(dpId);
    }

    @Override
    public void updateDepartment(Organization organization) throws Exception {
        of(organization);
        of(organization.getId());
		addTagToDepartment(organization);
        organizationMapper.updateDepartment(organization);
    }

	private void addTagToDepartment(Organization organization) {
		SecureUser currentUser = get();
		organization.setUpdatedBy(currentUser.getUid());
		organization.setCurrentCompany(currentUser.getCompany());
		organization.setCurrentCustomer(currentUser.getCompany());
	}

    /*
    **user methods
     */

	@Override
	public List<SecureUser> retrieveSecureUsers(String searchKey) throws Exception {
		return secureUserMapper.selectSecureUsers(searchKey, ApplicationSession.get().getReferLanguage());
	}

	@Override
	public SecureUser retrieveSecureUserByID(Integer id) throws Exception {
		of(id);
		SecureUser secureUser = secureUserMapper.selectSecureUserByID(id);
		List<Role> roles = roleMapper.selectRolesByUserID(id);
		List<Account> accounts = accountMapper.selectAccountsByUserID(id);
		if (null != roles) {
			secureUser.setRoles(roles);
		}
		if (null != accounts) {
			secureUser.setGlobalAccount(accounts);
		}
		return secureUser;
	}

	@Override
	public void addSecureUser(SecureUser secureUser) throws Exception {
		of(secureUser);
		addTagToSecureUser(secureUser);
		secureUserMapper.insertSecureUser(secureUser);
	}


	@Override
	public void removeSecureUser(Integer id) throws Exception {
		of(id);
		secureUserMapper.deleteSecureUser(id);
	}

	@Override
	public void updateSecureUser(SecureUser secureUser) throws Exception {
		addTagToSecureUser(secureUser);
		secureUserMapper.updateSecureUser(secureUser);
	}

	private void addTagToSecureUser(SecureUser secureUser) {
		SecureUser currentUser = get();
		secureUser.setUpdatedBy(currentUser.getUid());
		secureUser.setCurrentCompany(currentUser.getCompany());
		secureUser.setCurrentCustomer(currentUser.getCompany());
	}

	@Override
	public SecureUser retrieveSecureUserFromPMTUserByUID(String uid) throws Exception {
		return secureUserMapper.selectUserFromPMTUserByUID(uid);
	}

	@Override
	public SecureUser retrieveSecureUserByUID(String uid) {
		return secureUserMapper.selectUserByUID(uid);
	}


	@Override
	public void saveUserRole(Integer userId, Integer roleId) throws Exception {
		List<Role> roles = roleMapper.selectRolesByUserID(userId);
		if (!isUserRole(roleId, roles)) {
			SecureUser currentUser = get();
			String updatedBy = currentUser.getUid();
			String currentCompany = currentUser.getCompany();
			String currentCustomer = currentUser.getCompany();

			secureUserMapper.insertUserRole(userId, roleId, updatedBy, currentCompany, currentCustomer);
		}
	}

	private boolean isUserRole(Integer roleId,List<Role> userRoles) {
		if (isNotEmpty(userRoles)) {
			for (Role userRole : userRoles) {
				if (userRole.getId() == roleId) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void deleteUserRole(Integer userId) throws Exception {
		List<Role> roles = roleMapper.selectRolesByUserID(userId);
		secureUserMapper.deleteUserRole(userId);
	}

	@Override
	public void insertUserDivision(Integer userId, Integer departmentId) throws Exception {
		SecureUser currentUser = get();
		String updatedBy = currentUser.getUid();
		String currentCompany = currentUser.getCompany();
		String currentCustomer = currentUser.getCompany();

		secureUserMapper.insertUserDivision(userId, departmentId, updatedBy, currentCompany, currentCustomer);
	}

	@Override
	public void deleteUserDivision(Integer userId) throws Exception {
		secureUserMapper.deleteUserDivision(userId);
	}

	@Override
	public List<Organization> selectDepartmentsByUserID(Integer userId) throws Exception{
		return secureUserMapper.selectDepartmentsByUserID(userId);
	}

	/*
	 **EpCode methods
	 */
	@Override
	public List<AppEnum> retrieveBusinessCode(String name) throws Exception {
		return businessCodeMapper.selectBusinessCode(name);
	}

	@Override
	public List<AppEnum> getEpCodeListByType(String name) throws Exception {
		return businessCodeMapper.getEpCodeListByType(name, ApplicationSession.get().getReferLanguage());
	}

	@Override
	public AppEnum getEpCodeById(String key) throws Exception {
		if (StringUtils.isEmpty(key)) {
			return  new AppEnum();
		}
		return businessCodeMapper.getEpCodeById(key);
	}

	@Override
	public List<String> retrieveCodeCategory() throws Exception {
		return businessCodeMapper.selectCodeCategory();
	}

	@Override
	public void addBusinessCode(AppEnum appEnum) throws Exception {
		of(appEnum);
		of(appEnum.getSortingOrder());
		addTagToBusinessCode(appEnum);
		businessCodeMapper.insertBusinessCode(appEnum);
	}

	@Override
	public void removeBusinessCode(Integer id) throws Exception {
		of(id);
		businessCodeMapper.deleteBusinessCode(id);
	}

	@Override
	public void updateBusinessCode(AppEnum appEnum) throws Exception {
		of(appEnum);
		of(appEnum.getId());
		of(appEnum.getSortingOrder());
		addTagToBusinessCode(appEnum);
		businessCodeMapper.updateBusinessCode(appEnum);
	}

	private void addTagToBusinessCode(AppEnum appEnum) {
		SecureUser currentUser = get();
		appEnum.setUpdatedBy(currentUser.getUid());
		appEnum.setCurrentCompany(currentUser.getCompany());
		appEnum.setCurrentCustomer(currentUser.getCompany());
	}

	@Override
	public List<AppEnum> getCountry() throws Exception {
		return businessCodeMapper.getCountry();
	}

	@Override
	public List<AppEnum> getProvinceByCountry(String country) throws Exception {
		return businessCodeMapper.getProvinceByCountry(country);
	}

	@Override
	public AppEnum getMultipileLanguageByText(String textStr) throws Exception {
		return businessCodeMapper.getMultipileLanguageByText(textStr);
	}

	@Override
	public List<AppEnum> getEntityContactCountry() throws Exception {
		return businessCodeMapper.getEntityContactCountry();
	}

	/*
    **Quote template methods
     */
	@Override
	public List<QuoteTemplate> retrieveQuoteTemplateListByType(String tripType) throws Exception {
		
		SecureUser currentUser = ApplicationSession.get();
		return quoteTemplateMapper.selectQuoteTemplateListByType(tripType, "", currentUser.getReferLanguage());
	}

	@Override
	public boolean checkDuplicateQuoteTemplateByTypeAndName(String tripType, String name) throws Exception {
		SecureUser currentUser = ApplicationSession.get();
		List<QuoteTemplate> list = quoteTemplateMapper.selectQuoteTemplateListByType(tripType, name, currentUser.getReferLanguage());
		if (list != null && list.size() > 0)
			return true;
		else
			return false;
	}

	@Override
	public List<QuoteTemplate> retrieveQuoteTemplates(String tripType, String name) throws Exception {
        return quoteTemplateMapper.selectQuoteTemplates(tripType, name);
	}

    @Override
    public QuoteTemplate retrieveQuoteTemplateById(Integer id) throws Exception {
        return quoteTemplateMapper.selectQuoteTemplateById(id);
    }

	@Override
    public void addQuoteTemplate(QuoteTemplate quoteTemplate) throws Exception {
		addTagToQuoteTemplate(quoteTemplate);
		setQuoteTemplateCategorySequence(quoteTemplate);
        quoteTemplateMapper.insertQuoteTemplate(quoteTemplate);
	}

	@Override
	public void removeQuoteTemplateByCategory(String tripType, String name) throws Exception {
		quoteTemplateMapper.deleteQuoteTemplateByCategory(tripType, name);
	}

	@Override
    public void removeQuoteTemplate(Integer id) throws Exception {
        quoteTemplateMapper.deleteQuoteTemplate(id);
	}

	@Override
    public void updateQuoteTemplate(QuoteTemplate quoteTemplate) throws Exception {
		addTagToQuoteTemplate(quoteTemplate);
		setQuoteTemplateCategorySequence(quoteTemplate);
        quoteTemplateMapper.updateQuoteTemplate(quoteTemplate);
	}

	@Override
	public void updateQuoteTemplateSequence(Integer id, Integer categorySequence, Integer sequence) throws Exception {
		QuoteTemplate quoteTemplate = new QuoteTemplate();
		quoteTemplate.setId(id);
		quoteTemplate.setSequence(sequence);

		SecureUser currentUser = get();
		quoteTemplate.setUpdatedBy(currentUser.getUid());
		quoteTemplate.setCurrentCompany(currentUser.getCompany());
		quoteTemplate.setCurrentCustomer(currentUser.getCompany());
		if (categorySequence >= 1){
			quoteTemplate.setCategorySequence(categorySequence);
			quoteTemplateMapper.updateQuoteTemplateCategorySequence(quoteTemplate);
		}
		else{
			quoteTemplateMapper.updateQuoteTemplateSequence(quoteTemplate);
		}
	}

	private QuoteTemplate setQuoteTemplateCategorySequence(QuoteTemplate quoteTemplate){
		boolean isNewCategory = false;
		Integer id = 0;
		if (quoteTemplate.getId()==null) {
			isNewCategory = true;
		} else {
			id = quoteTemplate.getId();
			QuoteTemplate temp = quoteTemplateMapper.selectQuoteTemplateById(id);
			if (temp != null) {
				if (!temp.getCategory().equals(quoteTemplate.getCategory())) {
					isNewCategory = true;
				}
			}
		}

		if (isNewCategory){
			Integer itemSeq = 0;
			Integer categorySeq = quoteTemplateMapper.getCategorySequenceByCategory(quoteTemplate.getName(), quoteTemplate.getCategory());
			if (categorySeq == null) {
				categorySeq = quoteTemplateMapper.getLastCategorySequenceByName(quoteTemplate.getName(), id);
				if (categorySeq == null) {
					categorySeq = 1;
				} else {
					categorySeq = categorySeq + 1;
				}
				itemSeq = 1;
			} else {
				itemSeq = quoteTemplateMapper.getItemSequenceByCategory(quoteTemplate.getName(), quoteTemplate.getCategory());
				if (itemSeq == null) {
					itemSeq = 1;
				} else {
					itemSeq = itemSeq + 1;
				}
			}
			quoteTemplate.setCategorySequence(categorySeq);
			quoteTemplate.setSequence(itemSeq);
		}
		return quoteTemplate;
	}

	private void addTagToQuoteTemplate(QuoteTemplate quoteTemplate) {
		SecureUser currentUser = get();
		quoteTemplate.setUpdatedBy(currentUser.getUid());
		quoteTemplate.setCurrentCompany(currentUser.getCompany());
		quoteTemplate.setCurrentCustomer(currentUser.getCompany());
	}

	@Override
	public List<Map<String, Object>> retrieveQuoteTemplateTree(String tripType, String name) throws Exception {
		List<Map<String, Object>> result = newArrayList();
		String prevLevel1 = "";
		String prevLevel2 = "";
		String thisLevel1;
		String thisLevel2;
		int pid = 0;
		int cpid = 0;
		int categorySeq = 0;
		int itemSeq = 0;

		List<QuoteTemplate> findAll = retrieveQuoteTemplates(tripType, name);
		if (isNotEmpty(findAll)) {
			for (QuoteTemplate quoteTemplate : findAll) {
				thisLevel1 = quoteTemplate.getName();
				Map<String, Object> quoteTreeLevel1 = newHashMap();
				quoteTreeLevel1.put("id", quoteTemplate.getId());
				quoteTreeLevel1.put("pId", 0);
				quoteTreeLevel1.put("name", quoteTemplate.getName());
				quoteTreeLevel1.put("t", quoteTemplate.getName());
				quoteTreeLevel1.put("entityId", quoteTemplate.getId());
				quoteTreeLevel1.put("open", "true");
				quoteTreeLevel1.put("type", "event");
				quoteTreeLevel1.put("TripType", quoteTemplate.getType());
				quoteTreeLevel1.put("EventName", quoteTemplate.getName());
				quoteTreeLevel1.put("Category", quoteTemplate.getCategory());
				if (!prevLevel1.equals(thisLevel1)) {
					categorySeq = 0;
					itemSeq = 0;
					result.add(quoteTreeLevel1);
					pid = quoteTemplate.getId();
				}
				thisLevel2 = quoteTemplate.getCategory();
				Map<String, Object> quoteTreeData = newHashMap();
				quoteTreeData.put("id", quoteTemplate.getId() + 1000000);
				quoteTreeData.put("pId", pid);
				quoteTreeData.put("name", quoteTemplate.getCategory());
				quoteTreeData.put("t", quoteTemplate.getCategory());
				quoteTreeData.put("open", "true");
				quoteTreeData.put("entityId", quoteTemplate.getId());
				quoteTreeData.put("type", "category");
				quoteTreeData.put("TripType", quoteTemplate.getType());
				quoteTreeData.put("EventName", quoteTemplate.getName());
				quoteTreeData.put("Category", quoteTemplate.getCategory());
				if (!prevLevel2.equals(thisLevel2) || !prevLevel1.equals(thisLevel1)) {
					itemSeq = 0;
					categorySeq = categorySeq + 1;
					quoteTreeData.put("categorySeq", categorySeq);
					result.add(quoteTreeData);
					cpid = quoteTemplate.getId() + 1000000;
				}
				Map<String, Object> quoteTreeItem = newHashMap();
				quoteTreeItem.put("id", quoteTemplate.getId() + 100000000);
				quoteTreeItem.put("pId", cpid);
				quoteTreeItem.put("name", quoteTemplate.getItem());
				quoteTreeItem.put("t", quoteTemplate.getItem());
				quoteTreeItem.put("open", "true");
				quoteTreeItem.put("entityId", quoteTemplate.getId());
				quoteTreeItem.put("type", "item");
				quoteTreeItem.put("TripType", quoteTemplate.getType());
				quoteTreeItem.put("EventName", quoteTemplate.getName());
				quoteTreeItem.put("Category", quoteTemplate.getCategory());
				itemSeq = itemSeq + 1;
				quoteTreeItem.put("itemSeq", itemSeq);
				result.add(quoteTreeItem);
				prevLevel1 = thisLevel1;
				prevLevel2 = thisLevel2;
			}
			return result;
		}

		return newArrayList();

	}

	@Override
	public List<Map<String, Object>> retrieveQuoteTemplateTree(List<Condition> findAll) {
		List<Map<String, Object>> result = newArrayList();
		String prevLevel1 = "";
		String prevLevel2 = "";
		String thisLevel1;
		String thisLevel2;
		int pid = 0;
		int cpid = 0;
		int categorySeq = 0;
		int itemSeq = 0;

		if (isNotEmpty(findAll)) {
			for (Condition quoteTemplate : findAll) {
				thisLevel1 = quoteTemplate.getName();
				Map<String, Object> quoteTreeLevel1 = newHashMap();
				quoteTreeLevel1.put("id", quoteTemplate.getId());
				quoteTreeLevel1.put("pId", 0);
				quoteTreeLevel1.put("name", quoteTemplate.getName());
				quoteTreeLevel1.put("t", quoteTemplate.getName());
				quoteTreeLevel1.put("entityId", quoteTemplate.getId());
				quoteTreeLevel1.put("open", "true");
				quoteTreeLevel1.put("type", "event");
				quoteTreeLevel1.put("TripType", quoteTemplate.getType());
				quoteTreeLevel1.put("EventName", quoteTemplate.getName());
				quoteTreeLevel1.put("Category", quoteTemplate.getCategory());
				if (!prevLevel1.equals(thisLevel1)) {
					categorySeq = 0;
					itemSeq = 0;
					result.add(quoteTreeLevel1);
					pid = quoteTemplate.getId();
				}
				thisLevel2 = quoteTemplate.getCategory();
				Map<String, Object> quoteTreeData = newHashMap();
				quoteTreeData.put("id", quoteTemplate.getId() + 1000000);
				quoteTreeData.put("pId", pid);
				quoteTreeData.put("name", quoteTemplate.getCategory());
				quoteTreeData.put("t", quoteTemplate.getCategory());
				quoteTreeData.put("open", "true");
				quoteTreeData.put("entityId", quoteTemplate.getId());
				quoteTreeData.put("type", "category");
				quoteTreeData.put("TripType", quoteTemplate.getType());
				quoteTreeData.put("EventName", quoteTemplate.getName());
				quoteTreeData.put("Category", quoteTemplate.getCategory());
				if (!prevLevel2.equals(thisLevel2) || !prevLevel1.equals(thisLevel1)) {
					itemSeq = 0;
					categorySeq = categorySeq + 1;
					quoteTreeData.put("categorySeq", categorySeq);
					result.add(quoteTreeData);
					cpid = quoteTemplate.getId() + 1000000;
				}
				Map<String, Object> quoteTreeItem = newHashMap();
				quoteTreeItem.put("id", quoteTemplate.getId() + 100000000);
				quoteTreeItem.put("pId", cpid);
				quoteTreeItem.put("name", quoteTemplate.getItem());
				quoteTreeItem.put("t", quoteTemplate.getItem());
				quoteTreeItem.put("open", "true");
				quoteTreeItem.put("entityId", quoteTemplate.getId());
				quoteTreeItem.put("type", "item");
				quoteTreeItem.put("TripType", quoteTemplate.getType());
				quoteTreeItem.put("EventName", quoteTemplate.getName());
				quoteTreeItem.put("Category", quoteTemplate.getCategory());
				itemSeq = itemSeq + 1;
				quoteTreeItem.put("itemSeq", itemSeq);
				result.add(quoteTreeItem);
				prevLevel1 = thisLevel1;
				prevLevel2 = thisLevel2;
			}
			return result;
		}

		return newArrayList();
	}

	/*
    **trip event template methods
     */
	@Override
	public List<TripEventTemplate> retrieveTripEventTemplateListByType(String tripType) throws Exception {
		SecureUser currentUser = get();
		return tripEventTemplateMapper.selectTripEventTemplateListByType(tripType, "", currentUser.getReferLanguage());
	}

	@Override
	public boolean checkDuplicateEventTemplateByTypeAndName(String tripType, String name) throws Exception {
		SecureUser currentUser = get();
		List<TripEventTemplate> list = tripEventTemplateMapper.selectTripEventTemplateListByType(tripType, name, currentUser.getReferLanguage());
		if (list != null && list.size() > 0)
			return true;
		else
			return false;
	}

	@Override
	public List<TripEventTemplate> retrieveTripEventTemplatesForTripTemplate(String tripType) throws Exception {
		return tripEventTemplateMapper.selectTripEventTemplatesForTripTemplate(tripType);
	}

	@Override
	public List<TripEventTemplate> retrieveTripEventTemplates(String tripType, String name) throws Exception {
		return tripEventTemplateMapper.selectTripEventTemplates(tripType, name);
	}

	@Override
	public TripEventTemplate retrieveTripEventTemplateById(Integer id) throws Exception {
		of(id);
		TripEventTemplate tripEventTemplate = tripEventTemplateMapper.selectTripEventTemplateById(id);

		List<TripEventTemplateCost> templateCost = tripEventTemplateCostMapper.selectEventTemplateCostByTemplateId(id);
		tripEventTemplate.setTemplateCost(templateCost);

		List<TripEventTemplateNotify> templateNotify = tripEventTemplateNotifyMapper.selectEventTemplateNotifyByTemplateId(id);
		tripEventTemplate.setTemplateNotify(templateNotify);

		return tripEventTemplate;
	}

	@Override
	public List<Map<String, Object>> retrieveEventTemplateTree(List<TripEvent> findAll) {
		List<Map<String, Object>> result = newArrayList();
		String prevLevel1 = "";
		String prevLevel2 = "";
		String thisLevel1;
		String thisLevel2;
		int pid = 0;
		int cpid = 0;
		int categorySeq = 0;
		int itemSeq = 0;

		if (isNotEmpty(findAll)) {
			for (TripEvent quoteTemplate : findAll) {
				thisLevel1 = quoteTemplate.getName();
				Map<String, Object> quoteTreeLevel1 = newHashMap();
				quoteTreeLevel1.put("id", quoteTemplate.getId());
				quoteTreeLevel1.put("pId", 0);
				quoteTreeLevel1.put("name", quoteTemplate.getName());
				quoteTreeLevel1.put("t", quoteTemplate.getName());
				quoteTreeLevel1.put("entityId", quoteTemplate.getId());
				quoteTreeLevel1.put("open", "true");
				quoteTreeLevel1.put("type", "event");
				quoteTreeLevel1.put("TripType", quoteTemplate.getType());
				quoteTreeLevel1.put("EventName", quoteTemplate.getName());
				quoteTreeLevel1.put("Category", quoteTemplate.getCategory());
				if (!prevLevel1.equals(thisLevel1)) {
					categorySeq = 0;
					itemSeq = 0;
					result.add(quoteTreeLevel1);
					pid = quoteTemplate.getId();
				}
				thisLevel2 = quoteTemplate.getCategory();
				Map<String, Object> quoteTreeData = newHashMap();
				quoteTreeData.put("id", quoteTemplate.getId() + 1000000);
				quoteTreeData.put("pId", pid);
				quoteTreeData.put("name", quoteTemplate.getCategory());
				quoteTreeData.put("t", quoteTemplate.getCategory());
				quoteTreeData.put("open", "true");
				quoteTreeData.put("entityId", quoteTemplate.getId());
				quoteTreeData.put("type", "category");
				quoteTreeData.put("TripType", quoteTemplate.getType());
				quoteTreeData.put("EventName", quoteTemplate.getName());
				quoteTreeData.put("Category", quoteTemplate.getCategory());
				if (!prevLevel2.equals(thisLevel2) || !prevLevel1.equals(thisLevel1)) {
					itemSeq = 0;
					categorySeq = categorySeq + 1;
					quoteTreeData.put("categorySeq", categorySeq);
					result.add(quoteTreeData);
					cpid = quoteTemplate.getId() + 1000000;
				}
				Map<String, Object> quoteTreeItem = newHashMap();
				quoteTreeItem.put("id", quoteTemplate.getId() + 100000000);
				quoteTreeItem.put("pId", cpid);
				quoteTreeItem.put("name", quoteTemplate.getItem());
				quoteTreeItem.put("t", quoteTemplate.getItem());
				quoteTreeItem.put("open", "true");
				quoteTreeItem.put("entityId", quoteTemplate.getId());
				quoteTreeItem.put("type", "item");
				quoteTreeItem.put("TripType", quoteTemplate.getType());
				quoteTreeItem.put("EventName", quoteTemplate.getName());
				quoteTreeItem.put("Category", quoteTemplate.getCategory());
				itemSeq = itemSeq + 1;
				quoteTreeItem.put("itemSeq", itemSeq);
				result.add(quoteTreeItem);
				prevLevel1 = thisLevel1;
				prevLevel2 = thisLevel2;
			}
			return result;
		}

		return newArrayList();
	}

	@Override
	public void addTripEventTemplate(TripEventTemplate tripEventTemplate) throws Exception {
		addTagToEventTemplate(tripEventTemplate);
		setEventTemplateSequence(tripEventTemplate);

		if (tripEventTemplate.getLinkedEntity()==null) {
			Integer linkedEntity = 0;
			Integer linkedEntityContact = null;
			List<TripEventTemplateCost> templateCostList = tripEventTemplate.getTemplateCost();
			if (templateCostList != null) {
				for (TripEventTemplateCost templateCost : tripEventTemplate.getTemplateCost()) {
					if (templateCost.getLinkedEntity() != null){
						linkedEntity = templateCost.getLinkedEntity();
						linkedEntityContact = templateCost.getLinkedEntityContact();
					}
				}
			}
			if (linkedEntity == null) {
				tripEventTemplate.setLinkedEntity(linkedEntity);
				tripEventTemplate.setLinkedEntityContact(linkedEntityContact);
			}
		}

		tripEventTemplateMapper.insertTripEventTemplate(tripEventTemplate);

		//save Template Cost
		insertEventTemplateCost(tripEventTemplate);

		insertEventTemplateNotify(tripEventTemplate);

		if (tripEventTemplate.getDuplicateEmailForAllEvent().equals("Y"))
		{
			tripEventTemplateNotifyMapper.deleteAllOtherEventTemplateNotifyForCopy(tripEventTemplate.getId());
			tripEventTemplateNotifyMapper.copyEventTemplateNotifyByTemplateId(tripEventTemplate.getId());
		}
	}

	private TripEventTemplate setEventTemplateSequence(TripEventTemplate tripEventTemplate){
		boolean isNewCategory = false;
		Integer id = 0;
		if (tripEventTemplate.getId()==null) {
			isNewCategory = true;
		} else {
			id = tripEventTemplate.getId();
			TripEventTemplate temp = tripEventTemplateMapper.selectTripEventTemplateById(id);
			if (temp != null) {
				if (!temp.getCategory().equals(tripEventTemplate.getCategory())) {
					isNewCategory = true;
				}
			}
		}

		if (isNewCategory){
			Integer itemSeq = 0;
			Integer categorySeq = tripEventTemplateMapper.getCategorySequenceByCategory(tripEventTemplate.getName(), tripEventTemplate.getCategory());
			if (categorySeq == null) {
				categorySeq = tripEventTemplateMapper.getLastCategorySequenceByName(tripEventTemplate.getName(), id);
				if (categorySeq == null) {
					categorySeq = 1;
				} else {
					categorySeq = categorySeq + 1;
				}
				itemSeq = 1;
			} else {
				itemSeq = tripEventTemplateMapper.getItemSequenceByCategory(tripEventTemplate.getName(), tripEventTemplate.getCategory());
				if (itemSeq == null) {
					itemSeq = 1;
				} else {
					itemSeq = itemSeq + 1;
				}
			}
			tripEventTemplate.setCategorySequence(categorySeq);
			tripEventTemplate.setSequence(itemSeq);
		}

		return tripEventTemplate;
	}

    @Override
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void removeEventTemplateByCategory(String tripType, String name) throws Exception {
		tripEventTemplateMapper.deleteTripEventTemplateByCategory(tripType, name);
		tripMapper.deleteTemplateEventByCategory(tripType, name);
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void removeTripEventTemplate(Integer id) throws Exception {
		of(id);
		tripEventTemplateMapper.deleteTripEventTemplate(id);
		tripMapper.deleteTemplateEvent(id);
	}

	@Override
	public void updateTripEventTemplate(TripEventTemplate tripEventTemplate) throws Exception {
		//Clean Template Cost
		tripEventTemplateCostMapper.deleteEventTemplateCostByTemplateId(tripEventTemplate.getId());
		//save Template Cost
		insertEventTemplateCost(tripEventTemplate);

		if (tripEventTemplate.getLinkedEntity()==null) {
			Integer linkedEntity = 0;
			Integer linkedEntityContact = null;
			List<TripEventTemplateCost> templateCostList = tripEventTemplate.getTemplateCost();
			if (templateCostList != null) {
				for (TripEventTemplateCost templateCost : tripEventTemplate.getTemplateCost()) {
					if (templateCost.getLinkedEntity() != null){
						linkedEntity = templateCost.getLinkedEntity();
						linkedEntityContact = templateCost.getLinkedEntityContact();
					}
				}
			}
			if (linkedEntity != null) {
				tripEventTemplate.setLinkedEntity(linkedEntity);
				tripEventTemplate.setLinkedEntityContact(linkedEntityContact);
			}
		}

		//Clean Template Notify
		tripEventTemplateNotifyMapper.deleteEventTemplateNotifyByTemplateId(tripEventTemplate.getId());
		//save Template Notify
		insertEventTemplateNotify(tripEventTemplate);

		if (tripEventTemplate.getDuplicateEmailForAllEvent().equals("Y"))
		{
			tripEventTemplateNotifyMapper.deleteAllOtherEventTemplateNotifyForCopy(tripEventTemplate.getId());
			tripEventTemplateNotifyMapper.copyEventTemplateNotifyByTemplateId(tripEventTemplate.getId());
		}
		addTagToEventTemplate(tripEventTemplate);
		setEventTemplateSequence(tripEventTemplate);
        tripEventTemplateMapper.updateTripEventTemplate(tripEventTemplate);
	}

	private void insertEventTemplateCost(TripEventTemplate tripEventTemplate){
		//save Template Cost
		List<TripEventTemplateCost> templateCostList = tripEventTemplate.getTemplateCost() ;
		if (templateCostList!=null) {
			for (TripEventTemplateCost templateCost : tripEventTemplate.getTemplateCost()) {
				templateCost.setTemplateId(tripEventTemplate.getId());
				//templateCost.setLinkedEntity(tripEventTemplate.getLinkedEntity());
				//templateCost.setLinkedEntityContact(tripEventTemplate.getLinkedEntityContact());
				addTagToEventTemplateCost(templateCost);
				tripEventTemplateCostMapper.insertEventTemplateCost(templateCost);
			}
		}
	}

	private void insertEventTemplateNotify(TripEventTemplate tripEventTemplate){
		//save Template Notify
		List<TripEventTemplateNotify> templateNotifyList = tripEventTemplate.getTemplateNotify() ;
		if (templateNotifyList!=null) {
			for (TripEventTemplateNotify templateNotify : tripEventTemplate.getTemplateNotify()) {
				templateNotify.setTemplateId(tripEventTemplate.getId());
				addTagToEventTemplateNotify(templateNotify);
				tripEventTemplateNotifyMapper.insertEventTemplateNotify(templateNotify);
			}
		}
	}

	@Override
	public void updateTripEventTemplateSequence(Integer id, Integer categorySequence, Integer sequence) throws Exception {
		TripEventTemplate tripEventTemplate = new TripEventTemplate();
		tripEventTemplate.setId(id);
		tripEventTemplate.setSequence(sequence);

		SecureUser currentUser = get();
		tripEventTemplate.setUpdatedBy(currentUser.getUid());
		tripEventTemplate.setCurrentCompany(currentUser.getCompany());
		tripEventTemplate.setCurrentCustomer(currentUser.getCompany());
		if (categorySequence >= 1){
			tripEventTemplate.setCategorySequence(categorySequence);
			tripEventTemplateMapper.updateTripEventTemplateCategorySequence(tripEventTemplate);
		}
		else{
			tripEventTemplateMapper.updateTripEventTemplateSequence(tripEventTemplate);
		}
	}

	private void addTagToEventTemplate(TripEventTemplate tripEventTemplate) {
		SecureUser currentUser = get();
		tripEventTemplate.setUpdatedBy(currentUser.getUid());
		tripEventTemplate.setCurrentCompany(currentUser.getCompany());
		tripEventTemplate.setCurrentCustomer(currentUser.getCompany());
	}

	private void addTagToEventTemplateCost(TripEventTemplateCost templateCost) {
		SecureUser currentUser = get();
		templateCost.setUpdatedBy(currentUser.getUid());
		templateCost.setCurrentCompany(currentUser.getCompany());
		templateCost.setCurrentCustomer(currentUser.getCompany());
	}

	private void addTagToEventTemplateNotify(TripEventTemplateNotify templateNotify) {
		SecureUser currentUser = get();
		templateNotify.setUpdatedBy(currentUser.getUid());
		templateNotify.setCurrentCompany(currentUser.getCompany());
		templateNotify.setCurrentCustomer(currentUser.getCompany());
	}

	/*
    **Role methods
     */
	@Override
	public List<Role> retrieveRoles(String advanceSearch) throws Exception {
		//of(advanceSearch);
		return roleMapper.selectRole(ApplicationSession.get().getReferLanguage());
	}

	@Override
	public Role retrieveRoleById(Integer roleId) throws Exception {
		of(roleId);
		Role role = roleMapper.selectRoleById(roleId);
		List<RoleAccessRight> roleAccessRight = roleMapper.selectRoleAccessRight(roleId);
		if (null != roleAccessRight) {
			role.setRoleAccessRights(roleAccessRight);
		}
		return role;
	}

	@Override
	public List<RoleAccessRight> retrieveRoleAccessRights(Integer id) throws Exception {
		return roleMapper.selectRoleAccessRight(id);
	}

	@Override
	public void addRole(Role role) throws Exception {
		of(role);
		of(role.getName());
		addTagToRole(role);
        roleMapper.insertRole(role);
        recordRoleAccess(role);
    }

    private void recordRoleAccess(Role role) {
        if (role.getAccessRightIds() != null && role.getAccessRightIds() != "") {
            for (String id : role.accessRightIds.split(",")) {
                if (id != null && parseInt(id) > 0) {
                    RoleAccessRight temp = new RoleAccessRight();
                    temp.setRoleId(role.getId());
                    temp.setAccessRightId(parseInt(id));
                    roleMapper.insertRoleAccessRight(temp);
                }
            }
        }
    }

	@Override
	public void removeRole(Integer dpId) throws Exception {
		of(dpId);
		roleMapper.deleteRole(dpId);
	}

	@Override
	public void updateRole(Role role) throws Exception {
		of(role);
		of(role.getId());
		addTagToRole(role);
		roleMapper.deleteRoleAccessRightByRoleId(role.getId());

        recordRoleAccess(role);
        roleMapper.updateRole(role);
	}

	private void addTagToRole(Role role) {
		SecureUser currentUser = get();
		role.setUpdatedBy(currentUser.getUid());
		role.setCurrentCompany(currentUser.getCompany());
		role.setCurrentCustomer(currentUser.getCompany());
	}
}
