package ca.manitoulin.mtd.dto.security;

import ca.manitoulin.mtd.dto.AbstractDTO;

public class Account extends AbstractDTO {
	
	private String company;
	private String customer;
	private String accountNumber;
	private String description;

	private Integer id;
	private Integer secureUserId;
	private String code;
	private String name;
	private String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSecureUserId() {
		return secureUserId;
	}

	public void setSecureUserId(Integer secureUserId) {
		this.secureUserId = secureUserId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
