package ca.manitoulin.mtd.dto.dashboard;

import ca.manitoulin.mtd.dto.AbstractDTO;
import com.sun.org.apache.xpath.internal.operations.Number;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShipmentSummary extends AbstractDTO {
	private Integer countOpenShipments;
	private Integer countDeliveredShipments;
	private Integer countClosedShipments;
	private BigDecimal totalOpenShipments;
	private BigDecimal totalDeliveredShipments;
	private BigDecimal totalClosedShipments;
	private Integer countOpenQuotes;
	private Integer countCancelledQuotes;
	private Integer countApprovedQuotes;
	private BigDecimal totalOpenQuotes;
	private BigDecimal totalCancelledQuotes;
	private BigDecimal totalApprovedQuotes;
	private Data minEstDate;
	private Integer InboxMsg;
	private Integer ProceededMsg;



	public Integer getCountOpenShipments() {
		return countOpenShipments;
	}

	public void setCountOpenShipments(Integer countOpenShipments) {
		this.countOpenShipments = countOpenShipments;
	}

	public Integer getCountDeliveredShipments() {
		return countDeliveredShipments;
	}

	public void setCountDeliveredShipments(Integer countDeliveredShipments) {
		this.countDeliveredShipments = countDeliveredShipments;
	}

	public Integer getCountClosedShipments() {
		return countClosedShipments;
	}

	public void setCountClosedShipments(Integer countClosedShipments) {
		this.countClosedShipments = countClosedShipments;
	}

	public BigDecimal getTotalOpenShipments() {
		return totalOpenShipments;
	}

	public void setTotalOpenShipments(BigDecimal totalOpenShipments) {
		this.totalOpenShipments = totalOpenShipments;
	}

	public BigDecimal getTotalDeliveredShipments() {
		return totalDeliveredShipments;
	}

	public void setTotalDeliveredShipments(BigDecimal totalDeliveredShipments) {
		this.totalDeliveredShipments = totalDeliveredShipments;
	}

	public BigDecimal getTotalClosedShipments() {
		return totalClosedShipments;
	}

	public void setTotalClosedShipments(BigDecimal totalClosedShipments) {
		this.totalClosedShipments = totalClosedShipments;
	}

	public Integer getCountOpenQuotes() {
		return countOpenQuotes;
	}

	public void setCountOpenQuotes(Integer countOpenQuotes) {
		this.countOpenQuotes = countOpenQuotes;
	}

	public Integer getCountCancelledQuotes() {
		return countCancelledQuotes;
	}

	public void setCountCancelledQuotes(Integer countCancelledQuotes) {
		this.countCancelledQuotes = countCancelledQuotes;
	}

	public Integer getCountApprovedQuotes() {
		return countApprovedQuotes;
	}

	public void setCountApprovedQuotes(Integer countApprovedQuotes) {
		this.countApprovedQuotes = countApprovedQuotes;
	}

	public BigDecimal getTotalOpenQuotes() {
		return totalOpenQuotes;
	}

	public void setTotalOpenQuotes(BigDecimal totalOpenQuotes) {
		this.totalOpenQuotes = totalOpenQuotes;
	}

	public BigDecimal getTotalCancelledQuotes() {
		return totalCancelledQuotes;
	}

	public void setTotalCancelledQuotes(BigDecimal totalCancelledQuotes) {
		this.totalCancelledQuotes = totalCancelledQuotes;
	}

	public BigDecimal getTotalApprovedQuotes() {
		return totalApprovedQuotes;
	}

	public void setTotalApprovedQuotes(BigDecimal totalApprovedQuotes) {
		this.totalApprovedQuotes = totalApprovedQuotes;
	}

	public Data getMinEstDate() {
		return minEstDate;
	}

	public void setMinEstDate(Data minEstDate) {
		this.minEstDate = minEstDate;
	}

	public Integer getInboxMsg() {
		return InboxMsg;
	}

	public void setInboxMsg(Integer inboxMsg) {
		InboxMsg = inboxMsg;
	}

	public Integer getProceededMsg() {
		return ProceededMsg;
	}

	public void setProceededMsg(Integer proceededMsg) {
		ProceededMsg = proceededMsg;
	}
}
