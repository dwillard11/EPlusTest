package ca.manitoulin.mtd.service.share;

import java.util.Map;

public interface IShareURLService {

	public String generateShareURL(String base, String destination, Map<String, String> params);
		
	public String obtainURLDestinationAndParams(String URL);
}
