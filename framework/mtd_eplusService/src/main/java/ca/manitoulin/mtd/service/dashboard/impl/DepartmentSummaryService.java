package ca.manitoulin.mtd.service.dashboard.impl;

import ca.manitoulin.mtd.dao.dashboard.DepartmentSummaryMapper;
import ca.manitoulin.mtd.dto.dashboard.ShipmentSummary;
import ca.manitoulin.mtd.dto.dashboard.ShipmentSummaryGrid;
import ca.manitoulin.mtd.dto.security.Role;
import ca.manitoulin.mtd.service.dashboard.IDepartmentSummaryService;
import ca.manitoulin.mtd.util.ApplicationSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DepartmentSummaryService implements IDepartmentSummaryService {

	private static Logger logger = Logger.getLogger(DepartmentSummaryService.class);
	
	@Autowired
	private DepartmentSummaryMapper departmentSummaryMapper;

	@Override
	public ShipmentSummary retrieveShipmentForDashboard(Integer departmentId, String startDate, String endDate) throws Exception {
		return departmentSummaryMapper.retrieveShipmentForDashboard(departmentId, startDate, endDate);
	}

	@Override
	public List<ShipmentSummaryGrid> retrieveShipmentForDashboardWithCurrency(Integer departmentId, String startDate, String endDate) throws Exception {
		return departmentSummaryMapper.retrieveShipmentForDashboardWithCurrency(departmentId, startDate, endDate);
	}

	@Override
	public ShipmentSummary retrieveQuoteForDashboard(Integer departmentId, String startDate, String endDate) throws Exception {
		return departmentSummaryMapper.retrieveQuoteForDashboard(departmentId, startDate, endDate);
	}

	@Override
	public List<Role> retrieveOnlineUserCount(Integer departmentId, String activeDate) throws Exception {
		return departmentSummaryMapper.retrieveOnlineUserCount(departmentId, activeDate);
	}

	@Override
	public String retrieveCriticalShipment(Integer departmentId) throws Exception {
		Date tempDate = departmentSummaryMapper.retrieveCriticalShipment(departmentId);
		String criticalShipmentFlag = "";
		if (tempDate!=null)
		{
			Date now = new Date();
			if (tempDate.compareTo(now) < 1)
				criticalShipmentFlag = "R";
			else {
				long diffHours = 0;
				diffHours = (tempDate.getTime() - now.getTime()) / (60 * 60 * 1000);
				if (diffHours >= 2)
					criticalShipmentFlag = "G";
				else if (diffHours >= 0 && diffHours < 2)
					criticalShipmentFlag = "Y";
				else
					criticalShipmentFlag = "R";
			}
		}
		return criticalShipmentFlag;
	}

	@Override
	public ShipmentSummary retrieveMessageQueue(Integer departmentId) throws Exception {
		return departmentSummaryMapper.retrieveMessageQueue(departmentId);
	}
}
