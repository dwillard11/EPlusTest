package ca.manitoulin.mtd.dao.messagecentral;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import ca.manitoulin.mtd.dto.massagecentral.Message;

public interface MessageMapper {


	void updateMessageDeleteIndicator(@Param("schema") String schema, @Param("messageId") String id);
	
	Message selectMessageById(@Param("schema") String schema, @Param("messageId") String id);
	
	List<Map<String, String>> selectMessageDetailById(@Param("schema") String schema, @Param("messageId") String id);
	
	List<Message> selectMessages(@Param("schema") String schema, 
			@Param("company") String company, @Param("userid") String userid, @Param("useraccount") String useraccount,@Param("advanceSearch") String advanceSearch);

	


}
