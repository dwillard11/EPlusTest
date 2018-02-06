package ca.manitoulin.mtd.dao.security;

import ca.manitoulin.mtd.dto.security.Account;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountMapper {

	List<Account> selectAccountsByNumberAndCompany(@Param("accountNumber")String accountNumber,@Param("company")String company);

	List<Account> selectGlobalAccount(@Param("company")String company,
			@Param("customer")String customer,
			@Param("userid")String userid);

	List<Account> selectAccountsByUserID(@Param("id") Integer id);

	void insertAccount(Account account);

	void deleteAccountByUser(@Param("secureUserId") Integer id);

	void updateAccount(Account account);
}
