package ca.manitoulin.mtd.dao.operationconsole;

import java.util.List;

import ca.manitoulin.mtd.dto.operationconsole.TripNote;

public interface TripNoteMapper {
	
    List<TripNote> selectTripNotes(Integer tripId);

    void insertTripNote(TripNote note);

    void updateTripNote(TripNote note);

    void deleteTripNote(Integer id);
}
