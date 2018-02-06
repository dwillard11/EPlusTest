package ca.manitoulin.mtd.dto.operationconsole;

import java.math.BigDecimal;

import ca.manitoulin.mtd.dto.AbstractDTO;

public class Freight extends AbstractDTO {
	
	private Integer id;
	private String item;
	private String description;
	private Integer tripId;
	private Integer estimatedPieces;
	private String estimatedDimension; //40*40*40 CM
	private String estimatedUOM; //kg or dls
	private BigDecimal estimatedWeight;
	//private String estimatedWtUOM;
	private BigDecimal estimatedChargeWt;
	private BigDecimal estimatedCost;
	private String estimatedCurrency;
	
	private Integer actualPieces;
	private String actualDimension;
	private String actualUOM;
	private BigDecimal actualWeight;
	//private String actualWtUOM;
	private BigDecimal actualChargeWt;
	private BigDecimal actualCost;
	private String actualCurrency;	
	
	private String bagtag;
	private BigDecimal usdCost;
	private BigDecimal usdRate;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getTripId() {
		return tripId;
	}
	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}
	public Integer getEstimatedPieces() {
		return estimatedPieces;
	}
	public void setEstimatedPieces(Integer estimatedPieces) {
		this.estimatedPieces = estimatedPieces;
	}
	public String getEstimatedDimension() {
		return estimatedDimension;
	}
	public void setEstimatedDimension(String estimatedDimension) {
		this.estimatedDimension = estimatedDimension;
	}
	public BigDecimal getEstimatedWeight() {
		return estimatedWeight;
	}
	public void setEstimatedWeight(BigDecimal estimatedWeight) {
		this.estimatedWeight = estimatedWeight;
	}
	public BigDecimal getEstimatedCost() {
		return estimatedCost;
	}
	public void setEstimatedCost(BigDecimal estimatedCost) {
		this.estimatedCost = estimatedCost;
	}
	public String getEstimatedCurrency() {
		return estimatedCurrency;
	}
	public void setEstimatedCurrency(String estimatedCurrency) {
		this.estimatedCurrency = estimatedCurrency;
	}
	public String getEstimatedUOM() {
		return estimatedUOM;
	}
	public void setEstimatedUOM(String estimatedUOM) {
		this.estimatedUOM = estimatedUOM;
	}
	public Integer getActualPieces() {
		return actualPieces;
	}
	public void setActualPieces(Integer actualPieces) {
		this.actualPieces = actualPieces;
	}
	public String getActualDimension() {
		return actualDimension;
	}
	public void setActualDimension(String actualDimension) {
		this.actualDimension = actualDimension;
	}
	public String getActualUOM() {
		return actualUOM;
	}
	public void setActualUOM(String actualUOM) {
		this.actualUOM = actualUOM;
	}
	public BigDecimal getActualWeight() {
		return actualWeight;
	}
	public void setActualWeight(BigDecimal actualWeight) {
		this.actualWeight = actualWeight;
	}
	public BigDecimal getActualCost() {
		return actualCost;
	}
	public void setActualCost(BigDecimal actualCost) {
		this.actualCost = actualCost;
	}
	public String getActualCurrency() {
		return actualCurrency;
	}
	public void setActualCurrency(String actualCurrency) {
		this.actualCurrency = actualCurrency;
	}
	public String getBagtag() {
		return bagtag;
	}
	public void setBagtag(String bagtag) {
		this.bagtag = bagtag;
	}
	public BigDecimal getUsdCost() {
		return usdCost;
	}
	public void setUsdCost(BigDecimal usdCost) {
		this.usdCost = usdCost;
	}
	public BigDecimal getUsdRate() {
		return usdRate;
	}
	public void setUsdRate(BigDecimal usdRate) {
		this.usdRate = usdRate;
	}
	public BigDecimal getEstimatedChargeWt() {
		return estimatedChargeWt;
	}
	public void setEstimatedChargeWt(BigDecimal estimatedChargeWt) {
		this.estimatedChargeWt = estimatedChargeWt;
	}
	public BigDecimal getActualChargeWt() {
		return actualChargeWt;
	}
	public void setActualChargeWt(BigDecimal actualChargeWt) {
		this.actualChargeWt = actualChargeWt;
	}
	
	
	

}
