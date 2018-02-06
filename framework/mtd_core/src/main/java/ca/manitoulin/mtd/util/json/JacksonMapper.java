package ca.manitoulin.mtd.util.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Helper to use Jackson 
 * @author Bob Yu
 * @since 1.0
 */
public class JacksonMapper {
	private static final Logger log = Logger.getLogger(JacksonMapper.class);
	private static ObjectMapper mapper ;  
	private JacksonMapper() { 
		if(mapper == null){
			mapper = new ObjectMapper();
			SerializationConfig serializationConfig = mapper.getSerializationConfig();
			//NULL will be ignore
			serializationConfig = serializationConfig.withSerializationInclusion(Inclusion.NON_NULL);
			//Set default date format
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			serializationConfig.withDateFormat(df);
			
			mapper.setSerializationConfig(serializationConfig);
			
			DeserializationConfig deserializationConfig = mapper.getDeserializationConfig();
			//unknown properties will allowed?
			deserializationConfig = deserializationConfig.without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
			mapper.setDeserializationConfig(deserializationConfig);
			
			log.info("<New Instance> : JacksonMapper");
		}
	}
	private static class JacksonMapperHolder{
		public static final JacksonMapper instance = new JacksonMapper();		
	}
	public static ObjectMapper getInstance() {
		return JacksonMapperHolder.instance.getMapper();
	}
	public ObjectMapper getMapper(){
		return mapper;
	}
}
