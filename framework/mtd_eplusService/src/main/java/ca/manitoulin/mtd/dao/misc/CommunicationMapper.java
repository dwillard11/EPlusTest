package ca.manitoulin.mtd.dao.misc;

import ca.manitoulin.mtd.dto.misc.CommunicationEmail;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CommunicationMapper {
	
	void insert(CommunicationEmail email);

	void update(CommunicationEmail email);
	
	//List<CommunicationEmail> selectByTrip(Integer tripId);
	List<CommunicationEmail> searchEmails(@Param("tripId")Integer tripId, 
			@Param("dateFrom")Date dateFrom, 
			@Param("dateTo")Date dateTo,
			@Param("label") String label,
			@Param("searchKey")String searchKey,
			@Param("isAdmin") Boolean isAdmin);
	
	//List<CommunicationEmail> selectUnlinked(@Param("departmentIdList") List<Integer> deps);
	List<CommunicationEmail> selectUnlinkedByConditions(
			@Param("dateFrom")Date dateFrom, 
			@Param("dateTo")Date dateTo, 
			@Param("label") String label,
			@Param("searchKey")String searchKey,
			@Param("includeDelete") String includeDelete,
			@Param("includeProcessed") String includeProcessed,
			@Param("includeOut") String includeOut,
			@Param("departmentIdList") List<Integer> deps,
			@Param("isAdmin") Boolean isAdmin);

	void undelete(Long id);

	void delete(Long id);
	
	void deletePyshically(Long id);
	
	CommunicationEmail selectById(Long id);
	
	void updateLink(CommunicationEmail email);
	
	void updateReadStatus(@Param("id") Long id, @Param("readStatus") String status);

    void updateProcessStatus(@Param("id") Long emailId, @Param("processedStatus") String processed);

    void upLinkTrip(@Param("id") Long emailId);
    
    void updateLabel(@Param("id") Long id, @Param("label") String label);
}
