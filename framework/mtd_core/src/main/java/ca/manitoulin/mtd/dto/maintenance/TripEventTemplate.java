package ca.manitoulin.mtd.dto.maintenance;

import ca.manitoulin.mtd.dto.AbstractDTO;
import java.util.List;

public class TripEventTemplate extends AbstractDTO {
    private Integer id;
    private String type;
    private String typeDesc;
    private String name;
    private Integer categorySequence;
    private String category;
    private Integer sequence;
    private String code;
    private String item;
    private String description;
    private Double cost;
    private Integer linkedEntity;
    private String linkedEntityName;
    private String linkedEntityCell;
    private String linkedEntityAddress1;
    private String linkedEntityType;
    private Integer linkedEntityContact;
    private String customerNotify;
    private String status;
    private String duplicateEmailForAllEvent;
    private String eventClass;
    private String eventClassDesc;

    private List<TripEventTemplateCost> templateCost;
    private List<TripEventTemplateNotify> templateNotify;
    
    private String jointNotify;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getCategorySequence() {
        return categorySequence;
    }

    public void setCategorySequence(Integer categorySequence) {
        this.categorySequence = categorySequence;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TripEventTemplateCost> getTemplateCost() {
        return templateCost;
    }

    public void setTemplateCost(List<TripEventTemplateCost> templateCost) {
        this.templateCost = templateCost;
    }

    public List<TripEventTemplateNotify> getTemplateNotify() {
        return templateNotify;
    }

    public void setTemplateNotify(List<TripEventTemplateNotify> templateNotify) {
        this.templateNotify = templateNotify;
    }

    public String getDuplicateEmailForAllEvent() {
        return duplicateEmailForAllEvent;
    }

    public void setDuplicateEmailForAllEvent(String duplicateEmailForAllEvent) {
        this.duplicateEmailForAllEvent = duplicateEmailForAllEvent;
    }

	public String getLinkedEntityName() {
		return linkedEntityName;
	}

	public void setLinkedEntityName(String linkedEntityName) {
		this.linkedEntityName = linkedEntityName;
	}

	public String getLinkedEntityCell() {
		return linkedEntityCell;
	}

	public void setLinkedEntityCell(String linkedEntityCell) {
		this.linkedEntityCell = linkedEntityCell;
	}

    public String getCustomerNotify() {
        return customerNotify;
    }

    public void setCustomerNotify(String customerNotify) {
        this.customerNotify = customerNotify;
    }

    public String getLinkedEntityAddress1() {
        return linkedEntityAddress1;
    }

    public void setLinkedEntityAddress1(String linkedEntityAddress1) {
        this.linkedEntityAddress1 = linkedEntityAddress1;
    }

	public String getEventClass() {
		return eventClass;
	}

	public void setEventClass(String eventClass) {
		this.eventClass = eventClass;
	}

	public String getJointNotify() {
		return jointNotify;
	}

	public void setJointNotify(String jointNotify) {
		this.jointNotify = jointNotify;
	}

	public String getEventClassDesc() {
		return eventClassDesc;
	}

	public void setEventClassDesc(String eventClassDesc) {
		this.eventClassDesc = eventClassDesc;
	}

	public String getLinkedEntityType() {
		return linkedEntityType;
	}

	public void setLinkedEntityType(String linkedEntityType) {
		this.linkedEntityType = linkedEntityType;
	}
}
