package ca.manitoulin.mtd.dto.operationconsole;

import ca.manitoulin.mtd.dto.AbstractDTO;


public class TripNote extends AbstractDTO {

	private Integer id;
	private Integer tripId;
	private String content;
	private String status;
	private boolean canEdit;

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTripId() {
		return tripId;
	}
	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
}
