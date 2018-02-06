package ca.manitoulin.mtd.dao.maintenance;

import ca.manitoulin.mtd.dto.maintenance.TripEventTemplateCost;

import java.util.List;

public interface TripEventTemplateCostMapper {
    List<TripEventTemplateCost> selectEventTemplateCostByTemplateId(Integer templateId);

    TripEventTemplateCost selectEventTemplateCostById(Integer id);

    void insertEventTemplateCost(TripEventTemplateCost templateCost);

    void updateEventTemplateCost(TripEventTemplateCost templateCost);

    void deleteEventTemplateCost(Integer id);

    void deleteEventTemplateCostByTemplateId(Integer templateId);
}
