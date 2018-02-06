package ca.manitoulin.mtd.dao.tracing;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.manitoulin.mtd.dto.Page;
import ca.manitoulin.mtd.dto.support.Attachment;
import ca.manitoulin.mtd.dto.tracing.ManifestInfo;
import ca.manitoulin.mtd.dto.tracing.PackingPiece;
import ca.manitoulin.mtd.dto.tracing.PartnerChargeInfo;
import ca.manitoulin.mtd.dto.tracing.PickupStatusDetail;
import ca.manitoulin.mtd.dto.tracing.Probill;
import ca.manitoulin.mtd.dto.tracing.ProbillStoredProcedureParam;

public interface ProbillMapper {

	Probill selectProbillByNumber(@Param("schema") String schema, @Param("probillNumber") String probillNumber);

	//deprecated, move into retrieval SQL - 2016/8/11
	//Probill selectProbillExtByNumber(@Param("schema") String schema, @Param("probillNumber") String probillNumber);

	List<PackingPiece> selectPiecesByProbill(@Param("schema") String schema,
			@Param("probillNumber") String probillNumber);
	
	List<String> selectReceivedByProbill(@Param("schema") String schema,
			@Param("probillNumber") String probillNumber);
	
	Probill selectProbillByBOL(@Param("schema") String schema, @Param("bol") String bol, @Param("accounts") List<String> accounts);
	
	Probill selectProbillByShipper(@Param("schema") String schema, @Param("shipper") String shipper);
	
	Probill selectProbillByPO(@Param("schema") String schema, @Param("po") String po);
	
	String selectProbillNumberByPickupNumber(@Param("schema") String schema, @Param("pickupNumber") String pickupNumber);

	List<PickupStatusDetail> selectPickupStatusDetailByPickupNumber(@Param("schema") String schema, @Param("pickupNumber") String pickupNumber);
	
	
	List<Probill> selectProbillByPickupDate(@Param("schema") String schema, @Param("accounts") List<String> accounts,
			@Param("dateBegin") Integer dateBeginInInteger, @Param("dateEnd") Integer dateEndInInteger);

	List<Probill> selectProbillByDeliveryDate(@Param("schema") String schema, @Param("accounts") List<String> accounts,
			@Param("dateBegin") Integer dateBeginInInteger, @Param("dateEnd") Integer dateEndInInteger);

	List<Probill> selectPartnerProbillByPickupDate(@Param("schema") String schema,
			@Param("accounts") List<String> accounts, @Param("dateBegin") Integer dateBeginInInteger,
			@Param("dateEnd") Integer dateEndInInteger);

	List<Probill> selectPartnerProbillByDeliveryDate(@Param("schema") String schema,
			@Param("accounts") List<String> accounts, @Param("dateBegin") Integer dateBeginInInteger,
			@Param("dateEnd") Integer dateEndInInteger);
	
	List<Attachment> selectImages(@Param("probillNumber") String probillNumber, @Param("systemCode")String systemCode);
	
	void callManifestProc(ProbillStoredProcedureParam param);
	
	
	List<ManifestInfo> selectManifestByProbill(@Param("schema") String schema,
			@Param("probillNumber") String probillNumber);
	
	void callImageProc(@Param("imageId") String imageId, 
			@Param("probillNumber")String probillNumber,
			@Param("systemCode") String systemCode);
	
	List<String> selectProbillNumberByPickupDate(Page<String> page);
	

	List<PartnerChargeInfo> selectPartnerChargeInfo(@Param("schema") String schema,
			@Param("probillNumber") String probillNumber,
			@Param("terminalId") String terminalId);
	
	List<String> selectPartnerShowIndicator(@Param("schema") String schema,
			@Param("userAccount") String probillNumber);
}
