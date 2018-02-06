package ca.manitoulin.mtd.dto.tracing;

import ca.manitoulin.mtd.dto.AbstractDTO;
import ca.manitoulin.mtd.util.DateUtil;

public class PickupStatusDetail extends AbstractDTO {

	private String refNumber;
	private String statusDate;
	private String statusTime;
	private String action;
	private String comments;
	
	public String getFormattedStatusDate() {
		return DateUtil.displayDBDate(statusDate);
	}
	
	public String getFormattedStatusTime() {
		return DateUtil.displayDBTime(statusTime);

	}
	
	public String getRefNumber() {
		return refNumber;
	}
	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}

	public String getStatusTime() {
		return statusTime;
	}

	public void setStatusTime(String statusTime) {
		this.statusTime = statusTime;
	}
	
	
}
