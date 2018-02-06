package ca.manitoulin.mtd.dto.operationconsole;

import ca.manitoulin.mtd.dto.AbstractDTO;
import ca.manitoulin.mtd.dto.customer.Customer;
import ca.manitoulin.mtd.util.DateUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Trip extends AbstractDTO {
	private Integer id;

	private Customer consignee;
	private Customer billedClient;
	private Customer dropShipper;
	private Customer broker;
	private Customer dropConsignee;
	private Customer client;
	
	private String status;//select * from epCode where cd_name='Trip Status'
	private String statusDesp;
	private String type; //select * from epCode where cd_name='Trip Type'
	private String typeDesp;
	private String refId;
	private Integer departmentId;
	private String departmentShortName;
	
	//Trip
	private String authorizedBy;
	private String authorizationNo;
	private Integer totalPieces;
	private BigDecimal totalWeight;
	private BigDecimal totalVolume;
	private BigDecimal totalCubicFeet;
	private Integer brokerId;
	private String brokerName;
	private Integer billedClientId;
	private String billedClientName;
	private Integer dropCosigneeId;
	private String dropCosigneeName;
	private Integer dropShipperId;
	private String dropShipperName;

	private Integer consigneeId2;
	private Integer consigneeId3;
	private Integer shipperId2;
	private Integer shipperId3;	
	private Integer brokerId2;
	
	//Pickup
	private Integer agentId;
	private Date pickupDate;
	private Date deliveryDate;
	private String port;
	private BigDecimal chargedWeight;
	private String dims;
	private Date callTime;
	private Date readyTime;
	private Date closeTime;
	private Date dispathTime;
	private Date pickupTime;
	private Date deliveryProtectTime;
	private Date podTime;
	private String podName;
	private String pickupInstruction;

	private String flightSchedule;
	private String dutiesTax;
	private String destinationCity;
	private String pickupCity;
	private Date nextEventDate;
	private String nextEventName;
	private String eventSubject;

	//properties in the list
	private Integer clientId;
	private String clientName;
	private Integer consigneeId;
	private String consigneeName;
	private String consigneeAddress1;
	private String consigneeCity;
	private String consigneeState;
	private String consigneeCountry;
	private Integer shipperId;
	private String shipperName;
	private String shipperAddress1;
	private String shipperCity;
	private String shipperState;
	private String shipperCountry;
	private String orig;
	private String dest;
	private Date criticalTime;
	private Date criticalUTCTime;
	private String note;
	private Integer version;
	private Date expireDate;
	private Date quoteDate;
	private BigDecimal quoteTotal;
	private String quoteCurrency;

	//lock info
	private String lockStatus;
	private String lockedBy;
	private Date locktime;

	//report
	private String freightUnits;
	private BigDecimal totalCost;
	private String amountInvoiced;
	private String billCurrency;
	private String chargeCode;
	private String chargeDesc;
	private String eventCode;
	private String eventDesc;
	private Integer invoiceId;
	private String invoiceNo;
	private String linkedContactName;

	private List<Freight> freights;
	private List<Condition> conditions;
	private List<TripCost> costs;

	// data structure for show the condition tree
	private List<Map<String, Object>> conditionTree;
	
	// data transfer fields
	private List<Integer> quoteTemplatesTobeImported;
	private List<Integer> eventTemplatesTobeImported;
	
	private Integer numberOfNotes;
	private Integer isTemplate;
	private Integer numberOfUnreadEmail;
	
	private String systemOfMeasure;
    private String templateName;
    private Integer eventTemplateId;

	private Date currentUTCTime;

	public String getShipperCountry() {
		return shipperCountry;
	}

	public void setShipperCountry(String shipperCountry) {
		this.shipperCountry = shipperCountry;
	}

	public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }


	public Integer getIsTemplate() {
		return isTemplate;
	}

	public void setIsTemplate(Integer isTemplate) {
		this.isTemplate = isTemplate;
	}

	public String getReadyTimeStr(){
		return DateUtil.toDateString(this.readyTime, DateUtil.LONG_DATE_FORMAT_SHOW);
	}
	
	public String getExpireDateStr(){
		return DateUtil.toDateString(this.expireDate, DateUtil.DATE_TIME_SHOW);
	}
	public String getQuoteRefNo(){
		return this.departmentShortName + "-Q-" + this.refId;
	}
	
	public String getTripRefNo(){
		return this.departmentShortName + "-" + this.refId;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Customer getConsignee() {
		return consignee;
	}
	public void setConsignee(Customer consignee) {
		this.consignee = consignee;
	}
	public Customer getBilledClient() {
		return billedClient;
	}
	public void setBilledClient(Customer billedClient) {
		this.billedClient = billedClient;
	}
	public Customer getDropShipper() {
		return dropShipper;
	}
	public void setDropShipper(Customer dropShipper) {
		this.dropShipper = dropShipper;
	}
	public Customer getBroker() {
		return broker;
	}
	public void setBroker(Customer broker) {
		this.broker = broker;
	}
	public Customer getDropConsignee() {
		return dropConsignee;
	}
	public void setDropConsignee(Customer dropConsignee) {
		this.dropConsignee = dropConsignee;
	}
	public Customer getClient() {
		return client;
	}
	public void setClient(Customer client) {
		this.client = client;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDesp() {
		return statusDesp;
	}
	public void setStatusDesp(String statusDesp) {
		this.statusDesp = statusDesp;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypeDesp() {
		return typeDesp;
	}
	public void setTypeDesp(String typeDesp) {
		this.typeDesp = typeDesp;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public Integer getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}
	public Integer getClientId() {
		return clientId;
	}
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public Integer getConsigneeId() {
		return consigneeId;
	}
	public void setConsigneeId(Integer consigneeId) {
		this.consigneeId = consigneeId;
	}
	public String getConsigneeName() {
		return consigneeName;
	}
	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}
	public Integer getShipperId() {
		return shipperId;
	}
	public void setShipperId(Integer shipperId) {
		this.shipperId = shipperId;
	}
	public String getShipperName() {
		return shipperName;
	}
	public void setShipperName(String shipperName) {
		this.shipperName = shipperName;
	}
	public String getOrig() {
		return orig;
	}
	public void setOrig(String orig) {
		this.orig = orig;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public Date getCriticalTime() {	return criticalTime;}
	public void setCriticalTime(Date criticalTime) {
		this.criticalTime = criticalTime;
	}
	public Date getCriticalUTCTime() { return criticalUTCTime;	}
	public void setCriticalUTCTime(Date criticalUTCTime) { this.criticalUTCTime = criticalUTCTime;}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Date getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
	public List<Freight> getFreights() {
		return freights;
	}
	public void setFreights(List<Freight> freights) {
		this.freights = freights;
	}
	public List<Condition> getConditions() {
		return conditions;
	}
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	public Date getQuoteDate() {
		return quoteDate;
	}
	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}
	public BigDecimal getQuoteTotal() {
		return quoteTotal;
	}
	public void setQuoteTotal(BigDecimal quoteTotal) {
		this.quoteTotal = quoteTotal;
	}
	public String getQuoteCurrency() { return quoteCurrency; }
	public void setQuoteCurrency(String quoteCurrency) { this.quoteCurrency = quoteCurrency; }

	public List<Map<String, Object>> getConditionTree() {
		return conditionTree;
	}

	public void setConditionTree(List<Map<String, Object>> conditionTree) {
		this.conditionTree = conditionTree;
	}
	public String getDepartmentShortName() {
		return departmentShortName;
	}
	public void setDepartmentShortName(String departmentShortName) {
		this.departmentShortName = departmentShortName;
	}

	public String getAuthorizedBy() {
		return authorizedBy;
	}

	public void setAuthorizedBy(String authorizedBy) {
		this.authorizedBy = authorizedBy;
	}

	public String getAuthorizationNo() {
		return authorizationNo;
	}

	public void setAuthorizationNo(String authorizationNo) {
		this.authorizationNo = authorizationNo;
	}

	public Integer getTotalPieces() {
		return totalPieces;
	}

	public void setTotalPieces(Integer totalPieces) {
		this.totalPieces = totalPieces;
	}

	public BigDecimal getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(BigDecimal totalWeight) {
		this.totalWeight = totalWeight;
	}

	public BigDecimal getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(BigDecimal totalVolume) {
		this.totalVolume = totalVolume;
	}

	public Integer getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(Integer brokerId) {
		this.brokerId = brokerId;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public Integer getBilledClientId() {
		return billedClientId;
	}

	public void setBilledClientId(Integer billedClientId) {
		this.billedClientId = billedClientId;
	}

	public String getBilledClientName() {
		return billedClientName;
	}

	public void setBilledClientName(String billedClientName) {
		this.billedClientName = billedClientName;
	}

	public Integer getDropCosigneeId() {
		return dropCosigneeId;
	}

	public void setDropCosigneeId(Integer dropCosigneeId) {
		this.dropCosigneeId = dropCosigneeId;
	}

	public String getDropCosigneeName() {
		return dropCosigneeName;
	}

	public void setDropCosigneeName(String dropCosigneeName) {
		this.dropCosigneeName = dropCosigneeName;
	}

	public Integer getDropShipperId() {
		return dropShipperId;
	}

	public void setDropShipperId(Integer dropShipperId) {
		this.dropShipperId = dropShipperId;
	}

	public String getDropShipperName() {
		return dropShipperName;
	}

	public void setDropShipperName(String dropShipperName) {
		this.dropShipperName = dropShipperName;
	}

	public Integer getConsigneeId2() {
		return consigneeId2;
	}

	public void setConsigneeId2(Integer consigneeId2) {
		this.consigneeId2 = consigneeId2;
	}

	public Integer getConsigneeId3() {
		return consigneeId3;
	}

	public void setConsigneeId3(Integer consigneeId3) {
		this.consigneeId3 = consigneeId3;
	}

	public Integer getShipperId2() {
		return shipperId2;
	}

	public void setShipperId2(Integer shipperId2) {
		this.shipperId2 = shipperId2;
	}

	public Integer getShipperId3() {
		return shipperId3;
	}

	public void setShipperId3(Integer shipperId3) {
		this.shipperId3 = shipperId3;
	}

	public Integer getBrokerId2() {
		return brokerId2;
	}

	public void setBrokerId2(Integer brokerId2) {
		this.brokerId2 = brokerId2;
	}

	public List<TripCost> getCosts() {
		return costs;
	}
	public void setCosts(List<TripCost> costs) {
		this.costs = costs;
	}
	public List<Integer> getQuoteTemplatesTobeImported() {
		return quoteTemplatesTobeImported;
	}
	public void setQuoteTemplatesTobeImported(List<Integer> quoteTemplatesTobeImported) {
		this.quoteTemplatesTobeImported = quoteTemplatesTobeImported;
	}
	public List<Integer> getEventTemplatesTobeImported() {
		return eventTemplatesTobeImported;
	}
	public void setEventTemplatesTobeImported(List<Integer> eventTemplatesTobeImported) {
		this.eventTemplatesTobeImported = eventTemplatesTobeImported;
	}
	public Date getPickupDate() {
		return pickupDate;
	}
	public void setPickupDate(Date pickupDate) {
		this.pickupDate = pickupDate;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public BigDecimal getChargedWeight() {
		return chargedWeight;
	}
	public void setChargedWeight(BigDecimal chargedWeight) {
		this.chargedWeight = chargedWeight;
	}
	public String getDims() {
		return dims;
	}
	public void setDims(String dims) {
		this.dims = dims;
	}
	public Date getCallTime() {
		return callTime;
	}
	public void setCallTime(Date callTime) {
		this.callTime = callTime;
	}
	public Date getReadyTime() {
		return readyTime;
	}
	public void setReadyTime(Date readyTime) {
		this.readyTime = readyTime;
	}
	public Date getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}
	public Date getDispathTime() {
		return dispathTime;
	}
	public void setDispathTime(Date dispathTime) {
		this.dispathTime = dispathTime;
	}
	public Date getPickupTime() {
		return pickupTime;
	}
	public void setPickupTime(Date pickupTime) {
		this.pickupTime = pickupTime;
	}
	public Date getDeliveryProtectTime() {
		return deliveryProtectTime;
	}
	public void setDeliveryProtectTime(Date deliveryProtectTime) {
		this.deliveryProtectTime = deliveryProtectTime;
	}
	public Date getPodTime() {
		return podTime;
	}
	public void setPodTime(Date podTime) {
		this.podTime = podTime;
	}
	public String getPodName() {
		return podName;
	}
	public void setPodName(String podName) {
		this.podName = podName;
	}
	public String getPickupInstruction() {
		return pickupInstruction;
	}
	public void setPickupInstruction(String pickupInstruction) {
		this.pickupInstruction = pickupInstruction;
	}
	public Integer getAgentId() {
		return agentId;
	}
	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public String getFlightSchedule() {
		return flightSchedule;
	}

	public void setFlightSchedule(String flightSchedule) {
		this.flightSchedule = flightSchedule;
	}

	public String getDutiesTax() {
		return dutiesTax;
	}

	public void setDutiesTax(String dutiesTax) {
		this.dutiesTax = dutiesTax;
	}

	public String getDestinationCity() {
		return destinationCity;
	}

	public void setDestinationCity(String destinationCity) {
		this.destinationCity = destinationCity;
	}

	public String getPickupCity() {
		return pickupCity;
	}

	public void setPickupCity(String pickupCity) {
		this.pickupCity = pickupCity;
	}

	public Date getNextEventDate() {
		return nextEventDate;
	}

	public void setNextEventDate(Date nextEventDate) {
		this.nextEventDate = nextEventDate;
	}

	public String getNextEventName() {
		return nextEventName;
	}

	public void setNextEventName(String nextEventName) {
		this.nextEventName = nextEventName;
	}

	public String getEventSubject() {
		return eventSubject;
	}

	public void setEventSubject(String eventSubject) {
		this.eventSubject = eventSubject;
	}

	public String getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(String lockStatus) {
		this.lockStatus = lockStatus;
	}
	public BigDecimal getTotalCubicFeet() {
		return totalCubicFeet;
	}
	public void setTotalCubicFeet(BigDecimal totalCubicFeet) {
		this.totalCubicFeet = totalCubicFeet;
	}
	public String getLockedBy() {
		return lockedBy;
	}
	public void setLockedBy(String lockedBy) {
		this.lockedBy = lockedBy;
	}
	public Date getLocktime() {
		return locktime;
	}
	public void setLocktime(Date locktime) {
		this.locktime = locktime;
	}
	public Integer getNumberOfNotes() {
		return numberOfNotes;
	}
	public void setNumberOfNotes(Integer numberOfNotes) {
		this.numberOfNotes = numberOfNotes;
	}

	public String getFreightUnits() { return freightUnits; }
	public void setFreightUnits(String freightUnits) { this.freightUnits = freightUnits; }
	public BigDecimal getTotalCost() { return totalCost; }
	public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
	public String getAmountInvoiced() { return amountInvoiced; }
	public void setAmountInvoiced(String amountInvoiced) { this.amountInvoiced = amountInvoiced; }
	public String getBillCurrency() { return billCurrency; }
	public void setBillCurrency(String billCurrency) { this.billCurrency = billCurrency; }

	public String getSystemOfMeasure() {
		return systemOfMeasure;
	}

	public void setSystemOfMeasure(String systemOfMeasure) {
		this.systemOfMeasure = systemOfMeasure;
	}

	public Integer getNumberOfUnreadEmail() {
		return numberOfUnreadEmail;
	}

	public void setNumberOfUnreadEmail(Integer numberOfUnreadEmail) {
		this.numberOfUnreadEmail = numberOfUnreadEmail;
	}
	public String getConsigneeAddress1() {
		return consigneeAddress1;
	}

	public void setConsigneeAddress1(String consigneeAddress1) {
		this.consigneeAddress1 = consigneeAddress1;
	}

	public String getConsigneeCity() {
		return consigneeCity;
	}

	public void setConsigneeCity(String consigneeCity) {
		this.consigneeCity = consigneeCity;
	}

	public String getShipperAddress1() {
		return shipperAddress1;
	}

	public void setShipperAddress1(String shipperAddress1) {
		this.shipperAddress1 = shipperAddress1;
	}

	public String getShipperCity() {
		return shipperCity;
	}

	public void setShipperCity(String shipperCity) {
		this.shipperCity = shipperCity;
	}

	public String getConsigneeState() {
		return consigneeState;
	}

	public void setConsigneeState(String consigneeState) {
		this.consigneeState = consigneeState;
	}

	public String getConsigneeCountry() {
		return consigneeCountry;
	}

	public void setConsigneeCountry(String consigneeCountry) {
		this.consigneeCountry = consigneeCountry;
	}

	public String getShipperState() {
		return shipperState;
	}

	public void setShipperState(String shipperState) {
		this.shipperState = shipperState;
	}

	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
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

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public Integer getEventTemplateId() {
		return eventTemplateId;
	}

	public void setEventTemplateId(Integer eventTemplateId) {
		this.eventTemplateId = eventTemplateId;
	}

	public Date getCurrentUTCTime() {
		return currentUTCTime;
	}

	public void setCurrentUTCTime(Date currentUTCTime) {
		this.currentUTCTime = currentUTCTime;
	}

	public String getLinkedContactName() {
		return linkedContactName;
	}

	public void setLinkedContactName(String linkedContactName) {
		this.linkedContactName = linkedContactName;
	}
}
