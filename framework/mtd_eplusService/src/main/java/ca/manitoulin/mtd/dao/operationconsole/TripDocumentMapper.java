package ca.manitoulin.mtd.dao.operationconsole;

import ca.manitoulin.mtd.dto.operationconsole.TripDocument;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TripDocumentMapper {

    List<TripDocument> selectTripDocuments(@Param("id")Integer tripId, @Param("noEmail") String noEmail, @Param("language") String language);

    void insertTripDocument(TripDocument doc);
    
    TripDocument selectTripDocument(@Param("id")Integer id, @Param("language") String language);
    
    
    List<TripDocument> selectTripDocumentsByType(@Param("tripid")Integer tripId, @Param("type") String type, @Param("language") String language);
    
    void deleteTripDocument(Integer id);
    
    List<TripDocument> selectTripDocumentsByEmail(@Param("id")Long emailId, @Param("language") String language);
    
    void updateTripDocument(TripDocument doc);
    
    void insertDocumentFromUploaded(TripDocument doc);
    
    List<TripDocument> selectTripDocumentsByRefName(String ref);
}
