package ca.manitoulin.mtd.dto.security;

import ca.manitoulin.mtd.dto.AbstractDTO;

/**
 * Function is an object which point to a privilege to perform an operation
 * @author Bob Yu
 *
 */
public class SecureFunction extends AbstractDTO {

	private String id;
	private String name;
	private String remarks;
	
	private String parentFuncId;
	private String parentMenuId;
	private Integer numberOfSubFunction; 

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public Integer getNumberOfSubFunction() {
		return numberOfSubFunction;
	}

	public void setNumberOfSubFunction(Integer numberOfSubFunction) {
		this.numberOfSubFunction = numberOfSubFunction;
	}

	public String getParentFuncId() {
		return parentFuncId;
	}

	public void setParentFuncId(String parentFuncId) {
		this.parentFuncId = parentFuncId;
	}

	public String getParentMenuId() {
		return parentMenuId;
	}

	public void setParentMenuId(String parentMenuId) {
		this.parentMenuId = parentMenuId;
	}
	
}
