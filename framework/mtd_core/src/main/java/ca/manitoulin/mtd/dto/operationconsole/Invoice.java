package ca.manitoulin.mtd.dto.operationconsole;

import ca.manitoulin.mtd.dto.AbstractDTO;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Invoice extends AbstractDTO {
	
	private Integer id;
	private Integer tripId;
	private String refNum;
	private Date invoiceDate;
	private String invoiceFrom;
	private String invoiceTo;
	private BigDecimal miles;
	private BigDecimal rate;
	private String billingCurrency;
	private BigDecimal netProfit;
	private String netProfitCurrency;
	private BigDecimal exchangeRate;
	
	//List fields
	private String tripRefNum;
	private String serviceType;
	private String shipper;
	private String billedClient;
	private BigDecimal totalAmount;
	//private Integer version;
	private BigDecimal invoiceSubtotal;
	
	//Trip fields
	private Date pickupDate;
	private Date deliveryDate;
	private String authBy;
	private String authNo;
	private Integer totalPieces;
	private BigDecimal actualWeight;
	private BigDecimal chargeableWeight;
	private String systemOfMeasure;
	private Integer billedClientId;
	private Integer shipperId;

	private Integer documentId;
	
	private List<InvoiceDetail> details;
	
	public void replicateTripInfo(Trip trip){
		if(trip == null) return ;
		
		this.tripId = trip.getId();
		this.pickupDate = trip.getPickupDate();
		this.deliveryDate = trip.getDeliveryDate();
		//this.deliveryDate = trip.getPodTime();
		this.authBy = trip.getAuthorizedBy();
		this.authNo = trip.getAuthorizationNo();
		this.serviceType = trip.getTypeDesp();
		this.totalPieces = trip.getTotalPieces();
		this.actualWeight = trip.getTotalWeight();
		this.chargeableWeight = trip.getChargedWeight();
		this.billedClientId = trip.getBilledClientId();
		this.shipperId = trip.getShipperId();
		this.systemOfMeasure = trip.getSystemOfMeasure();
		if(isEmpty(invoiceFrom)) {
			if (StringUtils.isEmpty(trip.getShipperAddress1()) && StringUtils.isEmpty(trip.getShipperCity())) {
				this.invoiceFrom = "";
			} else {
				Joiner joiner = Joiner.on(", ").skipNulls();
				this.invoiceFrom = joiner.join(StringUtils.isEmpty(trip.getShipperAddress1()) ? "" : trip.getShipperAddress1(), StringUtils.isEmpty(trip.getShipperCity()) ? "" : trip.getShipperCity());
			}
		}
		if(isEmpty(invoiceTo)){
			if (StringUtils.isEmpty(trip.getConsigneeAddress1()) && StringUtils.isEmpty(trip.getConsigneeCity())) {
				this.invoiceTo = "";
			} else {
				Joiner joiner = Joiner.on(", ").skipNulls();
				this.invoiceTo = joiner.join(StringUtils.isEmpty(trip.getConsigneeAddress1()) ? "" : trip.getConsigneeAddress1(), StringUtils.isEmpty(trip.getConsigneeCity()) ? "" : trip.getConsigneeCity());
			}
		}
	}
	
	public Trip updateToTrip(Trip trip){
		trip.setPickupDate(this.pickupDate);
		trip.setDeliveryDate(deliveryDate);
		trip.setPodTime(deliveryDate);
		trip.setAuthorizationNo(authNo);
		trip.setAuthorizedBy(authBy);
		trip.setBilledClientId(billedClientId);
		trip.setShipperId(shipperId);
		trip.setBillCurrency(this.billingCurrency);
		return trip;
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
	public String getRefNum() {
		return refNum;
	}
	public void setRefNum(String refNum) {
		this.refNum = refNum;
	}
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getInvoiceFrom() {
		return invoiceFrom;
	}
	public void setInvoiceFrom(String invoiceFrom) {
		this.invoiceFrom = invoiceFrom;
	}
	public String getInvoiceTo() {
		return invoiceTo;
	}
	public void setInvoiceTo(String invoiceTo) {
		this.invoiceTo = invoiceTo;
	}
	public BigDecimal getMiles() {
		return miles;
	}
	public void setMiles(BigDecimal miles) {
		this.miles = miles;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public List<InvoiceDetail> getDetails() {
		return details;
	}
	public void setDetails(List<InvoiceDetail> details) {
		this.details = details;
	}
	public String getBillingCurrency() {
		return billingCurrency;
	}
	public void setBillingCurrency(String billingCurrency) {
		this.billingCurrency = billingCurrency;
	}
	public String getTripRefNum() {
		return tripRefNum;
	}
	public void setTripRefNum(String tripRefNum) {
		this.tripRefNum = tripRefNum;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getShipper() {
		return shipper;
	}
	public void setShipper(String shipper) {
		this.shipper = shipper;
	}

	public String getBilledClient() {
		return billedClient;
	}

	public void setBilledClient(String billedClient) {
		this.billedClient = billedClient;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	/*public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}*/
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
	public String getAuthBy() {
		return authBy;
	}
	public void setAuthBy(String authBy) {
		this.authBy = authBy;
	}
	public String getAuthNo() {
		return authNo;
	}
	public void setAuthNo(String authNo) {
		this.authNo = authNo;
	}
	public Integer getTotalPieces() {
		return totalPieces;
	}
	public void setTotalPieces(Integer totalPieces) {
		this.totalPieces = totalPieces;
	}
	public BigDecimal getActualWeight() {
		return actualWeight;
	}
	public void setActualWeight(BigDecimal actualWeight) {
		this.actualWeight = actualWeight;
	}
	public BigDecimal getChargeableWeight() {
		return chargeableWeight;
	}
	public void setChargeableWeight(BigDecimal chargeableWeight) {
		this.chargeableWeight = chargeableWeight;
	}

	public String getSystemOfMeasure() {
		return systemOfMeasure;
	}

	public void setSystemOfMeasure(String systemOfMeasure) {
		this.systemOfMeasure = systemOfMeasure;
	}

	public Integer getBilledClientId() {
		return billedClientId;
	}
	public void setBilledClientId(Integer billedClientId) {
		this.billedClientId = billedClientId;
	}
	public Integer getShipperId() {
		return shipperId;
	}
	public void setShipperId(Integer shipperId) {
		this.shipperId = shipperId;
	}

	public BigDecimal getInvoiceSubtotal() {
		return invoiceSubtotal;
	}

	public void setInvoiceSubtotal(BigDecimal invoiceSubtotal) {
		this.invoiceSubtotal = invoiceSubtotal;
	}

    public String getActualWeightWithUOM() {
        if (equal(null, getActualWeight())) return EMPTY;
        if (getSystemOfMeasure()!=null && getSystemOfMeasure().equals("M"))
        	return (new StringBuffer()).append(getActualWeight()).append(" ").append("KG").toString();
        else
			return (new StringBuffer()).append(getActualWeight()).append(" ").append("LBS").toString();
    }

    public String getChargeableWeightWithUOM() {
        if (equal(null, getChargeableWeight())) return EMPTY;
		if (getSystemOfMeasure()!=null && getSystemOfMeasure().equals("M"))
        	return (new StringBuffer()).append(getChargeableWeight()).append(" ").append("KG").toString();
		else
			return (new StringBuffer()).append(getChargeableWeight()).append(" ").append("LBS").toString();
    }

	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	public BigDecimal getNetProfit() {
		return netProfit;
	}

	public void setNetProfit(BigDecimal netProfit) {
		this.netProfit = netProfit;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getNetProfitCurrency() {
		return netProfitCurrency;
	}

	public void setNetProfitCurrency(String netProfitCurrency) {
		this.netProfitCurrency = netProfitCurrency;
	}
}
