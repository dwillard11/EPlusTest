package ca.manitoulin.mtd.dao.maintenance;

import ca.manitoulin.mtd.dto.maintenance.TripEventTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TripEventTemplateMapper {
	
    List<TripEventTemplate> selectTripEventTemplateListByType(@Param("tripType") String tripType, @Param("name") String name, @Param("language") String language);

    List<TripEventTemplate> selectTripEventTemplatesForTripTemplate(@Param("tripType") String tripType);

    List<TripEventTemplate> selectTripEventTemplates(@Param("tripType") String tripType, @Param("name") String name);

    TripEventTemplate selectTripEventTemplateById(@Param("id") Integer id);

    void insertTripEventTemplate(TripEventTemplate tripEventTemplate);

    void updateTripEventTemplate(TripEventTemplate tripEventTemplate);

    void updateTripEventTemplateCategorySequence(TripEventTemplate tripEventTemplate);

    void updateTripEventTemplateSequence(TripEventTemplate tripEventTemplate);

    void deleteTripEventTemplateByCategory(@Param("tripType") String tripType, @Param("name") String name);

    void deleteTripEventTemplate(@Param("id") Integer id);

    Integer getCategorySequenceByCategory(@Param("name") String name, @Param("category") String category);

    Integer getLastCategorySequenceByName(@Param("name") String name, @Param("id") Integer id);

    Integer getItemSequenceByCategory(@Param("name") String name, @Param("category") String category);
}
