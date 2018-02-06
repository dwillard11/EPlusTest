package ca.manitoulin.mtd.dto.support;

import java.util.Date;

import ca.manitoulin.mtd.dto.AbstractDTO;


/**
 * System log saved in database B_LOG
 * @author Bob Yu
 *
 */
public class AppLog extends AbstractDTO {

	private static final long serialVersionUID = -7943615473378201934L;
	private String id;
	private String category;
	private String description;

	private String operator;
	private Date operationTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Date getOperationTime() {
		return operationTime;
	}
	public void setOperationTime(Date operationTime) {
		this.operationTime = operationTime;
	}

	
}
