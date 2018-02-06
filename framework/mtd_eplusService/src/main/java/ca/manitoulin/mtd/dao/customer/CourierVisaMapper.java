package ca.manitoulin.mtd.dao.customer;

import ca.manitoulin.mtd.dto.customer.CourierVisa;

import java.util.List;

public interface CourierVisaMapper {
    List<CourierVisa> selectCourierVisaByContactId(Integer contactId);

    CourierVisa selectCourierVisaById(Integer id);

    void insertCourierVisa(CourierVisa courierVisa);

    void updateCourierVisa(CourierVisa courierVisa);

    void deleteCourierVisa(Integer id);

    void deleteCourierVisaByContactId(Integer contactId);
}
