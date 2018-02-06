package ca.manitoulin.mtd.dto.massagecentral;

import static ca.manitoulin.mtd.util.DateUtil.LONG_DATE_FORMAT;
import static ca.manitoulin.mtd.util.DateUtil.TIME_FORMAT;
import static ca.manitoulin.mtd.util.DateUtil.toDateString;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.manitoulin.mtd.dto.AbstractDTO;
import ca.manitoulin.mtd.util.DateUtil;

public class MessageSubscription extends AbstractDTO {


	private static final long serialVersionUID = -8523387906240612092L;
	private String id;
	private String probillNo; //also Nofitication for
	private Date requestedDate;
	private Date requestedTime;
	private Date expiryDate;
	private String email;
	private String activityDescription;
	private String activityCode;//DLVY/RSC
	private String messageCentral; //Y/N
	private String notification; // M/E/B
	
	private List<String> trigger; //DLVY/RSC
	
	/**
	 * Prefix "0" to probill number
	 * @return
	 */
	public String getFormattedProbillNumber(){
		if (!"All Probills".equals(probillNo)) {
			return StringUtils.leftPad(probillNo, 10, "0");
		} else {
			return probillNo;
		}
		
	}
	
	public Integer getExpiryDateInInteger(){
		if(expiryDate == null) return 0;
		return Integer.parseInt(DateUtil.toDateString(expiryDate, DateUtil.DB_DATE_FORMAT));
	}


	public String getFormattedExpiryDate() {
		if(expiryDate== null)
			return "No Expiry";
		String s = toDateString(expiryDate, LONG_DATE_FORMAT);
		
		return s;
	}

	public String getFormattedRequestDate() {
		return toDateString(requestedDate, LONG_DATE_FORMAT);
	}

	public String getFormattedRequestTime() {
		return toDateString(requestedTime, TIME_FORMAT);

	}
	public String getMessageCentral() {
		return messageCentral;
	}

	public void setMessageCentral(String messageCentral) {
		this.messageCentral = messageCentral;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProbillNo() {
		return probillNo;
	}

	public void setProbillNo(String probillNo) {
		this.probillNo = probillNo;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public List<String> getTrigger() {
		return trigger;
	}

	public void setTrigger(List<String> trigger) {
		this.trigger = trigger;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getRequestedDate() {
		return requestedDate;
	}

	public void setRequestedDate(Date requestedDate) {
		this.requestedDate = requestedDate;
	}

	public Date getRequestedTime() {
		return requestedTime;
	}

	public void setRequestedTime(Date requestedTime) {
		this.requestedTime = requestedTime;
	}

	public String getActivityDescription() {
		return activityDescription;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}



}
