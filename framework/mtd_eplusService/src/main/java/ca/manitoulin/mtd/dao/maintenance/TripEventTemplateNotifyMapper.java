package ca.manitoulin.mtd.dao.maintenance;

import ca.manitoulin.mtd.dto.maintenance.TripEventTemplateNotify;

import java.util.List;

public interface TripEventTemplateNotifyMapper {
    List<TripEventTemplateNotify> selectEventTemplateNotifyByTemplateId(Integer templateId);

    TripEventTemplateNotify selectEventTemplateNotifyById(Integer id);

    void insertEventTemplateNotify(TripEventTemplateNotify templateNotify);

    void updateEventTemplateNotify(TripEventTemplateNotify templateNotify);

    void deleteEventTemplateNotify(Integer id);

    void deleteEventTemplateNotifyByTemplateId(Integer templateId);

    void deleteAllOtherEventTemplateNotifyForCopy(Integer templateId);

    void copyEventTemplateNotifyByTemplateId(Integer templateId);
}
