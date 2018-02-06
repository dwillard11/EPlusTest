package ca.manitoulin.mtd.dao.maintenance;

import java.math.BigDecimal;

public interface CurrencyMapper {
	
	BigDecimal selectUSDRate(String currency);

}
