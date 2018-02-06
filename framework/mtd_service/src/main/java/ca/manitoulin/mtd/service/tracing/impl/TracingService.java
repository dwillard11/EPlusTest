package ca.manitoulin.mtd.service.tracing.impl;

import static ca.manitoulin.mtd.constant.ApplicationConstants.getConfig;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_ACCOUNT_TYPE;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_DATE_BEGIN;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_DATE_END;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_IMAGES;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_IMAGE_ID;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_PROBILL;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_PROBILLNO;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_TRACING_BOL;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_TRACING_PICKUPNUMBER;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_TRACING_PICKUPSTATUS;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_TRACING_PO;
import static ca.manitoulin.mtd.constant.ContextConstants.PARAM_TRACING_SHIPPER;
import static ca.manitoulin.mtd.dto.support.Attachment.toAFPFilename;
import static ca.manitoulin.mtd.ftp.util.FTPUtils.downloadFiles;
import static ca.manitoulin.mtd.util.json.JsonHelper.toObject;
import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ca.manitoulin.mtd.dao.tracing.TracingMapper;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.support.Attachment;
import ca.manitoulin.mtd.dto.tracing.PickupStatusDetail;
import ca.manitoulin.mtd.dto.tracing.Probill;
import ca.manitoulin.mtd.proxy.IWsClientProxy;
import ca.manitoulin.mtd.service.tracing.ITracingService;
import ca.manitoulin.mtd.service.tracing.ITracingWebService;

@Service
public class TracingService implements ITracingService {
	
	private static final Logger log = Logger.getLogger(TracingService.class);
		
	@Autowired
	private IWsClientProxy proxy;
	@Autowired
	private TracingMapper tracingMapper;
	

	@Override
	public Probill retrieveProbillByNumber(String probillNo, String accountType) throws Exception {
		RequestContext  requestContext = new RequestContext();
		requestContext.setServiceClass(ITracingWebService.class);
		requestContext.setMethodName("retrieveProbillByNumber");
		requestContext.addParameter(PARAM_PROBILLNO, probillNo);
		if (isNotBlank(accountType)) {
			requestContext.addParameter(PARAM_ACCOUNT_TYPE, accountType);
		} 
			
		ResultContext result = proxy.execute(requestContext);
		Probill probill = toObject(result.getResult(PARAM_PROBILL),Probill.class);
		if (null != probill) {
			asyncPrepareImages(probill.getProbillNumber());
		}
		return probill;
	}
	
	@Override
	public Probill retrieveProbillByBoL(String numberValue, String accountType) throws Exception {
		RequestContext  requestContext = new RequestContext();
		requestContext.setServiceClass(ITracingWebService.class);
		requestContext.setMethodName("retrieveProbillByBOL");
		requestContext.addParameter(PARAM_TRACING_BOL, numberValue);
		if (isNotBlank(accountType)) {
			requestContext.addParameter(PARAM_ACCOUNT_TYPE, accountType);
		} 
		ResultContext result = proxy.execute(requestContext);
		Probill probill = toObject(result.getResult(PARAM_PROBILL),Probill.class);
		if (null != probill) {
			asyncPrepareImages(probill.getProbillNumber());
		}
		return probill;
	}
	
	
	
	@Override
	public Probill retrieveProbillByShipper(String numberValue, String accountType) throws Exception {
		RequestContext  requestContext = new RequestContext();
		requestContext.setServiceClass(ITracingWebService.class);
		requestContext.setMethodName("retrieveProbillByShipper");
		requestContext.addParameter(PARAM_TRACING_SHIPPER, numberValue);
		if (isNotBlank(accountType)) {
			requestContext.addParameter(PARAM_ACCOUNT_TYPE, accountType);
		} 
		ResultContext result = proxy.execute(requestContext);
		Probill probill = toObject(result.getResult(PARAM_PROBILL),Probill.class);
		if (null != probill) {
			asyncPrepareImages(probill.getProbillNumber());
		}
		return probill;
	}
	@Override
	public Probill retrieveProbillByPO(String numberValue, String accountType) throws Exception {
		RequestContext  requestContext = new RequestContext();
		requestContext.setServiceClass(ITracingWebService.class);
		requestContext.setMethodName("retrieveProbillByPO");
		requestContext.addParameter(PARAM_TRACING_PO, numberValue);
		if (isNotBlank(accountType)) {
			requestContext.addParameter(PARAM_ACCOUNT_TYPE, accountType);
		} 
		ResultContext result = proxy.execute(requestContext);
		Probill probill = toObject(result.getResult(PARAM_PROBILL),Probill.class);
		if (null != probill) {
			asyncPrepareImages(probill.getProbillNumber());
		}
		return probill;
	}
	
	@Override
	public Probill retrieveProbillByPickupNumber(String pickupNumber) throws Exception {
		RequestContext  requestContext = new RequestContext();
		requestContext.setServiceClass(ITracingWebService.class);
		requestContext.setMethodName("retrieveProbillByPickupNumber");
		requestContext.addParameter(PARAM_TRACING_PICKUPNUMBER, pickupNumber);
		ResultContext result = proxy.execute(requestContext);
		Probill probill = toObject(result.getResult(PARAM_PROBILL),Probill.class);
		return probill;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PickupStatusDetail> retrieveStatusDetailsByPickupNumber(String pickupNumber) throws Exception {
		//enable the remote service when back-end ready
		RequestContext  requestContext = new RequestContext();
		requestContext.setServiceClass(ITracingWebService.class);
		requestContext.setMethodName("retrievePickUpStatusDetails");
		requestContext.addParameter(PARAM_TRACING_PICKUPNUMBER, pickupNumber);
		ResultContext result = proxy.execute(requestContext);
		List<PickupStatusDetail> details = (List<PickupStatusDetail>) result.getResult(PARAM_TRACING_PICKUPSTATUS);
		return details;
	}


	
	@SuppressWarnings("unchecked")
	@Override
	public List<Probill> retrieveProbillsByPickupDate(Date beginDate, Date endDate, String accountType) throws Exception {
		RequestContext  requestContext = new RequestContext();
		requestContext.setServiceClass(ITracingWebService.class);
		requestContext.setMethodName("retrieveProbillsByPickupDate");
		requestContext.addParameter(PARAM_DATE_BEGIN, beginDate);
		requestContext.addParameter(PARAM_DATE_END, endDate);
		if (isNotBlank(accountType)) {
			requestContext.addParameter(PARAM_ACCOUNT_TYPE, accountType);
		} 
		ResultContext result = proxy.execute(requestContext);
		List<Probill> probills = (List<Probill>) result.getResult(PARAM_PROBILL);
		return probills;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Probill> retrieveProbillsByDeliveryDate(Date beginDate, Date endDate, String accountType)throws Exception {
		RequestContext  requestContext = new RequestContext();
		requestContext.setServiceClass(ITracingWebService.class);
		requestContext.setMethodName("retrieveProbillsByDeliveryDate");
		requestContext.addParameter(PARAM_DATE_BEGIN, beginDate);
		requestContext.addParameter(PARAM_DATE_END, endDate);
		if (isNotBlank(accountType)) {
			requestContext.addParameter(PARAM_ACCOUNT_TYPE, accountType);
		} 
		ResultContext result = proxy.execute(requestContext);
		List<Probill> probills = (List<Probill>) result.getResult(PARAM_PROBILL);
		return probills;
	}
	
	@Override
	@Async
	public void asyncPrepareImages(String probillNumber) throws Exception {
		syncPrepareImages(probillNumber);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void syncPrepareImages(String probillNumber) throws Exception {
		
		long begin = currentTimeMillis();
		// Retrieve images with probill number;
		RequestContext  requestContext = new RequestContext();
		requestContext.setServiceClass(ITracingWebService.class);
		requestContext.setMethodName("retrieveImageIds");
		requestContext.addParameter(PARAM_PROBILLNO, probillNumber);
		ResultContext result = proxy.execute(requestContext);
		
		List<String> images = (List<String>)result.getResult(PARAM_IMAGES);
		if(images == null || images.isEmpty()){
			log.info("-> Download Image task finished: NO images for Probill# " + probillNumber);
			return;
		}else{
			log.debug("-> Prepare compare " + images.size() + " images for Probill# " + probillNumber);
		}
				
		// Retrieve downloaded images from mySQL;
		List<Attachment> downloadedImages = new ArrayList<Attachment>();
		//call mySQL mapper to retrieve downloaded Images;
		downloadedImages = tracingMapper.selectDownloadedImages(probillNumber);
		Set<String> downloadedImageIdSet = toImageIdSet(downloadedImages);
		
		// check probill folder exist or not, if not, create one
		String probillPath = getConfig("ftp.local.path")+probillNumber;
		File probillFolder = new File(probillPath);
		if(!probillFolder.exists()){
			probillFolder.mkdirs();
		}
		
		// Compare both, if image is not download yet, call remote generateImage();
		List<String> localPaths = new ArrayList<String>();
		List<String> remotePaths = new ArrayList<String>();
		Map<String,String> pathMap = new HashMap<String,String>();
		for(String imageIdTobeDownload : images){
			//if downloaded, skip
			if(downloadedImageIdSet.contains(imageIdTobeDownload)){
				log.debug("-> Image already download, SKIP. " + imageIdTobeDownload + " at Probill# " + probillNumber);
				continue;
			}
			//otherwise, call service to generate image
			RequestContext  reqContext = new RequestContext();
			reqContext.setServiceClass(ITracingWebService.class);
			reqContext.setMethodName("generateImage");
			reqContext.addParameter(PARAM_PROBILLNO, probillNumber);
			reqContext.addParameter(PARAM_IMAGE_ID, imageIdTobeDownload);
			//We don't know whether it succeed or Not
			proxy.execute(reqContext);
			
			//Generate remote path
			//set remote folder in config.properties
			String remotePath = getConfig("ftp.remote.path") + toAFPFilename(imageIdTobeDownload);
			
			// Generate local path of images
			String localPath = probillPath + "/" + toAFPFilename(imageIdTobeDownload);
			remotePaths.add(remotePath);
			localPaths.add(localPath);
			pathMap.put(localPath, imageIdTobeDownload);
		}
		
		if(remotePaths.isEmpty()){
			log.info("-> All images are already download! Probill# " + probillNumber);
			return;
		}
		
		//try to download the image from FTP;
		downloadFiles(getConfig("ftp.host"), parseInt(getConfig("ftp.port")), remotePaths, localPaths);
		log.info(localPaths.size() + " images are successfully download! Probill# " + probillNumber);
		
		// update mySQL download information;
		for(String localFile : localPaths){
			File downloadedFile = new File(localFile);
			if(downloadedFile.exists()){
				// only save to MySQL DB when file detected.
				String saveImageId = pathMap.get(localFile);
				tracingMapper.insertImagesRecords(probillNumber, saveImageId);
				log.info("-> Image info saved in local DB: " + localFile);
			}
		}
		
		log.info("-> Images of Probill# " + probillNumber + " saved in local complete: " + (currentTimeMillis() - begin) / 1000 + " sec");
	}
	
	

	private Set<String> toImageIdSet(List<Attachment> images){
		Set<String> imageIds = new HashSet<String>();
		
		if(images == null || 0 == images.size()) return imageIds ;
		
		
		for(Attachment image : images){
			imageIds.add(trimToEmpty(image.getId()));
		}
		
		return imageIds;
	}
}
