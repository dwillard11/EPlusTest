package ca.manitoulin.mtd.dto.dashboard;

import ca.manitoulin.mtd.dto.AbstractDTO;

import javax.xml.crypto.Data;
import java.math.BigDecimal;

public class ShipmentSummaryGrid extends AbstractDTO {
	private Integer groupId;
	private String groupDesc;
	private String currency;
	private Integer countShipments;
	private Integer totalShipments;

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getCountShipments() {
		return countShipments;
	}

	public void setCountShipments(Integer countShipments) {
		this.countShipments = countShipments;
	}

	public Integer getTotalShipments() {
		return totalShipments;
	}

	public void setTotalShipments(Integer totalShipments) {
		this.totalShipments = totalShipments;
	}
}
