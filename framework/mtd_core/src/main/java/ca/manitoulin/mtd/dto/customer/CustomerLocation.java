package ca.manitoulin.mtd.dto.customer;

import ca.manitoulin.mtd.dto.AbstractDTO;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomerLocation extends AbstractDTO {
	private Integer id;
	private Integer customerId;
	private String customerName;
	private String code;
	private String quickName;
	private String status;
	private String address1;
	private String address2;
	private String city;
	private String stateProvince;
	private String postalCode;
	private String country;
	private String busPhone1;
	private String busPhone2;
	private String afterHoursPhone;
	private String fax;
	private String email;
	private String openHours;
	private Integer locationUsage;
	private String mapUrl;
	private Integer billingCompany;
	private Integer brokerUs;
	private Integer brokerCa;
	private Integer altBrokerUs;
	private Integer altBrokerCa;
	private Integer brokerNat;
	private Integer agentId;
	private String notes;
	private String known;
	private Integer knownStatusIndex;
	private String tsa;
	private String tsaAirport;
	private String tsaContact;
	private String csa;
    private Customer company;
	private String keyId;
	private Integer contactId;
	private String contactName;

    private List<CustomerContact> contacts;

    public CustomerLocation() {
        company = new Customer();
        contacts = new ArrayList<>();
    }

	public String getGenericInfo(){
        Joiner joiner = Joiner.on(", ").skipNulls();
        if (StringUtils.isEmpty(contactName))
        	return joiner.join(quickName, (StringUtils.isEmpty(address1) ? address2 : address1), city, (StringUtils.isEmpty(busPhone1) ? busPhone2 : busPhone1));
        else
			return joiner.join(quickName, (StringUtils.isEmpty(address1) ? address2 : address1), city, contactName, (StringUtils.isEmpty(busPhone1) ? busPhone2 : busPhone1));
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getQuickName() {
		return quickName;
	}

	public void setQuickName(String quickName) {
		this.quickName = quickName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStateProvince() {
		return stateProvince;
	}

	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBusPhone1() {
		return busPhone1;
	}

	public void setBusPhone1(String busPhone1) {
		this.busPhone1 = busPhone1;
	}

	public String getBusPhone2() {
		return busPhone2;
	}

	public void setBusPhone2(String busPhone2) {
		this.busPhone2 = busPhone2;
	}

	public String getAfterHoursPhone() {
		return afterHoursPhone;
	}

	public void setAfterHoursPhone(String afterHoursPhone) {
		this.afterHoursPhone = afterHoursPhone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOpenHours() {
		return openHours;
	}

	public void setOpenHours(String openHours) {
		this.openHours = openHours;
	}

	public Integer getLocationUsage() {
		return locationUsage;
	}

	public void setLocationUsage(Integer locationUsage) {
		this.locationUsage = locationUsage;
	}

	public String getMapUrl() {
		return mapUrl;
	}

	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}

	public Integer getBillingCompany() {
		return billingCompany;
	}

	public void setBillingCompany(Integer billingCompany) {
		this.billingCompany = billingCompany;
	}

	public Integer getBrokerUs() {
		return brokerUs;
	}

	public void setBrokerUs(Integer brokerUs) {
		this.brokerUs = brokerUs;
	}

	public Integer getBrokerCa() {
		return brokerCa;
	}

	public void setBrokerCa(Integer brokerCa) {
		this.brokerCa = brokerCa;
	}

	public Integer getAltBrokerUs() {
		return altBrokerUs;
	}

	public void setAltBrokerUs(Integer altBrokerUs) {
		this.altBrokerUs = altBrokerUs;
	}

	public Integer getAltBrokerCa() {
		return altBrokerCa;
	}

	public void setAltBrokerCa(Integer altBrokerCa) {
		this.altBrokerCa = altBrokerCa;
	}

	public Integer getBrokerNat() {
		return brokerNat;
	}

	public void setBrokerNat(Integer brokerNat) {
		this.brokerNat = brokerNat;
	}

	public Integer getAgentId() {
		return agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getKnown() {
		return known;
	}

	public void setKnown(String known) {
		this.known = known;
	}

	public Integer getKnownStatusIndex() {
		return knownStatusIndex;
	}

	public void setKnownStatusIndex(Integer knownStatusIndex) {
		this.knownStatusIndex = knownStatusIndex;
	}

	public String getTsa() {
		return tsa;
	}

	public void setTsa(String tsa) {
		this.tsa = tsa;
	}

	public String getTsaAirport() {
		return tsaAirport;
	}

	public void setTsaAirport(String tsaAirport) {
		this.tsaAirport = tsaAirport;
	}

	public String getTsaContact() {
		return tsaContact;
	}

	public void setTsaContact(String tsaContact) {
		this.tsaContact = tsaContact;
	}

	public String getCsa() {
		return csa;
	}

	public void setCsa(String csa) {
		this.csa = csa;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
}
