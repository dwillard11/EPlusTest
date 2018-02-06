package ca.manitoulin.mtd.dao.security;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.manitoulin.mtd.dto.security.Account;

public interface AccountMapper {

	List<Account> selectAccountsByNumberAndCompany(@Param("accountNumber")String accountNumber,@Param("company")String company);
	
	List<Account> selectGlobalAccount(@Param("company")String company,
			@Param("customer")String customer,
			@Param("userid")String userid);
}
