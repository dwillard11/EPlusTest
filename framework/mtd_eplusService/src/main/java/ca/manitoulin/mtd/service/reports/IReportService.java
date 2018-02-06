package ca.manitoulin.mtd.service.reports;

import ca.manitoulin.mtd.dto.customer.CustomerLocation;
import ca.manitoulin.mtd.dto.operationconsole.Trip;

import java.util.List;

public interface IReportService {
	List<CustomerLocation> getTSAAuthorizedAgentByAirportReporting(String tsaAirport, String address, String quickName, String searchKey);

	List<Trip> getTripReport(List<Integer> departmentIdList, String tripNum, String pickupDate, String deliveryDate, String shipperName, String consigneeName, String podName, String searchKey);

	List<Trip> getCurrencyReportByDepartment(List<Integer> departmentIdList, String startDate, String endDate, String clientName, String shipperName, String consigneeName, String tripNum, String tripStatus, String currency, String searchKey);

	List<Trip> getSaleReporting(List<Integer> departmentIdList, String currency, String searchKey);

	List<Trip> getServiceReporting(List<Integer> departmentIdList, String consigneeName, String currency, String searchKey);

    List<Trip> getCostReporting(List<Integer> departmentIdList, String tripNum, String startDate, String endDate, String chargeCode, String chargeDetail, String currency, String searchKey);
}
