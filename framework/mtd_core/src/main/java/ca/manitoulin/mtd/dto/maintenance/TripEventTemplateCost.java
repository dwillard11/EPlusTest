package ca.manitoulin.mtd.dto.maintenance;

import ca.manitoulin.mtd.dto.AbstractDTO;

import java.math.BigDecimal;
import java.util.Date;

public class TripEventTemplateCost extends AbstractDTO {
    private Integer id;
    private Integer templateId;
    private BigDecimal estCost;
    private Date estDate;
    private String estCurrency;
    private String chargeCode;
    private String chargeDesc;
    private String description;
    private String visible;
    private Integer linkedEntity;
    private Integer linkedEntityContact;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public BigDecimal getEstCost() {
        return estCost;
    }

    public void setEstCost(BigDecimal estCost) {
        this.estCost = estCost;
    }

    public Date getEstDate() {
        return estDate;
    }

    public void setEstDate(Date estDate) {
        this.estDate = estDate;
    }

    public String getEstCurrency() {
        return estCurrency;
    }

    public void setEstCurrency(String estCurrency) {
        this.estCurrency = estCurrency;
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
}
