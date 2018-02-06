package ca.manitoulin.mtd.dao.operationconsole;

import ca.manitoulin.mtd.dto.operationconsole.Trip;
import ca.manitoulin.mtd.dto.operationconsole.TripEvent;
import ca.manitoulin.mtd.dto.operationconsole.TripEventNotify;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TripEventMapper {
	
    List<TripEvent> selectTripEvents(@Param("tripId") Integer tripId, @Param("language") String lang);

    TripEvent selectTripEventById(Integer id);

    void insertTripEvent(TripEvent TripEvent);

    void deleteTripEvent(Integer id);

    void updateTripEvent(TripEvent TripEvent);

    void updateTripEventCategorySequence(TripEvent tripEvent);

    void updateTripEventSequence(TripEvent tripEvent);

    Integer getTripEventCategorySequenceByCategory(@Param("name") String name, @Param("category") String category, @Param("tripId") Integer tripId);

    Integer getLastTripEventCategorySequenceByName(@Param("name") String name, @Param("id") Integer id, @Param("tripId") Integer tripId);

    Integer getTripEventSequenceByCategory(@Param("name") String name, @Param("category") String category, @Param("tripId") Integer tripId);

    void deleteTripEventsByTrip(Integer tripId);

    Integer hasCostDataByEventIds(String eventIds);
    /**
     * Duplicate all event templates into quotes.
     * @param trip - id required, eventTemplatesTobeImported is optional to specify templates to be imported
     */
    void insertTripsWithTemplate(Trip trip);

    void insertTripEventCostWithTemplate(Integer tripId);

    void insertTripEventNotifyWithTemplate(Integer tripId);

    TripEvent selectLastCompleteTripEvent(Integer tripId);

    TripEvent selectNextTripEvent(@Param("tripId")Integer tripId, @Param("categorySequence")Integer categorySequence, @Param("sequence")Integer sequence);
    
    List<TripEvent> selectEventWithActualDate(@Param("tripId")Integer tripId, @Param("customerNotify") String customerNofity, @Param("language") String lang);

    List<TripEventNotify> selectEventNotifies(Integer eventId);
    
    void insertEventNotify(TripEventNotify notify);
    
    void updateEventNotify(TripEventNotify notify);
    
    void deleteEventNotify(Integer notifyId);

    void deleteEventNotifiesWithEventId(Integer eventId);

    void deleteEventNotifiesWithTripId(@Param("tripId") Integer tripId, @Param("exculedEvent") Integer eventId);
    
    void updateEventCompleteInfo(TripEvent tripEvent);
}
