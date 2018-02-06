package ca.manitoulin.mtd.dto.customer;

import ca.manitoulin.mtd.dto.AbstractDTO;
import com.google.common.base.Joiner;

import java.util.List;

public class CustomerContact extends AbstractDTO {
	private Integer id;
	private Integer locationId;
	private String status;
	private String contactType;
	private String notes;
	private String firstName;
	private String lastName;
	private String address1;
	private String address2;
	private String city;
	private String stateProvince;
	private String postalCode;
	private String country;
	private String phoneOffice;
	private String phoneMobile;
	private String phonePager;
	private String phoneFax;
	private String phoneHome;
	private String email;
	private String mapUrl;
	private String onlineStatus;
	private String onlineCountry;
	private String courierAirport;
	private String courierCity;
	private String currentFlight;
	private String nexus;
	private String companyCreditCard;
	private String tsaPreClearance;
	private String globalEntry;

	private Customer company;
	private CustomerLocation division;
	
	private String companyName;
	private String divisionAddress;
	private String divisionCity;

	private List<CourierVisa> courierVisa;

	public CustomerContact(){
		company = new Customer();
		division = new CustomerLocation();
	}
	
    public String getGenericInfo() {
        Joiner joiner1 = Joiner.on(" ").skipNulls();
        Joiner joiner = Joiner.on(",").skipNulls();

        return joiner.join(joiner1.join(firstName, lastName), contactType, email);
    }
	public String getEmailGenericInfo() {
        Joiner joiner = Joiner.on(",").skipNulls();
        return joiner.join(companyName,divisionAddress,divisionCity,firstName, lastName, contactType, email);
	}
	public String getEmailShowInfo() {
		Joiner joiner1 = Joiner.on(" ").skipNulls();
		Joiner joiner = Joiner.on(",").skipNulls();
		return joiner.join(joiner1.join(firstName, lastName),  email);
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public String getPhoneOffice() {
		return phoneOffice;
	}

	public void setPhoneOffice(String phoneOffice) {
		this.phoneOffice = phoneOffice;
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = phoneMobile;
	}

	public String getPhonePager() {
		return phonePager;
	}

	public void setPhonePager(String phonePager) {
		this.phonePager = phonePager;
	}

	public String getPhoneFax() {
		return phoneFax;
	}

	public void setPhoneFax(String phoneFax) {
		this.phoneFax = phoneFax;
	}

	public String getPhoneHome() {
		return phoneHome;
	}

	public void setPhoneHome(String phoneHome) {
		this.phoneHome = phoneHome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMapUrl() {
		return mapUrl;
	}

	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}

	public String getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public String getOnlineCountry() {
		return onlineCountry;
	}

	public void setOnlineCountry(String onlineCountry) {
		this.onlineCountry = onlineCountry;
	}

	public String getCourierAirport() {
		return courierAirport;
	}

	public void setCourierAirport(String courierAirport) {
		this.courierAirport = courierAirport;
	}

	public String getCourierCity() {
		return courierCity;
	}

	public void setCourierCity(String courierCity) {
		this.courierCity = courierCity;
	}

	public String getCurrentFlight() {
		return currentFlight;
	}

	public void setCurrentFlight(String currentFlight) {
		this.currentFlight = currentFlight;
	}

	public String getNexus() {
		return nexus;
	}

	public void setNexus(String nexus) {
		this.nexus = nexus;
	}

	public String getCompanyCreditCard() {
		return companyCreditCard;
	}

	public void setCompanyCreditCard(String companyCreditCard) {
		this.companyCreditCard = companyCreditCard;
	}

	public String getTsaPreClearance() {
		return tsaPreClearance;
	}

	public void setTsaPreClearance(String tsaPreClearance) {
		this.tsaPreClearance = tsaPreClearance;
	}

	public String getGlobalEntry() {
		return globalEntry;
	}

	public void setGlobalEntry(String globalEntry) {
		this.globalEntry = globalEntry;
	}

	public List<CourierVisa> getCourierVisa() {
		return courierVisa;
	}

	public void setCourierVisa(List<CourierVisa> courierVisa) {
		this.courierVisa = courierVisa;
	}

	public Customer getCompany() {
		return company;
	}

	public void setCompany(Customer company) {
		this.company = company;
	}

	public CustomerLocation getDivision() {
		return division;
	}

	public void setDivision(CustomerLocation division) {
		this.division = division;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDivisionAddress() {
		return divisionAddress;
	}

	public void setDivisionAddress(String divisionAddress) {
		this.divisionAddress = divisionAddress;
	}

	public String getDivisionCity() {
		return divisionCity;
	}

	public void setDivisionCity(String divisionCity) {
		this.divisionCity = divisionCity;
	}
}
