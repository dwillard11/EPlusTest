package ca.manitoulin.mtd.dto.operationconsole;

import ca.manitoulin.mtd.dto.AbstractDTO;

import java.math.BigDecimal;

public class InvoiceDetail extends AbstractDTO {
	
	private Integer id;
	private Integer invoiceId;
	private Integer sequence;
	private String item;
	private String chargeCode;
	private String chargeDesp;
	private BigDecimal amount;
	private String taxCode;


    private BigDecimal taxCodeValue;

    public BigDecimal getTaxAmount() {
        if (taxCodeValue != null && amount != null) {
            return taxCodeValue.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotal() {
        if (amount != null) {
            return amount.add(getTaxAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getChargeCode() {
		return chargeCode;
	}
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}
	public String getChargeDesp() {
		return chargeDesp;
	}
	public void setChargeDesp(String chargeDesp) {
		this.chargeDesp = chargeDesp;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getTaxCode() {
		return taxCode;
	}
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

    public BigDecimal getTaxCodeValue() {
        return taxCodeValue;
    }

    public void setTaxCodeValue(BigDecimal taxCodeValue) {
        this.taxCodeValue = taxCodeValue;
    }
}
