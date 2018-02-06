package ca.manitoulin.mtd.dao.operationconsole;

import ca.manitoulin.mtd.dto.operationconsole.Trip;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TripMapper {
	String getDivisionCodeByTripId(@Param("tripId") Integer tripId);

	List<Trip> selectQuotes(
			@Param("departmentIdList") List<Integer> departmentIdList,
			@Param("departmentId") Integer departmentId,
							@Param("statusCode") String statusCode,
							@Param("searchKey") String searchKey,
							@Param("startDate") String startDate,
							@Param("endDate") String endDate,
							@Param("language") String lang);
	
	List<Trip> selectTrips(
			@Param("departmentIdList") List<Integer> departmentIdList,
			@Param("departmentId") Integer departmentId,
			@Param("tripStatus") String statusCode,
						   @Param("searchKey") String searchKey,
						   @Param("startDate") String startDate,
						   @Param("endDate") String endDate,
							@Param("awb") String awb,
							@Param("chargeCode") String chargeCode,
							@Param("language") String lang);

	List<Trip> selectTripList(
			@Param("departmentIdList") List<Integer> departmentIdList,
			@Param("departmentId") Integer departmentId,
			@Param("tripStatus") String statusCode,
			@Param("searchKey") String searchKey,
			@Param("startDate") String startDate,
			@Param("endDate") String endDate,
			@Param("awb") String awb,
			@Param("eventDesc") String eventDesc,
			@Param("chargeCode") String chargeCode,
			@Param("chargeDesc") String chargeDesc,
			@Param("language") String lang);

	void insertTrip(Trip trip);
	
	void updateTripReference(@Param("tripId") Integer id, @Param("ref") String ref);
	
	void updateQuote(Trip trip);

    Trip selectQuoteByID(@Param("tripId") Integer quoteId, @Param("language") String lang);
    
    void updateTrip(Trip trip);
    
    void updatePickup(Trip trip);

	void updateQuotePdfData(Trip trip);
	
	void updateBOL(Trip trip);
	
	void updateTripNextEvent(@Param("tripId")Integer tripId, @Param("nextEventName")String nextEventName, @Param("nextEventDate")Date nextEventTime);
	
	void updateTripLockStatus(Trip trip);
	
	void deleteTripLockStatusByUUID(String userUId);

	int findTripByRefNumber(String refNumber);

	Trip selectTripByRefNumber(String refNumber);

	void deleteTripLockStatusByID(@Param("tripId") Integer tripId);

	List<Trip> selectLockedTripAndQuotes(
			@Param("departmentIdList") List<Integer> departmentIdList, @Param("language") String lang);

    int selectTemplateName(@Param("templateName")String templateName);

	void updateTripTemplateName(@Param("tripId") Integer tripId, @Param("templateName") String templateName, @Param("eventTemplateId") Integer eventTemplateId);

	List<Trip> selectTripTemplates(@Param("departmentId") Integer departmentId);
	
	void updateTripUOM(Trip trip);
	
	void updatePOD(Trip trip);
	
	void updateBillingInfo(Trip trip);
	
	void deleteTemplateEvent(Integer eventTemplateId);

	void deleteTemplateEventByCategory(@Param("tripType") String tripType, @Param("name") String name);

	void updateTripDivision(Trip trip);

	String getTripEventSubject(@Param("tripId") Integer tripId,
							   @Param("language") String lang);

	void updateTripEventSubject(@Param("tripId") Integer tripId,
								@Param("eventSubject") String eventSubject);
}
