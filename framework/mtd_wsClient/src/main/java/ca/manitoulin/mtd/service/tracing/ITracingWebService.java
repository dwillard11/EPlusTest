package ca.manitoulin.mtd.service.tracing;

import java.util.List;

import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.tracing.ManifestInfo;
import ca.manitoulin.mtd.dto.tracing.Probill;
import ca.manitoulin.mtd.exception.BusinessException;

public interface ITracingWebService {
	
	ResultContext retrieveProbillByNumber(RequestContext requestContext);
	
	
	ResultContext retrieveProbillByBOL(RequestContext requestContext);
	
	
	ResultContext retrieveProbillByShipper(RequestContext requestContext);
	
	
	ResultContext retrieveProbillByPO(RequestContext requestContext);
	
	
	ResultContext retrieveProbillByPickupNumber(RequestContext requestContext);
	
	
	ResultContext retrievePickUpStatusDetails(RequestContext requestContext);
	
	/**
	 * Tracing probills by pickup date range
	 * @param requestContext -Set PARAM_DATE_BEGIN & PARAM_DATE_END in params (java.util.Date).
	 * If anyone is null, it will be set to current date
	 * @return List of Probill. from ContextConstants.PARAM_PROBILL
	 * @throws BusinessException 
	 */
	ResultContext retrieveProbillsByPickupDate(RequestContext requestContext) throws BusinessException;
	/**
	 * Tracing probills by delivery date range
	 * @param requestContext -Set PARAM_DATE_BEGIN & PARAM_DATE_END in params (java.util.Date).
	 * If anyone is null, it will be set to current date
	 * @return List of Probill. from ContextConstants.PARAM_PROBILL
	 */	
	ResultContext retrieveProbillsByDeliveryDate(RequestContext requestContext)throws BusinessException;

	ResultContext generateImage(RequestContext requestContext);
	
	List<ManifestInfo> retrieveManifestInfo(Probill probill, String systemCode, String schema);
	
	/**
	 * Query image name by probill number
	 * @param requestContext - set probill in ContextConstants.PARAM_PROBILLNO
	 * @return List of Attachment, from ContextConstants.PARAM_IMAGES
	 */
	ResultContext retrieveImageInfo(RequestContext requestContext);
	
	ResultContext retrieveImageIds(RequestContext requestContext);
	
	ResultContext retrieveProbillNumberByPage(RequestContext requestContext);
}
