package ca.manitoulin.mtd.dao.messagecentral;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.manitoulin.mtd.dto.massagecentral.MessageSubscription;
import ca.manitoulin.mtd.dto.security.SecureUser;

public interface MessageSubscriptionMapper {
	
	List<MessageSubscription> selectSubscriptions(@Param("schema") String schema,
			@Param("userid") String userid,
			@Param("company") String company,
			@Param("useraccount") String useraccount);
	
	MessageSubscription selectSubscription(@Param("schema") String schema, @Param("id")String id);
	
	void updateSubscription(@Param("schema") String schema, @Param("object")MessageSubscription messageSubscription);
	
	void insertSubscription(@Param("schema") String schema, @Param("object") MessageSubscription messageSubscription, @Param("user") SecureUser user);
	
	void deleteSubscription(@Param("schema") String schema,
			@Param("id") String id);

}
