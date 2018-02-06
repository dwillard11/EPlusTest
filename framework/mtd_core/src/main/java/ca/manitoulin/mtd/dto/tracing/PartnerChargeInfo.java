package ca.manitoulin.mtd.dto.tracing;

import java.math.BigDecimal;

import ca.manitoulin.mtd.dto.AbstractDTO;

public class PartnerChargeInfo extends AbstractDTO {
	
	private Integer pieces;
	private String code;
	private String haz;
	private String pk;
	private String description;
	private BigDecimal weight;
	private BigDecimal rate;
	private BigDecimal amount;
	
	public Integer getPieces() {
		return pieces;
	}
	public void setPieces(Integer pieces) {
		this.pieces = pieces;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getHaz() {
		return haz;
	}
	public void setHaz(String haz) {
		this.haz = haz;
	}
	public String getPk() {
		return pk;
	}
	public void setPk(String pk) {
		this.pk = pk;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	

}
