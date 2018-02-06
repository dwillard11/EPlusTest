package ca.manitoulin.mtd.dto.security;

import ca.manitoulin.mtd.code.PrivilegeType;
import ca.manitoulin.mtd.dto.AbstractDTO;

/**
 * Privilege POJO
 * @author Bob Yu
 *
 */
public class Privilege extends AbstractDTO {

	//mapped fields
	private String id;
	private String sysCode;
	private PrivilegeType category;
	
	//business fields
	private String name;
	
	public void setPrivilegeTypeCode(String code){
		this.category = PrivilegeType.valueByCode(code);
	}
	
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
	public String getSysCode() {
		return sysCode;
	}
	public void setSysCode(String sysCode) {
		this.sysCode = sysCode;
	}
	public PrivilegeType getCategory() {
		return category;
	}
	public void setCategory(PrivilegeType category) {
		this.category = category;
	}
	
}
