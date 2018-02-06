package ca.manitoulin.mtd.service.maintenance.impl;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.manitoulin.mtd.dao.maintenance.CurrencyMapper;
import ca.manitoulin.mtd.service.maintenance.ICurrencyService;

@Service
public class CurrencyService implements ICurrencyService {
	
	private static final Logger log = Logger.getLogger(CurrencyService.class);
	
	@Autowired
	private CurrencyMapper currencyMapper;

	@Override
	public BigDecimal retrieveUSDRate(String currencyCode) {
		
		BigDecimal rate = currencyMapper.selectUSDRate(currencyCode);
		
		BigDecimal cadExchangeUsdRate = currencyMapper.selectUSDRate("USD");
		
		return (rate == null ? null : rate.divide(cadExchangeUsdRate, 4, BigDecimal.ROUND_HALF_UP));
	}

}
