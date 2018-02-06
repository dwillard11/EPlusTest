package ca.manitoulin.mtd.dao.operationconsole;

import ca.manitoulin.mtd.dto.operationconsole.TripCost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TripCostMapper {
	
    List<TripCost> selectTripCosts(@Param("tripId") Integer tripId, @Param("showAll") Boolean showAll);

    List<TripCost> selectTripCostsByEventId(@Param("eventId") Integer eventId, @Param("showAll") Boolean showAll);
    TripCost selectTripCostById(Integer id);

    void insertTripCost(TripCost TripCost);

    void deleteTripCost(Integer id);

    void updateTripCost(TripCost TripCost);

    void deleteTripCostsByTrip(Integer tripId);
    
    void deleteTripEventCostsByTrip(Integer tripId);

    void deleteTripCostsByEvent(Integer eventId);
    
    void updateLinkedEntity(@Param("costId") Integer costId, @Param("entity") Integer entity);

}
