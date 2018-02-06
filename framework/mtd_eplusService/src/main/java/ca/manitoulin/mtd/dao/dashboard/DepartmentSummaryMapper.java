package ca.manitoulin.mtd.dao.dashboard;

import ca.manitoulin.mtd.dto.dashboard.ShipmentSummary;
import ca.manitoulin.mtd.dto.dashboard.ShipmentSummaryGrid;
import ca.manitoulin.mtd.dto.security.Role;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface DepartmentSummaryMapper {
    ShipmentSummary retrieveShipmentForDashboard(@Param("departmentId") Integer departmentId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    List<ShipmentSummaryGrid> retrieveShipmentForDashboardWithCurrency(@Param("departmentId") Integer departmentId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    ShipmentSummary retrieveQuoteForDashboard(@Param("departmentId") Integer departmentId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    List<Role> retrieveOnlineUserCount(@Param("departmentId") Integer departmentId, @Param("activeDate") String activeDate);
    Date retrieveCriticalShipment(@Param("departmentId") Integer departmentId);
    ShipmentSummary retrieveMessageQueue(@Param("departmentId") Integer departmentId);
}
