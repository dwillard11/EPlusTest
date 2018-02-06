package ca.manitoulin.mtd.service.dashboard;

import ca.manitoulin.mtd.dto.dashboard.ShipmentSummary;
import ca.manitoulin.mtd.dto.dashboard.ShipmentSummaryGrid;
import ca.manitoulin.mtd.dto.security.Role;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface IDepartmentSummaryService {
    ShipmentSummary retrieveShipmentForDashboard(Integer departmentId, String startDate, String endDate) throws Exception;
    List<ShipmentSummaryGrid> retrieveShipmentForDashboardWithCurrency(Integer departmentId, String startDate, String endDate) throws Exception;
    ShipmentSummary retrieveQuoteForDashboard(Integer departmentId, String startDate, String endDate) throws Exception;
    List<Role> retrieveOnlineUserCount(Integer departmentId, String activeDate) throws Exception;
    String retrieveCriticalShipment(Integer departmentId) throws Exception;
    ShipmentSummary retrieveMessageQueue(Integer departmentId) throws Exception;
}
