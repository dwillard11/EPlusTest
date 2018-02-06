package ca.manitoulin.mtd.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Super class of all data transfer object, implements java.io.Serializable
 * @author Bob Yu
 * @since 1.0
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractDTO implements Serializable {

	protected String createdBy;
	protected Date createTime;
	protected String updatedBy;
	protected Date updateTime;
	protected String currentCompany;
	protected String currentCustomer;
	
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		//result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		//result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result + ((updatedBy == null) ? 0 : updatedBy.hashCode());
		return result;
	}
	public String getCurrentCompany() {
		return currentCompany;
	}
	public void setCurrentCompany(String currentCompany) {
		this.currentCompany = currentCompany;
	}
	public String getCurrentCustomer() {
		return currentCustomer;
	}
	public void setCurrentCustomer(String currentCustomer) {
		this.currentCustomer = currentCustomer;
	}
}
