package ca.manitoulin.mtd.util.json;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * JSON -> Object, Object -> JSON translator
 * 
 * @author Bob Yu
 * @since 1.0
 * 
 */
public class JsonHelper {

	private static final Logger log = Logger.getLogger(JsonHelper.class);
	
	private JsonHelper(){
		
	}

	/**
	 * JSON to object
	 * 
	 * @param jsonStr
	 * @param clazz
	 * @return
	 * @throws IOException 
	 */
	public static <T>T toObject(String jsonStr, Class<T> clazz) throws IOException {
		if(jsonStr == null)
			return null;
		ObjectMapper mapper = JacksonMapper.getInstance();
		T bean = null;
		
		bean = mapper.readValue(jsonStr, clazz);
		
		return bean;

	}

	/**
	 * object to JSON<br/>
	 * in case need to ignore some properties, use @JsonIgnore
	 * 
	 * @param o
	 * @return
	 * @throws IOException 
	 */
	public static String toJsonString(Object o) throws IOException {
		if (o == null)
			return StringUtils.EMPTY;
		
		ObjectMapper mapper = JacksonMapper.getInstance();
		StringWriter sw = new StringWriter();
		JsonGenerator jsonGenerator = null;
		String rtn = null;
		jsonGenerator = new JsonFactory().createJsonGenerator(sw);
		mapper.writeValue(jsonGenerator, o);
		rtn = sw.toString();
				
		return rtn;
	}
	
	/**
	 * Force convert to specified object.
	 * @param o
	 * @param clazz
	 * @return
	 */
	public static <T>T toObject(Object o, Class<T> clazz) {
		if(o == null) return null;
		ObjectMapper mapper = JacksonMapper.getInstance();
		T bean = null;
		
		bean = mapper.convertValue(o, clazz);
		
		return bean;

	}
}
