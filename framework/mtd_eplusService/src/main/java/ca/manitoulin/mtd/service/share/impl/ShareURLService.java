package ca.manitoulin.mtd.service.share.impl;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ca.manitoulin.mtd.service.share.IShareURLService;

@Service
public class ShareURLService implements IShareURLService{
	private static final Logger log = Logger.getLogger(ShareURLService.class);

	// eg
	// http://mtdirect.dapasoft.com/index.html#/app/messagecentral/messagedetails?id=12278275&isShare=Y
	// base: http://mtdirect.dapasoft.com
	// destination: /index.html#/app/messagecentral/messagedetails
	// params {id:12278275,isShare:Y}
	public  String generateShareURL(String base, String destination, Map<String, String> params) {
		String url = base;
		url += destination;
		if (null == params || params.isEmpty()) {
			log.debug("url is not share or need not params url->" + url);
			return url;
		} else {
			url += "?";
			int flag = 0;
			for (Entry<String, String> entry : params.entrySet()) {
				if (flag == 0) {
					url += entry.getKey() + "=" + entry.getValue();
				} else {
					url += "&" + entry.getKey() + "=" + entry.getValue();
				}
				flag++;
			}
			log.debug("share url is:" + url);
			return url;
		}

	}

	// eg:http://mtdirect.dapasoft.com/index.html#/app/messagecentral/messagedetails?id=12278275&isShare=Y
	// ==> destinationAndParams is .html#/app/messagecentral/messagedetails?id=12278275&isShare=Y
	public  String obtainURLDestinationAndParams(String URL) {
		if (isBlank(URL)) {
			log.error("URL can not be null!!!");
			return null;
		} else {
			try {
				return URL.substring(URL.indexOf(".html"), URL.length());
			} catch (Exception e) {
				log.error("URL-->" + URL + " format error" + e.getMessage());
			}
		}
		return null;
	}
}
