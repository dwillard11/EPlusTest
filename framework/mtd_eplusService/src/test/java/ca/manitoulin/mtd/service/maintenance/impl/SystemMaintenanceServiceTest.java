package ca.manitoulin.mtd.service.maintenance.impl;

import ca.manitoulin.mtd.dto.maintenance.TripEventTemplate;
import ca.manitoulin.mtd.dto.security.Organization;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.support.AppEnum;
import ca.manitoulin.mtd.service.maintenance.ISystemMaintenanceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;


/**
 * Created by zhuiz on 2017/2/19.
 * SystemMaintenanceService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-ws.xml"})
public class SystemMaintenanceServiceTest {

    //static private Logger log = getLogger(SystemMaintenanceServiceTest.class);
    @Autowired
    private ISystemMaintenanceService systemMaintenanceService;

    @Test
    public void retrieveSecureUserByID() throws Exception {
        SecureUser secureUser = systemMaintenanceService.retrieveSecureUserByID(1);
        notNull(secureUser);
    }

    @Test
    public void retrieveSecureUsers(String userId) throws Exception {
        systemMaintenanceService.retrieveSecureUsers(userId);
    }


    @Test
    public void addSecureUser() throws Exception {
        SecureUser secureUser = new SecureUser();
        secureUser.setDepId(2);
        secureUser.setCompany("ePlus Corporation");
        secureUser.setCustomer("customerT");
        secureUser.setUid("Cheery");
        secureUser.setStatus("Active");
        secureUser.setDefaultCurrency("USD");
        secureUser.setUpdatedBy("John");
        secureUser.setCurrentCompany("current Company");
        secureUser.setCurrentCustomer("Current Customer");
        systemMaintenanceService.addSecureUser(secureUser);
    }

    @Test
    public void removeSecureUser() throws Exception {
        systemMaintenanceService.removeSecureUser(4);
    }

    @Test
    public void updateSecureUser() throws Exception {
        SecureUser secureUser = new SecureUser();
        secureUser.setId("1");
        secureUser.setDepId(2);
        secureUser.setCompany("Test Corporation");
        secureUser.setCustomer("Cherry");
        secureUser.setUid("Penny");
        secureUser.setStatus("Active");
        secureUser.setDefaultCurrency("USD");
        secureUser.setUpdatedBy("John");
        secureUser.setCurrentCompany("current Company");
        secureUser.setCurrentCustomer("Current Customer");
        systemMaintenanceService.updateSecureUser(secureUser);
    }



    @Test
    public void retrieveDepartments() throws Exception {
        List<Organization> list = systemMaintenanceService.retrieveDepartments("abc");
        notEmpty(list);
        notNull(list);
    }

    @Test
    public void retrieveDepartmentById() throws Exception {
        systemMaintenanceService.retrieveDepartmentById(1);
    }


    @Test
    public void addDepartment() throws Exception {
        Organization organization = new Organization();
        organization.setName("CUnitTest");
        organization.setCity("CCity");
        organization.setAddress1("CAddress1,Tan");
        organization.setAddress2("CAddress2,Tan");
        organization.setProvince("California");
        organization.setCountry("US");
        organization.setPhoneMobile("666666");
        organization.setPhoneOffice("66666");
        organization.setPhoneFax("000000");
        organization.setPhone800("400-800-666");
        organization.setNotes("666:well done!");
        organization.setInvoiceOfficeSeq(66666);
        organization.setInvoiceOfficeName("California U.S.");
        organization.setInvoiceOfficeLine1("Office line1.");
        organization.setInvoiceOfficeLine2("Office line2.");
        organization.setInvoiceOfficeLine3("Office line3");
        organization.setDefaultCurrency("CNY");
        systemMaintenanceService.addDepartment(organization);
    }

    @Test
    public void removeDepartment() throws Exception {
        systemMaintenanceService.removeDepartment(3);
    }

    @Test
    public void updateDepartment() throws Exception {
        Organization organization = new Organization();
        organization.setId(1);
        organization.setName("TestUpdate");
        organization.setCity("CCity");
        organization.setAddress1("CAddress1,Tan");
        organization.setAddress2("CAddress2,Tan");
        organization.setProvince("California");
        organization.setCountry("US");
        organization.setPhoneMobile("666666");
        organization.setPhoneOffice("66666");
        organization.setPhoneFax("000000");
        organization.setPhone800("400-800-666");
        organization.setNotes("666:well done!");
        organization.setInvoiceOfficeSeq(66666);
        organization.setInvoiceOfficeName("California U.S.");
        organization.setInvoiceOfficeLine1("Office line1.");
        organization.setInvoiceOfficeLine2("Office line2.");
        organization.setInvoiceOfficeLine3("Office line3");
        organization.setDefaultCurrency("CNY");
        systemMaintenanceService.updateDepartment(organization);
    }

    @Test
    public void retrieveBusinessCode() throws Exception {
        List<AppEnum> list = systemMaintenanceService.retrieveBusinessCode("abc");
        notEmpty(list);
        notNull(list);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void retrieveDepartments2() throws Exception {
        List<Organization> list = systemMaintenanceService.retrieveDepartments(null);
        notEmpty(list);
        notNull(list);
    }

    @Test
    public void retrieveCodeCategory() throws Exception {
        systemMaintenanceService.retrieveCodeCategory();
    }


    @Test
    public void addBusinessCode() throws Exception {
        AppEnum appEnum = new AppEnum();
        appEnum.setCategoryCode("abcvvvvvddd");
        appEnum.setStatus("nb");
        appEnum.setSortingOrder(1);
        appEnum.setRemarks("beautiful");
        appEnum.setCode("nice");
        appEnum.setLabel("ground");
        systemMaintenanceService.addBusinessCode(appEnum);
        notNull(appEnum.getId());

    }

    @Test(expected = java.lang.NullPointerException.class)
    public void addBusinessCode2() throws Exception {
        systemMaintenanceService.addBusinessCode(null);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void addBusinessCode3() throws Exception {
        AppEnum appEnum = new AppEnum();
        appEnum.setSortingOrder(null);
        systemMaintenanceService.addBusinessCode(appEnum);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void removeBusinessCode() throws Exception {
        systemMaintenanceService.removeBusinessCode(null);
    }

    @Test
    public void removeBusinessCode2() throws Exception {
        systemMaintenanceService.removeBusinessCode(30);
    }

    @Test
    public void updateBusinessCode() throws Exception {
        AppEnum appEnum = new AppEnum();
        appEnum.setId(36);
        appEnum.setCategoryCode("A new update");
        appEnum.setStatus("222");
        appEnum.setSortingOrder(222);
        appEnum.setRemarks("beautiful");
        appEnum.setCode("nice");
        appEnum.setLabel("ground");
        systemMaintenanceService.updateBusinessCode(appEnum);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void updateBusinessCode2() throws Exception {
        systemMaintenanceService.updateBusinessCode(null);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void updateBusinessCode3() throws Exception {
        AppEnum appEnum = new AppEnum();
        appEnum.setId(null);
        systemMaintenanceService.updateBusinessCode(appEnum);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void updateBusinessCode4() throws Exception {
        AppEnum appEnum = new AppEnum();
        appEnum.setSortingOrder(null);
        systemMaintenanceService.updateBusinessCode(appEnum);
    }

    @Test
    public void retrieveTripEventTemplates(String tripType) throws Exception {
        // systemMaintenanceService.retrieveTripEventTemplates(tripType);
    }

    @Test
    public void retrieveTripEventTemplateById() throws Exception {
        systemMaintenanceService.retrieveDepartmentById(1);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void retrieveTripEventTemplateById2() throws Exception {
        systemMaintenanceService.retrieveDepartmentById(null);
    }

    @Test
    public void addTripEventTemplate() throws Exception {
        TripEventTemplate tripEventTemplate = new TripEventTemplate();
        tripEventTemplate.setType("C test type");
        tripEventTemplate.setName("test name");
        tripEventTemplate.setCategory("airPortC");
        tripEventTemplate.setSequence(1);
        tripEventTemplate.setCode("CC");
        tripEventTemplate.setItem("NO.1 item");
        tripEventTemplate.setDescription("NO.1 description");
        tripEventTemplate.setCost(666.666);
        tripEventTemplate.setStatus("active");
        systemMaintenanceService.addTripEventTemplate(tripEventTemplate);
    }


    @Test
    public void removeTripEventTemplate() throws Exception {
        systemMaintenanceService.removeDepartment(1);
    }

    @Test
    public void updateTripEventTemplate() throws Exception {
        TripEventTemplate tripEventTemplate = new TripEventTemplate();
        tripEventTemplate.setId(16);
        tripEventTemplate.setType("C16 test type");
        tripEventTemplate.setName("Trip name");
        tripEventTemplate.setCategory("airPortC");
        tripEventTemplate.setSequence(16);
        tripEventTemplate.setCode("CC");
        tripEventTemplate.setItem("NO.1 item");
        tripEventTemplate.setDescription("NO.1 description");
        tripEventTemplate.setCost(666.666);
        tripEventTemplate.setStatus("active");
        systemMaintenanceService.updateTripEventTemplate(tripEventTemplate);
    }


}