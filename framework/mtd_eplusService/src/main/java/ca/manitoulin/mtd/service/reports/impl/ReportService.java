package ca.manitoulin.mtd.service.reports.impl;

import ca.manitoulin.mtd.dto.customer.CustomerLocation;
import ca.manitoulin.mtd.dto.operationconsole.*;
import ca.manitoulin.mtd.dao.reports.ReportMapper;
import ca.manitoulin.mtd.service.reports.IReportService;
import ca.manitoulin.mtd.util.ApplicationSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReportService implements IReportService {
	private static final Logger log = Logger.getLogger(ReportService.class);

	@Autowired
	private ReportMapper reportMapper;

	@Override
	public List<CustomerLocation> getTSAAuthorizedAgentByAirportReporting(String tsaAirport, String address, String quickName, String searchKey) {
		List<CustomerLocation> locations = reportMapper.getTSAAuthorizedAgentByAirportReporting(tsaAirport, address, quickName, searchKey);
		return locations;
	}

	@Override
	public List<Trip> getTripReport(List<Integer> departmentIdList, String tripNum, String pickupDate, String deliveryDate, String shipperName, String consigneeName, String podName, String searchKey) {
		List<Trip> trips = reportMapper.getTripReport(departmentIdList, tripNum, pickupDate, deliveryDate, shipperName, consigneeName, podName, searchKey);
		return trips;
	}

	@Override
	public List<Trip> getCurrencyReportByDepartment(List<Integer> departmentIdList, String startDate, String endDate, String clientName, String shipperName, String consigneeName, String tripNum, String tripStatus, String currency, String searchKey) {
		List<Trip> trips = reportMapper.getCurrencyReportByDepartment(departmentIdList, startDate, endDate, clientName, shipperName, consigneeName, tripNum, tripStatus, currency, searchKey, ApplicationSession.get().getReferLanguage());
		return trips;
	}

	@Override
	public List<Trip> getSaleReporting(List<Integer> departmentIdList, String currency, String searchKey) {
		List<Trip> trips = reportMapper.getSaleReporting(departmentIdList, currency, searchKey);
		return trips;
	}

	@Override
	public List<Trip> getServiceReporting(List<Integer> departmentIdList, String consigneeName, String currency, String searchKey) {
		List<Trip> trips = reportMapper.getServiceReporting(departmentIdList, consigneeName, currency, searchKey, ApplicationSession.get().getReferLanguage());
		return trips;
	}

	@Override
	public List<Trip> getCostReporting(List<Integer> departmentIdList, String tripNum, String startDate, String endDate, String chargeCode, String chargeDetail, String currency, String searchKey) {
		List<Trip> trips = reportMapper.getCostReporting(departmentIdList, tripNum, startDate, endDate, chargeCode, chargeDetail, currency, searchKey);
		return trips;
	}
}
