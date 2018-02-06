package ca.manitoulin.mtd.constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * Load config.properties, usually provides application constants and configurations
 * 
 * @author Bob Yu
 *
 */
public class ApplicationConstants {
	
	//Public constants
	public static final String PARAM_PAGE = "PAGE";
	
	
	//Configurations in config.properties
	private static final String CONFIG_FILE = "config.properties";
	private static Map<String, Object> configs = new HashMap<String, Object>();
	
	
	private static final Logger log = Logger.getLogger(ApplicationConstants.class);
	static {
		InputStream in = null;
		Properties p = new Properties();
		try{
			in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(CONFIG_FILE);		
			p.load(in);
			for(Object k : p.keySet()){
				String key = (String) k;
				configs.put( key, p.getProperty(key));
			}
			log.info("config.properties is loaded!"  );
		} catch (IOException e){
			log.error("Unable to read config.properties");				
		} finally{
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					log.error("Unable to close inputstream");		
				}
		}
	}
	
	/**
	 * get configuration in file config.properties
	 * @param key 
	 * @return settings
	 */
	public static String getConfig(String key){
		return (String) configs.get(key);
	}
	
	/**
	 * ad-hoc reset configurations, otherwise, the value will only be reloaded after the application rebooted.
	 * @param config
	 * @param value
	 */
	public static void set(String config, Object value){
		configs.put(config, value);
		
		log.info("--> Application Configuration Loaded: " + config + " = " + value);
	}
}
