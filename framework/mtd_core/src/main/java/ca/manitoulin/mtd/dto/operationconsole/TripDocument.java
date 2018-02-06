package ca.manitoulin.mtd.dto.operationconsole;

import ca.manitoulin.mtd.dto.support.Attachment;

public class TripDocument extends Attachment {
	
	private Integer tripId;
	private Long communicationId;
	private String refName;

	public Integer getTripId() {
		return tripId;
	}

	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}

	public Long getCommunicationId() {
		return communicationId;
	}

	public void setCommunicationId(Long communicationId) {
		this.communicationId = communicationId;
	}

	public String getRefName() {
		return refName;
	}

	public void setRefName(String refName) {
		this.refName = refName;
	}

	

}
