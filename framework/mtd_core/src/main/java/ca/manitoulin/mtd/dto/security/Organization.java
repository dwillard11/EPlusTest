package ca.manitoulin.mtd.dto.security;

import ca.manitoulin.mtd.dto.AbstractDTO;

/**
 * Organization POJO point to PMTCOMP
 * @author Bob Yu
 *
 */
public class Organization extends AbstractDTO {
	private static final long serialVersionUID = -4338597949336366690L;
    private String code;
    private String name; //dp_name
    private String shortName; //dp_shortname

    private Integer id;
    private String status;
    private String city;
    private String address1;
    private String address2;
    private String postalCode;
    private String province;
    private String country;
    private String phoneMobile;
    private String phoneOffice;
    private String phoneFax;
    private String phone800;
    private String notes;
    private Integer invoiceOfficeSeq;
    private String invoiceOfficeName;
    private String invoiceOfficeLine1;
    private String invoiceOfficeLine2;
    private String invoiceOfficeLine3;
    private String defaultCurrency;
    private String defaultTimezone;
    private String defaultEmail;
    private String mailHost;
    private String mailUserName;
    private String mailPassword;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneMobile() {
        return phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
    }

    public String getPhoneOffice() {
        return phoneOffice;
    }

    public void setPhoneOffice(String phoneOffice) {
        this.phoneOffice = phoneOffice;
    }

    public String getPhoneFax() {
        return phoneFax;
    }

    public void setPhoneFax(String phoneFax) {
        this.phoneFax = phoneFax;
    }

    public String getPhone800() {
        return phone800;
    }

    public void setPhone800(String phone800) {
        this.phone800 = phone800;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getInvoiceOfficeSeq() {
        return invoiceOfficeSeq;
    }

    public void setInvoiceOfficeSeq(Integer invoiceOfficeSeq) {
        this.invoiceOfficeSeq = invoiceOfficeSeq;
    }

    public String getInvoiceOfficeName() {
        return invoiceOfficeName;
    }

    public void setInvoiceOfficeName(String invoiceOfficeName) {
        this.invoiceOfficeName = invoiceOfficeName;
    }

    public String getInvoiceOfficeLine1() {
        return invoiceOfficeLine1;
    }

    public void setInvoiceOfficeLine1(String invoiceOfficeLine1) {
        this.invoiceOfficeLine1 = invoiceOfficeLine1;
    }

    public String getInvoiceOfficeLine2() {
        return invoiceOfficeLine2;
    }

    public void setInvoiceOfficeLine2(String invoiceOfficeLine2) {
        this.invoiceOfficeLine2 = invoiceOfficeLine2;
    }

    public String getInvoiceOfficeLine3() {
        return invoiceOfficeLine3;
    }

    public void setInvoiceOfficeLine3(String invoiceOfficeLine3) {
        this.invoiceOfficeLine3 = invoiceOfficeLine3;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public String getDefaultTimezone() {
        return defaultTimezone;
    }

    public void setDefaultTimezone(String defaultTimezone) {
        this.defaultTimezone = defaultTimezone;
    }

    public String getDefaultEmail() {
        return defaultEmail;
    }

    public void setDefaultEmail(String defaultEmail) {
        this.defaultEmail = defaultEmail;
    }

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public String getMailUserName() {
        return mailUserName;
    }

    public void setMailUserName(String mailUserName) {
        this.mailUserName = mailUserName;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

}
