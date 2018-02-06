package ca.manitoulin.mtd.service.maintenance;

import java.math.BigDecimal;

public interface ICurrencyService {

    BigDecimal retrieveUSDRate(String currencyCode);
}
