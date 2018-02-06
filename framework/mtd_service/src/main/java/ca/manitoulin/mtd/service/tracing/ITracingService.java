package ca.manitoulin.mtd.service.tracing;

import java.util.Date;
import java.util.List;

import ca.manitoulin.mtd.dto.tracing.PickupStatusDetail;
import ca.manitoulin.mtd.dto.tracing.Probill;

public interface ITracingService {

	
	Probill retrieveProbillByNumber(String probillNo, String accountType) throws Exception;

	Probill retrieveProbillByBoL(String numberValue, String accountType) throws Exception;

	Probill retrieveProbillByShipper(String numberValue, String accountType) throws Exception;

	Probill retrieveProbillByPO(String numberValue, String accountType) throws Exception;
	
	Probill retrieveProbillByPickupNumber(String pickupNumber) throws Exception;
	
	List<PickupStatusDetail> retrieveStatusDetailsByPickupNumber(String pickupNumber) throws Exception;
	
	List<Probill> retrieveProbillsByPickupDate(Date beginDate, Date endDate,String accountType) throws Exception;

	List<Probill> retrieveProbillsByDeliveryDate(Date beginDate, Date endDate,String accountType) throws Exception;
	
	void asyncPrepareImages(String probillNumber) throws Exception;
	
	void syncPrepareImages(String probillNumber) throws Exception;

	
}
