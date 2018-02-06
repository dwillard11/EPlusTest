package ca.manitoulin.mtd.dto.operationconsole;

import ca.manitoulin.mtd.dto.AbstractDTO;

import java.math.BigDecimal;
import java.util.Date;

import static ca.manitoulin.mtd.util.DateUtil.LONG_DATE_FORMAT_SHOW;
import static ca.manitoulin.mtd.util.DateUtil.toDateString;

public class TripCost extends AbstractDTO{
	
	private Integer id;
	private Integer tripId;
	
	private Date estDate;
	private BigDecimal estCost;
	private String estCurrency;
	private Date actDate;
	private BigDecimal actCost;
	private String actCurrency;
	
	private BigDecimal amount;
	private String chargeCode;
	private String chargeDesc;
	private BigDecimal actUsedCost;
	private BigDecimal actUsedRate;
	private String description;
	
	private String eventItem;
	private Integer eventId;
	private Integer linkedEntity;
	private Integer linkedEntityContact;
	private String visible;

	private String linkedEntityName;
	private String linkedContactName;
	
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
	public Date getEstDate() {
		return estDate;
	}
	public void setEstDate(Date estDate) {
		this.estDate = estDate;
	}
	public BigDecimal getEstCost() {
		return estCost;
	}
	public void setEstCost(BigDecimal estCost) {
		this.estCost = estCost;
	}
	public String getEstCurrency() {
		return estCurrency;
	}
	public void setEstCurrency(String estCurrency) {
		this.estCurrency = estCurrency;
	}
	public Date getActDate() {
		return actDate;
	}
	public void setActDate(Date actDate) {
		this.actDate = actDate;
	}
	public BigDecimal getActCost() {
		return actCost;
	}
	public void setActCost(BigDecimal actCost) {
		this.actCost = actCost;
	}
	public String getActCurrency() {
		return actCurrency;
	}
	public void setActCurrency(String actCurrency) {
		this.actCurrency = actCurrency;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getChargeCode() {
		return chargeCode;
	}
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}
	public String getChargeDesc() {
		return chargeDesc;
	}
	public void setChargeDesc(String chargeDesc) {
		this.chargeDesc = chargeDesc;
	}
	public BigDecimal getActUsedCost() {
		return actUsedCost;
	}
	public void setActUsedCost(BigDecimal actUsedCost) {
		this.actUsedCost = actUsedCost;
	}
	public BigDecimal getActUsedRate() {
		return actUsedRate;
	}
	public void setActUsedRate(BigDecimal actUsedRate) {
		this.actUsedRate = actUsedRate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getVisible() {
		return visible;
	}
	public void setVisible(String visible) {
		this.visible = visible;
	}
	public String getEventItem() {
		return eventItem;
	}
	public void setEventItem(String eventItem) {
		this.eventItem = eventItem;
	}
	public Integer getEventId() {
		return eventId;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	public Integer getLinkedEntity() {
		return linkedEntity;
	}
	public void setLinkedEntity(Integer linkedEntity) {
		this.linkedEntity = linkedEntity;
	}

	public Integer getLinkedEntityContact() {
		return linkedEntityContact;
	}

	public void setLinkedEntityContact(Integer linkedEntityContact) {
		this.linkedEntityContact = linkedEntityContact;
	}

	public String getActDateStr() {
        return toDateString(getActDate(), LONG_DATE_FORMAT_SHOW);
    }

    public String getEstDateStr() {
        return toDateString(getEstDate(), LONG_DATE_FORMAT_SHOW);
    }

	public String getLinkedEntityName() {
		return linkedEntityName;
	}

	public void setLinkedEntityName(String linkedEntityName) {
		this.linkedEntityName = linkedEntityName;
	}

	public String getLinkedContactName() {
		return linkedContactName;
	}

	public void setLinkedContactName(String linkedContactName) {
		this.linkedContactName = linkedContactName;
	}
}
