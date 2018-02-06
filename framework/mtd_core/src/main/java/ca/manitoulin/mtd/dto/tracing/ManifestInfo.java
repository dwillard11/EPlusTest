package ca.manitoulin.mtd.dto.tracing;

import java.util.Date;

import ca.manitoulin.mtd.dto.AbstractDTO;
import ca.manitoulin.mtd.util.DateUtil;

public class ManifestInfo extends AbstractDTO {
	
	private String number;
	private String type;
	private String fromCity;
	private String fromCityCode;
	private String fromDate;
	private String fromTime;
	private String toCity;
	private String toCityCode;
	

	private String toDate;
	private String toTime;
	private String status;
	
	private String dispathId;
	private String lineId;
	private String deliveryField;
	private String statusTooltip;
	private String deliveryDate;
	private String deliveryTime;
	private String receivedBy;
	private String deliveryStatus;
	
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		ManifestInfo otherone = (ManifestInfo) obj;
		return this.toString().equals(otherone.toString());
	}

	public String getFormattedDeliveryDate() {
		Date date = DateUtil.toDate(deliveryDate, DateUtil.DB_DATE_FORMAT_MMDDYYYY);
		return DateUtil.toDateString(date, DateUtil.LONG_DATE_FORMAT);

	}

	public String getFormattedDeliveryTime() {
		return DateUtil.displayDBTime(deliveryTime);
	}
	
	public String getFromDayOfWeek(){
		Date date = DateUtil.toDate(fromDate, DateUtil.DB_DATE_FORMAT_MMDDYYYY);
		return DateUtil.toDayOfWeek(date);
		
	}
	public String getToDayOfWeek(){
		Date date = DateUtil.toDate(toDate, DateUtil.DB_DATE_FORMAT_MMDDYYYY);
		return DateUtil.toDayOfWeek(date);
		
	}	
	
	
	public String getFormattedFromDate() {
		Date date = DateUtil.toDate(fromDate, DateUtil.DB_DATE_FORMAT_MMDDYYYY);
		return DateUtil.toDateString(date, DateUtil.LONG_DATE_FORMAT);

	}

	public String getFormattedFromTime() {
		return DateUtil.displayDBTime(fromTime);
	}
	
	public String getFormattedToDate() {
		Date date = DateUtil.toDate(toDate, DateUtil.DB_DATE_FORMAT_MMDDYYYY);
		return DateUtil.toDateString(date, DateUtil.LONG_DATE_FORMAT);

	}

	public String getFormattedToTime() {
		return DateUtil.displayDBTime(toTime);
	}
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFromCity() {
		return fromCity;
	}
	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}
	
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getFromTime() {
		return fromTime;
	}
	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}
	public String getToCity() {
		return toCity;
	}
	public void setToCity(String toCity) {
		this.toCity = toCity;
	}
	
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getToTime() {
		return toTime;
	}
	public void setToTime(String toTime) {
		this.toTime = toTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getDispathId() {
		return dispathId;
	}
	public void setDispathId(String dispathId) {
		this.dispathId = dispathId;
	}
	public String getLineId() {
		return lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	public String getDeliveryField() {
		return deliveryField;
	}
	public void setDeliveryField(String deliveryField) {
		this.deliveryField = deliveryField;
	}
	public String getStatusTooltip() {
		return statusTooltip;
	}
	public void setStatusTooltip(String statusTooltip) {
		this.statusTooltip = statusTooltip;
	}
	

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getReceivedBy() {
		return receivedBy;
	}

	public void setReceivedBy(String receivedBy) {
		this.receivedBy = receivedBy;
	}


	
	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public String getFromCityCode() {
		return fromCityCode;
	}

	public void setFromCityCode(String fromCityCode) {
		this.fromCityCode = fromCityCode;
	}

	public String getToCityCode() {
		return toCityCode;
	}

	public void setToCityCode(String toCityCode) {
		this.toCityCode = toCityCode;
	}

	@Override
	public String toString() {
		return "ManifestInfo [number=" + number + ", type=" + type + ", fromCityCode=" + fromCityCode + ", fromDate="
				+ fromDate + ", fromTime=" + fromTime + ", toCityCode=" + toCityCode + ", toDate=" + toDate
				+ ", toTime=" + toTime  + "]";
	}

}
