package ca.manitoulin.mtd.dto.support;

import ca.manitoulin.mtd.dto.AbstractDTO;

/**
 * Enumeration saved in database.
 * @author Bob Yu
 */
public class AppEnum extends AbstractDTO {

    private static final long serialVersionUID = 8680133111264730745L;
    private Integer id; // ENUM_ID
    private String categoryCode; // ECAT_CODE
    private String code; // ENUM_CODE
    private String label; // ENUM_VAL
    
    private String label_english;
    private String label_french;
    private String label_chinese;
    private String label_german;
    private String label_spanish;

	private String remarks; // ENUM_REMARKS
	private Integer sortingOrder; // SORT_NUM

    private String status; //EpCode.cd_status
    private String attribute2;
    private String attribute3;
    private String attribute4;
    private String attribute5;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getSortingOrder() {
		return sortingOrder;
	}
	public void setSortingOrder(Integer sortingOrder) {
		this.sortingOrder = sortingOrder;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttribute2() {
		return attribute2;
	}
	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2;
	}
	public String getAttribute3() {
		return attribute3;
	}
	public void setAttribute3(String attribute3) {
		this.attribute3 = attribute3;
	}
	public String getAttribute4() {
		return attribute4;
	}
	public void setAttribute4(String attribute4) {
		this.attribute4 = attribute4;
	}
	public String getAttribute5() {
		return attribute5;
	}
	public void setAttribute5(String attribute5) {
		this.attribute5 = attribute5;
	}
	public String getLabel_english() {
		return label_english;
	}
	public void setLabel_english(String label_english) {
		this.label_english = label_english;
	}
	public String getLabel_french() {
		return label_french;
	}
	public void setLabel_french(String label_french) {
		this.label_french = label_french;
	}
	public String getLabel_chinese() {
		return label_chinese;
	}
	public void setLabel_chinese(String label_chinese) {
		this.label_chinese = label_chinese;
	}
	public String getLabel_german() {
		return label_german;
	}
	public void setLabel_german(String label_german) {
		this.label_german = label_german;
	}
	public String getLabel_spanish() {
		return label_spanish;
	}
	public void setLabel_spanish(String label_spanish) {
		this.label_spanish = label_spanish;
	}
}
