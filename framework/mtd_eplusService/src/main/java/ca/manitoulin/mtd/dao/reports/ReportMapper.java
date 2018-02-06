package ca.manitoulin.mtd.dao.reports;

import ca.manitoulin.mtd.dto.customer.CustomerLocation;
import ca.manitoulin.mtd.dto.operationconsole.Trip;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReportMapper {
	List<CustomerLocation> getTSAAuthorizedAgentByAirportReporting(@Param("tsaAirport")String tsaAirport,
																 @Param("address")String address,
																 @Param("quickName")String quickName,
																 @Param("searchKey") String searchKey);

	List<Trip> getTripReport(
            @Param("departmentIdList") List<Integer> departmentIdList,
            @Param("tripNum") String tripNum,
			@Param("pickupDate") String pickupDate,
			@Param("deliveryDate") String deliveryDate,
			@Param("shipperName") String shipperName,
			@Param("consigneeName") String consigneeName,
			@Param("podName") String podName,
            @Param("searchKey") String searchKey);

	List<Trip> getCurrencyReportByDepartment(
			@Param("departmentIdList") List<Integer> departmentIdList,
			@Param("startDate") String startDate,
			@Param("endDate") String endDate,
			@Param("clientName") String clientName,
			@Param("shipperName") String shipperName,
			@Param("consigneeName") String consigneeName,
			@Param("tripNum") String tripNum,
			@Param("tripStatus") String tripStatus,
			@Param("currency") String currency,
			@Param("searchKey") String searchKey,
			@Param("language") String lang);

	List<Trip> getSaleReporting(
			@Param("departmentIdList") List<Integer> departmentIdList,
			@Param("currency") String currency,
			@Param("searchKey") String searchKey);

	List<Trip> getServiceReporting(
			@Param("departmentIdList") List<Integer> departmentIdList,
			@Param("consigneeName") String consigneeName,
			@Param("currency") String currency,
			@Param("searchKey") String searchKey,
			@Param("language") String lang);

	List<Trip> getCostReporting(
			@Param("departmentIdList") List<Integer> departmentIdList,
			@Param("tripNum") String tripNum,
			@Param("startDate") String startDate,
			@Param("endDate") String endDate,
			@Param("chargeCode") String chargeCode,
			@Param("chargeDetail") String chargeDetail,
			@Param("currency") String currency,
			@Param("searchKey") String searchKey);
}
