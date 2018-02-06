package ca.manitoulin.mtd.dto.customer;

import java.util.ArrayList;
import java.util.List;

import ca.manitoulin.mtd.dto.AbstractDTO;

public class Customer extends AbstractDTO {
	
	private Integer id;
	private String type;
	private String typeDesc;
	private String name;
	private String quickName;
	private String status;
	
	private List<CustomerLocation> divisions;

	private Integer parentId;
	private String nodeType;
	private Integer isOnline;
	
	public void setTypeInString(String t){
		this.type = t;
	}
	
	public void setType(String t){
		this.type = t;
	}
	
	public Customer(){
		divisions = new ArrayList<CustomerLocation>();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuickName() {
		return quickName;
	}

	public void setQuickName(String quickName) {
		this.quickName = quickName;
	}

	public List<CustomerLocation> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<CustomerLocation> divisions) {
		this.divisions = divisions;
	}

	public String getType() {
		return type;
	}

	

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	public Integer getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Integer isOnline) {
		this.isOnline = isOnline;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
