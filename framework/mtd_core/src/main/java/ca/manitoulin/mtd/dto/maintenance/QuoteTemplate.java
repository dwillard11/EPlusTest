package ca.manitoulin.mtd.dto.maintenance;

import ca.manitoulin.mtd.dto.AbstractDTO;

public class QuoteTemplate extends AbstractDTO {
    private Integer id;
    private String type;
    private String typeDesc;
    private String name;
    private Integer categorySequence;
    private String category;
    private Integer sequence;
    private String item;
    private String description;
    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategorySequence() {
        return categorySequence;
    }

    public void setCategorySequence(Integer categorySequence) {
        this.categorySequence = categorySequence;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
