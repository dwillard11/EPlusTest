package ca.manitoulin.mtd.dao.tracing;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.tracing.PartnerChargeInfo;
import ca.manitoulin.mtd.dto.tracing.Probill;
import ca.manitoulin.mtd.dto.tracing.ProbillStoredProcedureParam;
import ca.manitoulin.mtd.test.AbstractDatabaseOperationTest;
import ca.manitoulin.mtd.util.json.JsonHelper;
import junit.framework.Assert;

public class ProbillMapperTest extends AbstractDatabaseOperationTest {
	static private Logger log = Logger.getLogger(ProbillMapperTest.class);
	
	@Autowired
	private ProbillMapper probillMapper;
	
	@Test
	public void testSelectPartnerChargeInfo() throws IOException{
		String probill = "91805427";
		String terminalId = "120";
		List<PartnerChargeInfo> list = probillMapper.selectPartnerChargeInfo(ContextConstants.SCHEMA_PRD, probill, terminalId);
		
		Assert.assertNotNull(list);
		
		for(PartnerChargeInfo p : list){
			System.out.println(JsonHelper.toJsonString(p));
		}
		
	}
	
	@Test
	public void testSelectProbillByNumber(){
		String probill = "22799033";
		
		Probill p = probillMapper.selectProbillByNumber(ContextConstants.SCHEMA_PRD, probill);
		Assert.assertNotNull(p);
	}

	@Test
	public void testCallManifestProc() throws ParseException {
		
		String probill = "22799033";
		String systemCode = "MAN";
		
		String returnCode = "1";
		ProbillStoredProcedureParam param = new ProbillStoredProcedureParam();
		param.setProbillNumber(probill);
		param.setReturnCode(returnCode);
		param.setSystemCode(systemCode);
		probillMapper.callManifestProc(param);
		Assert.assertEquals("OK", StringUtils.trim(param.getReturnCode()));
		

	}
	
	@Test
	public void testCallGenreateImage() throws ParseException {
		
		
		//final String probill = "22937797";
		//There are others can by choose: B16187AA.YNN,B16187AA.YNR,B16187AA.YNT,B16187AA.YNV,B16189AA.KQF,B16189AA.KRJ,
		//B16189AA.KRL,B16189AA.KRN,B16189AA.KRP,B16189AA.KRR
		//final String imageId = "B16187AA.YGL";
		
		
		final String probill = "92124800";
		final String imageId = "B15169AA.KUN";
		String systemCode = "MAN";
		

		probillMapper.callImageProc(imageId, probill, systemCode);

	}

}
