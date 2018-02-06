package ca.manitoulin.mtd.dto.customer;

import ca.manitoulin.mtd.dto.AbstractDTO;

import java.util.Date;

public class Courier extends AbstractDTO {
	private Integer id;
	private Integer entityId;
	private String company;
	private String airLineInfo;
	private String contactName;
	private String locationName;
	private String locationCode;
	private String address;
	private String city;
	private String country;
	private String note;
	private String onlineStatus;
	private String onlineStatusDesc;
	private String onlineCountry;
	private String courierAirport;
	private String courierCity;
	private String visaCountry;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getAirLineInfo() {
		return airLineInfo;
	}

	public void setAirLineInfo(String airLineInfo) {
		this.airLineInfo = airLineInfo;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public String getOnlineStatusDesc() {
		return onlineStatusDesc;
	}

	public void setOnlineStatusDesc(String onlineStatusDesc) {
		this.onlineStatusDesc = onlineStatusDesc;
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

	public String getVisaCountry() {
		return visaCountry;
	}

	public void setVisaCountry(String visaCountry) {
		this.visaCountry = visaCountry;
	}
}
