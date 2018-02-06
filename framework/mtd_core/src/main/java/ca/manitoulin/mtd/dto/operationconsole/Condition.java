package ca.manitoulin.mtd.dto.operationconsole;

import ca.manitoulin.mtd.dto.maintenance.QuoteTemplate;

public class Condition extends QuoteTemplate {
	
	private Integer tripId;

	public Integer getTripId() {
		return tripId;
	}

	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}

}
