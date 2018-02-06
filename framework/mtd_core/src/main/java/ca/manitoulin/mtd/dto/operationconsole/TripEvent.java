package ca.manitoulin.mtd.dto.operationconsole;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ca.manitoulin.mtd.dto.maintenance.TripEventTemplate;
import ca.manitoulin.mtd.util.DateUtil;

public class TripEvent extends TripEventTemplate {
	
	private Integer tripId;
	private String tripType;
	private Integer eventTemplateId;
	
	private Date estimatedDate;
	private Date actualDate;
	private Integer markedComplete; 
	private String setup;
	private String link;
	private BigDecimal totalCosts;
	
	private String podName;
	private Date podDate;
	private String contactName;

	List<TripCost> costs;
	List<TripEventNotify> notifies;
	
	public String getActualDateStr(){
		return DateUtil.toDateString(this.actualDate, DateUtil.DATE_TIME_FARMAT_YYYYMMDDHHMM);
	}
	
	public Integer getTripId() {
		return tripId;
	}
	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}
	public Integer getEventTemplateId() {
		return eventTemplateId;
	}
	public void setEventTemplateId(Integer eventTemplateId) {
		this.eventTemplateId = eventTemplateId;
	}
	public Date getEstimatedDate() {
		return estimatedDate;
	}
	public void setEstimatedDate(Date estimatedDate) {
		this.estimatedDate = estimatedDate;
	}
	public Date getActualDate() {
		return actualDate;
	}
	public void setActualDate(Date actualDate) {
		this.actualDate = actualDate;
	}
	public String getSetup() {
		return setup;
	}
	public void setSetup(String setup) {
		this.setup = setup;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public List<TripCost> getCosts() {
		return costs;
	}
	public void setCosts(List<TripCost> costs) {
		this.costs = costs;
	}
	public List<TripEventNotify> getNotifies() {
		return notifies;
	}
	public void setNotifies(List<TripEventNotify> notifies) {
		this.notifies = notifies;
	}
	public String getTripType() {
		return tripType;
	}
	public void setTripType(String tripType) {
		this.tripType = tripType;
	}

	public Integer getMarkedComplete() {
		return markedComplete;
	}

	public void setMarkedComplete(Integer markedComplete) {
		this.markedComplete = markedComplete;
	}

	public BigDecimal getTotalCosts() {
		return totalCosts;
	}

	public void setTotalCosts(BigDecimal totalCosts) {
		this.totalCosts = totalCosts;
	}

	public String getPodName() {
		return podName;
	}

	public void setPodName(String podName) {
		this.podName = podName;
	}

	public Date getPodDate() {
		return podDate;
	}

	public void setPodDate(Date podDate) {
		this.podDate = podDate;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
}
