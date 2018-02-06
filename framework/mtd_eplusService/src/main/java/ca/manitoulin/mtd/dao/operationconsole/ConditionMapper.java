package ca.manitoulin.mtd.dao.operationconsole;

import java.util.List;

import ca.manitoulin.mtd.dto.operationconsole.Condition;
import ca.manitoulin.mtd.dto.operationconsole.Trip;
import org.apache.ibatis.annotations.Param;

public interface ConditionMapper {
	
    List<Condition> selectConditions(Integer tripId);

    Condition selectConditionById(Integer id);

    void insertCondition(Condition condition);

    void deleteCondition(Integer id);

    void updateCondition(Condition condition);

    void updateConditionForQuotePdf(Condition condition);

    void updateConditionCategorySequence(Condition condition);

    void updateConditionSequence(Condition condition);

    Integer getConditionCategorySequenceByCategory(@Param("name") String name, @Param("category") String category, @Param("tripId") Integer tripId);

    Integer getLastConditionCategorySequenceByName(@Param("name") String name, @Param("id") Integer id, @Param("tripId") Integer tripId);

    void deleteConditionsByTrip(Integer tripId);
    
    /**
     * Duplicate all T%C templates into quotes.
     * @param trip - id required, quoteTemplatesTobeImported is optional to specify templates to be imported
     */
    void insertConditionsWithTemplate(Trip trip);
    
}
