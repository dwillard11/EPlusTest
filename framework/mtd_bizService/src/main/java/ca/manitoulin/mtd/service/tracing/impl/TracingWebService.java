package ca.manitoulin.mtd.service.tracing.impl;

import static ca.manitoulin.mtd.util.json.JsonHelper.toObject;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ca.manitoulin.mtd.code.DocumentType;
import ca.manitoulin.mtd.code.UserAccountType;
import ca.manitoulin.mtd.code.UserType;
import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dao.misc.CityMapper;
import ca.manitoulin.mtd.dao.misc.DeliveryCodeMapper;
import ca.manitoulin.mtd.dao.tracing.ProbillMapper;
import ca.manitoulin.mtd.dto.Page;
import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.security.Account;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.dto.support.Attachment;
import ca.manitoulin.mtd.dto.tracing.ManifestInfo;
import ca.manitoulin.mtd.dto.tracing.PackingPiece;
import ca.manitoulin.mtd.dto.tracing.PartnerChargeInfo;
import ca.manitoulin.mtd.dto.tracing.PickupStatusDetail;
import ca.manitoulin.mtd.dto.tracing.Probill;
import ca.manitoulin.mtd.dto.tracing.ProbillStoredProcedureParam;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.service.tracing.ITracingWebService;
import ca.manitoulin.mtd.util.DateUtil;
import ca.manitoulin.mtd.util.json.JsonHelper;

@Service
public class TracingWebService implements ITracingWebService {

	private static final Logger log = Logger.getLogger(TracingWebService.class);
	
	@Autowired
	private ProbillMapper probillMapper;
	@Autowired
	private CityMapper cityMapper;
	@Autowired
	private DeliveryCodeMapper deliveryCodeMapper;
	
	@Override
	public ResultContext retrieveProbillByNumber(RequestContext requestContext) {
		
		ResultContext rlt = new ResultContext();
		
		String probillNumber = (String) requestContext.getParameter(ContextConstants.PARAM_PROBILLNO);
		String schema = requestContext.getUserProfile().getDbSchema();
		
		Probill probill = probillMapper.selectProbillByNumber(schema, probillNumber);
		
		if(probill == null){
			log.info("PROBILL IS NOT FOUND :" + probillNumber);
			return rlt;
		}
		
		probill = retrieveExtendData(probill, requestContext.getUserProfile());
		
		probill = retrieveProbillSections(probill, requestContext.getUserProfile());
		
		// The follow checking only be active when tracing by probill number:
		if("US".equals(StringUtils.trimToEmpty(probill.getConsigneeCountry()))){
			if(StringUtils.isEmpty(probill.getEtaDate())){
				probill.setCalculatedETA("NA");
			}else if(probill.getEtaDate().equals(probill.getProbillETA())){
				probill.setCalculatedETA("NA");
			}
			
		}
		
		rlt.addResult(ContextConstants.PARAM_PROBILL, probill);

		return rlt;
	}
	
	protected Probill retrieveExtendData(Probill probill, SecureUser secureUser){
		
		String probillNumber = probill.getProbillNumber();
		String schema = secureUser.getDbSchema();
		
		//If transferred to a carrier
		if(StringUtils.trimToNull(probill.getTransferTo()) != null){
			//If delivery date entered, show:Delivered
			if(!StringUtils.isEmpty(probill.getFormattedDeliveryDate())){
				probill.setDeliveryCodeDesp("Delivered");
			}else{
				//If no delivery date entered, show:  	Delivery Code: (blank)
				probill.setDeliveryCodeDesp(null);
			}
		}
		
		// calculate ETA
		// if delivery date is null, show ETA( controlled in the front page)
		String oeta = DateUtil.displayDBDate(probill.getEtaDate());
		String carrierEta =  DateUtil.displayDBDate(probill.getToCarrierETA());
		String probillEta = DateUtil.displayDBDate(probill.getProbillETA());
		log.debug("-> OETA :" + oeta);
		log.debug("-> a carrier ETA date :" + carrierEta);
		log.debug("-> ETA date :" + probillEta);
		if( oeta != null){
			probill.setCalculatedETA(oeta);
		}else if(StringUtils.trimToNull(probill.getTransferTo()) != null){
			if(carrierEta != null){
				probill.setCalculatedETA(carrierEta);
			}else{
				probill.setCalculatedETA("Please call Customer Service");
			}
		}else{
			probill.setCalculatedETA(probillEta);
		}
		
		//calculate rescheduled date and rescheduled reason
		Date rescheduledDate = DateUtil.toDate(probill.getRescheduledDate(), DateUtil.DB_DATE_FORMAT);
		Date appointmentDate = DateUtil.toDate(probill.getDeliveryApptDate(), DateUtil.DB_DATE_FORMAT);
		log.debug("-> rescheduledDate :" + probill.getRescheduledDate());
		log.debug("-> appointmentDate :" + probill.getDeliveryApptDate());
		if(rescheduledDate != null 
				&& (appointmentDate == null || rescheduledDate.after(appointmentDate))){
			//If rescheduled date (FRP103. INEXT1) is greater than Delivery Appointment Date (FRL00142.FHDADT), 
			//show Rescheduled Date (FRP103. INEXT1) and Reason (WWW.DELCODES. DELDESC).
			String rescheduleResonDesc = deliveryCodeMapper.selectCodeDescription(probill.getRescheduledReasonCode());
			String rescheduleMessage = MessageFormat.format("{0} - {1}", 
					DateUtil.toDateString(rescheduledDate, DateUtil.LONG_DATE_FORMAT), 
					rescheduleResonDesc);
			probill.setRescheduledReason(rescheduleMessage);
		}else{
			if(probill.getDeliveryApptDate() != null){
				probill.setDeliveryAppointment(StringUtils.join(new String[]{probill.getFormattedDeliveryApptDate(), probill.getFormattedDeliveryApptTime()}, " at "));
			}else{
				probill.setDeliveryAppointment("NA");
			}
		}
		
		//calculate delivery special tips(Make sure this logic is after ETA calculation)
		Date calculatedETA = DateUtil.toDate(probill.getCalculatedETA(), DateUtil.LONG_DATE_FORMAT);
		
		if(probill.getDeliveryDate() == null && calculatedETA != null && calculatedETA.before(new Date())){
			probill.setDeliveryAppointment("NA");
			StringBuilder sb = new StringBuilder("Delivery information is not available at this time.<br/>");
			//TODO customize the link of Customer Service
			sb.append("Please contact <a href='#'>Customer Service</a> for more information.");
			probill.setDeliverySpecialTips(sb.toString());
		}
		
		//TODO calculate status section
		
		//Received by
		assimbleReceivedBy(probill, secureUser);
		log.debug("Received by info assimbled -> " + probillNumber);
		
		return probill;
	}
	
	protected Probill retrieveProbillSections(Probill probill, SecureUser secureUser) {
		
		String probillNumber = probill.getProbillNumber();
		String schema = secureUser.getDbSchema();
		String sysCode = secureUser.getSystemId();
		Boolean isPartnerRequest = UserType.PARTNER.toString().equals(secureUser.getType());
		// Pieces: Will skip when The current user type is PARTNER
		if(isPartnerRequest == false){
			List<PackingPiece> pieces = probillMapper.selectPiecesByProbill(schema, probillNumber);
			probill.setPiecesList(pieces);
			log.debug("Pieces info assimbled -> " + probillNumber);
		}else{
			log.debug("Skip pieces retrieval, user type: " + secureUser.getType());
		}
		
		// Manifest table
		List<ManifestInfo> manifestList = retrieveManifestInfo(probill, sysCode, schema);
		probill.setManifestList(manifestList);
		log.debug("Manifest info assimbled -> " + probillNumber);

		// Images
		List<Attachment> images = retrieveImages(probillNumber, sysCode);
		probill.setImages(images);
		log.debug("Image info assimbled -> " + probillNumber);
		
		// partner section
		DecimalFormat percentageFormat = new DecimalFormat("#0.00%");
		DecimalFormat moneyFormat = new DecimalFormat("$#0.00");
		if(secureUser.getAccount().equals(StringUtils.trimToEmpty(probill.getTransferedFromCarrier()))){
			probill.setInterlineFrom(percentageFormat.format(probill.getFromCarrierDiscountPercentage() ));
			String revenue = MessageFormat.format("{0} for {1}", 
					moneyFormat.format(probill.getFromCarrierRevenue()), secureUser.getAccount());
			probill.setRevenue(revenue);
		}else if (secureUser.getAccount().equals(StringUtils.trimToEmpty(probill.getTransferedToCarrier()))){
			probill.setInterlineTo(percentageFormat.format(probill.getToCarrierDiscountPercentage()));
			String revenue = MessageFormat.format("{0} for {1}", 
					moneyFormat.format(probill.getToCarrierRevenue()), secureUser.getAccount());
			probill.setRevenue(revenue);
		}
		if(probill.getDiscountAmount() != null && probill.getDiscountPercentage() != null){
			String discount = MessageFormat.format("{0} {1}", 
					percentageFormat.format(probill.getDiscountPercentage()),
					moneyFormat.format(probill.getDiscountAmount()));
			probill.setDiscount(discount);
		}
		// Show partner only when 1) current user is a Partner
		if(isPartnerRequest){
			//2)And, When "TBDAK1" has value
			List<String> indicator = probillMapper.selectPartnerShowIndicator(schema, secureUser.getAccount());
			if(indicator != null && !indicator.isEmpty()){
				List<PartnerChargeInfo> partnerList = probillMapper.selectPartnerChargeInfo(schema, probillNumber, probill.getTerminalId());
				probill.setPartnerList(partnerList);
				log.debug("Partner charge info assimbled -> " + probillNumber);
			}else{
				log.debug("Skip partner retrieval, selectPartnerChargeInfo() returns null ");
			}
		}else{
			log.debug("Skip partner retrieval, user type: " + secureUser.getType());
		}
		
		
		return probill;
	}
	
	public List<Attachment> retrieveImages(String probillNumber, String systemCode){
		List<Attachment> images = probillMapper.selectImages(probillNumber, systemCode);
		if (images != null && !images.isEmpty()) {
			for (Attachment image : images) {
				String typeCode = image.getFileType();
				DocumentType imgType = DocumentType.valueByCode(typeCode);
				if (imgType == null) {
					log.warn("** Image Type is not mapped in DocumentType: " + typeCode);
				} else {
					image.setDescription(imgType.toString());
				}
			}
		}
		
		return images;
	}
	
	public List<ManifestInfo> retrieveManifestInfo(Probill probill, String systemCode, String schema){
		String probillNumber = probill.getProbillNumber();
		ProbillStoredProcedureParam param = new ProbillStoredProcedureParam();
		param.setProbillNumber(probillNumber);
		param.setReturnCode("NA");
		param.setSystemCode(systemCode);
		probillMapper.callManifestProc(param);
		
		if("OK".equals(StringUtils.trimToEmpty(param.getReturnCode()))){
			log.debug("-> Manifest SP succeed");
			List<ManifestInfo> manifestList =
					probillMapper.selectManifestByProbill(schema, probillNumber);
			
			if(manifestList != null){
				//Do not use for(:) here, we need to remove element during loop
				Iterator<ManifestInfo> it = manifestList.iterator();
				ManifestInfo previouseManifest = new ManifestInfo();
				log.debug("-> Number of manifest records = " + manifestList.size());
				while(it.hasNext()){
					ManifestInfo manifest = it.next();

					//retrieve city info
					manifest.setFromCity(this.retrieveCityName(schema, manifest.getFromCityCode()));
					manifest.setToCity(this.retrieveCityName(schema, manifest.getToCityCode()));
					
					//show status
					String type = StringUtils.trim(manifest.getType());
					String dispatchId = StringUtils.trimToEmpty(manifest.getDispathId());
					String line = StringUtils.trimToEmpty(manifest.getLineId());
					String deliveryField = StringUtils.trimToEmpty(manifest.getDeliveryField());
					String status = null;
					switch(type){
					case "OTR":
						status = "In Transit";
						break;
					case "PUD":
						
						//If the line number (MTW001.LINE) is 1, we display Picked Up.
						if("1".equals(line)){
							status = "Picked Up";
						}
						//If Dispatch ID (MTW001.VMTRIP) is 1 or 2, and Line ID is 1 or 2, or 3, we display Picked Up. 
						else if(ArrayUtils.contains(new String[]{"1","2"}, dispatchId) 
								&& (ArrayUtils.contains(new String[]{"1","2","3" }, line))){
							status = "Picked Up";
						}else if(StringUtils.trimToNull(probill.getTransferTo()) != null){
							//If there is a transfer to (FRL00142.FHXT), we display In Transit.
							status = "In Transit";
						}else{
							status = "Out for Delivery";
						}
						break;
					case "MTY":
						//If the delivery date (FRL00142.FHDDAT) and time (FRL00142.FHDTIM) are not entered, we do not display that entry.
						if(StringUtils.isEmpty(manifest.getFormattedDeliveryDate())
								&& StringUtils.isEmpty(manifest.getFormattedDeliveryTime())){
							log.debug("--> *** Delivery date and time is empty, remove!");
							previouseManifest = manifest;
							it.remove();
							continue;
						}else if("Y".equals(deliveryField)){
							log.debug("--> Probill.transferTo() = " + StringUtils.trimToNull(probill.getTransferTo()));
							log.debug("--> Probill.deliveryDate() = " + probill.getFormattedDeliveryDate());
							
							
							if(StringUtils.trimToNull(probill.getTransferTo()) != null 
									&& StringUtils.isEmpty(probill.getFormattedDeliveryDate() )){
								log.debug("--> Probill.transferTo() is not null && Probill.deliveryDate() is null, set status to In Transit");
								status = "In Transit";
							}else{
								status = "Delivered";
							}
							
							//If Status is MTY and delivered, show delivery date and time
							manifest.setToDate(manifest.getDeliveryDate());
							manifest.setToTime(manifest.getDeliveryTime());
						}else{
							status = "Delivered";
						}
						
						break;
					default:
						status = type;
					}
					manifest.setStatus(status);
					
					//Generate status description
					if("Delivered".equals(status)){
						String deliveryStatus = StringUtils.trimToNull(manifest.getDeliveryStatus());
						if(deliveryStatus == null ) {
							log.debug("-> delivery status is null, probill " + probillNumber);
						}else{
							
							log.debug("-> retrieve delivery description of " + deliveryStatus);
							String description = deliveryCodeMapper.selectCodeDescription(deliveryStatus);
							
							String tooltip = MessageFormat.format("{0} {1} @ {2} Received by: {3}", 
									description, manifest.getFormattedDeliveryDate(), 
									manifest.getFormattedDeliveryTime(), manifest.getReceivedBy());
							manifest.setStatusTooltip(tooltip);
							log.debug("-> set tooltip: " + tooltip);
						}
						
						
					}
					
					//If the last record and the current record are the same,skip displaying the same line twice
					log.debug("current manifest:" + manifest);
					log.debug("previouse manifest:" + previouseManifest);
					if(manifest.equals(previouseManifest)){
						log.debug("--> Same record found, remove");
						it.remove();
					}else{
					
						log.debug("--> Manifest info culculated");
						
					}
					//save current manifest to compare purpose
					previouseManifest = manifest;
				}
				
				
			}
			
			return manifestList;
		}else{
			
			log.error("FAILED TO CALL MANIFEST SP -> " + param);
			return null;
		}
		
	}
	
	private String retrieveCityName(String schema, String cityCode){
		String code = StringUtils.trimToNull(cityCode);
		if(code == null){
			return StringUtils.EMPTY;
		}
		String city = null;
		String state = null;
		if(code.length() > 3){
			city =  cityMapper.selectCityNameWithLongCode(schema, cityCode);
			state = cityMapper.selectStateWithLongCode(schema, code);
		}else{
			city = cityMapper.selectCityNameWithShortCode(schema, cityCode);
			state = cityMapper.selectStateWithShortCode(schema, code);
		}
		
		return StringUtils.join(new String[]{StringUtils.trimToNull(city), StringUtils.trimToNull(state)}, ",");
	}
	
	private void assimbleReceivedBy(Probill probill, SecureUser user){
		String schema = user.getDbSchema();
		String probillNumber = probill.getProbillNumber();
		String receivedBy = "";
		List<String> receivedByList = probillMapper.selectReceivedByProbill(schema, probillNumber);
		//probill.setReceivedBy(StringUtils.join(receivedBy, ","));
		//use the first record(Not confirmed)
		if(receivedByList != null && !receivedByList.isEmpty()){
			receivedBy = receivedByList.get(0);
		}
		//If transferred to a carrier (FHXT):
		if(StringUtils.trimToNull(probill.getTransferTo()) != null){
			//If delivery date entered (FHDDAT > 0), show:		Received by: (receiver’s name)
			if(StringUtils.isNotEmpty(probill.getFormattedDeliveryDate())){
				probill.setReceivedBy(receivedBy);
			} else {
				//If no delivery date entered, show:	Received by: (blank)
				probill.setReceivedBy(StringUtils.EMPTY);
			}
		}else{
			//Otherwise, if not transferred to a carrier, show:  Received by: (receiver’s name)
			probill.setReceivedBy(receivedBy);
		}
		
	}
	
	private void assimbleReceivedBy(List<Probill> probills, SecureUser user){
		long begin = System.currentTimeMillis();
		
		if(probills == null) return;
		for(Probill probill : probills){
			assimbleReceivedBy(probill, user);
		}
		
		log.info("--> Time effort to assimble received by -> " + (System.currentTimeMillis() - begin) / 1000 + " SEC");
		
	}

	@Override
	public ResultContext retrieveProbillByBOL(RequestContext requestContext) {
		
		ResultContext rlt = new ResultContext();
		
		String bolNumber = (String) requestContext.getParameter(ContextConstants.PARAM_TRACING_BOL);
		//tracing by global or current account
		String accountType = (String) requestContext.getParameter(ContextConstants.PARAM_ACCOUNT_TYPE);
		SecureUser user = requestContext.getUserProfile();
		
		List<String> accounts = new ArrayList<String>();
		if(UserAccountType.GlobalAccount.toString().equals(accountType)){
			log.debug("-> use global account to search BOL " + bolNumber);
			for(Account act : user.getGlobalAccount()){
				accounts.add(act.getAccountNumber());
			}
		}else{
			log.debug("-> use current account to search BOL " + bolNumber);
			accounts.add(user.getAccount());
		}
		
		
		String schema = user.getDbSchema();
		
		//BoL# saved in DB in char(25). should make sure the length is 25.
		//bolNumber = StringUtils.rightPad(StringUtils.trimToEmpty(bolNumber), 25);
       
		Probill probill = probillMapper.selectProbillByBOL(schema, bolNumber, accounts);
		
		if(probill == null){
			log.info("NO PROBILL FOUND WITH BOL#:" + bolNumber);
			return rlt;
		}
		
		probill = retrieveExtendData(probill, requestContext.getUserProfile());
		
		probill = retrieveProbillSections(probill, requestContext.getUserProfile());
		
		rlt.addResult(ContextConstants.PARAM_PROBILL, probill);

		return rlt;

	}

	@Override
	public ResultContext retrieveProbillByShipper(RequestContext requestContext) {
		ResultContext rlt = new ResultContext();
		
		String shipperNumber = (String) requestContext.getParameter(ContextConstants.PARAM_TRACING_SHIPPER);
		String schema = requestContext.getUserProfile().getDbSchema();
   
		//Shipper# saved in DB in char(15). should make sure the length is 15.
		//shipperNumber = StringUtils.rightPad(StringUtils.trimToEmpty(shipperNumber), 15);
		
		Probill probill = probillMapper.selectProbillByShipper(schema, shipperNumber);
		
		if(probill == null){
			log.info("NO PROBILL FOUND WITH SHIPPER#:" + shipperNumber);
			return rlt;
		}
		
		probill = retrieveExtendData(probill, requestContext.getUserProfile());
		
		probill = retrieveProbillSections(probill, requestContext.getUserProfile());
		
		rlt.addResult(ContextConstants.PARAM_PROBILL, probill);

		return rlt;
	}

	@Override
	public ResultContext retrieveProbillByPO(RequestContext requestContext) {
		ResultContext rlt = new ResultContext();
		//PO# VPO00406135    , first parameter is char(15), others are char(7)
		
		String poNumber = (String) requestContext.getParameter(ContextConstants.PARAM_TRACING_PO);
		String schema = requestContext.getUserProfile().getDbSchema();
		
		//poNumber = StringUtils.rightPad(StringUtils.trimToEmpty(poNumber), 15);
		
		Probill probill = probillMapper.selectProbillByPO(schema, poNumber);
		
		if(probill == null){
			log.info("NO PROBILL FOUND WITH PO#:" + poNumber);
			return rlt;
		}
		
		probill = retrieveExtendData(probill, requestContext.getUserProfile());
		
		probill = retrieveProbillSections(probill, requestContext.getUserProfile());
		
		rlt.addResult(ContextConstants.PARAM_PROBILL, probill);

		return rlt;
	}

	@Override
	public ResultContext retrieveProbillByPickupNumber(RequestContext requestContext) {
		
		ResultContext rlt = new ResultContext();
		//Pickup number is a numeric in database
		String pickupNumber = (String) requestContext.getParameter(ContextConstants.PARAM_TRACING_PICKUPNUMBER);
		String schema = requestContext.getUserProfile().getDbSchema();
		
		log.debug("--> parameter in: pickupNumber=" + pickupNumber + ", schema=" + schema);
		
		String probillNumber = probillMapper.selectProbillNumberByPickupNumber(schema, pickupNumber);
		
		if(probillNumber == null){
			log.info("NO PROBILL FOUND WITH PICKUP#:" + pickupNumber);
			return rlt;
		}
		
		Probill probill = probillMapper.selectProbillByNumber(schema, probillNumber);
		
		if(probill == null){
			log.info("PROBILL IS NOT FOUND :" + probillNumber);
			return rlt;
		}
		
		probill = retrieveExtendData(probill, requestContext.getUserProfile());
		
		//For tracing by pickup number, no need to retrieve pieces, manifest.
		
		rlt.addResult(ContextConstants.PARAM_PROBILL, probill);
		
		return rlt;
	}

	@Override
	public ResultContext retrievePickUpStatusDetails(RequestContext requestContext) {
		ResultContext rlt = new ResultContext();
		//Pickup number is a numeric in database
		String pickupNumber = (String) requestContext.getParameter(ContextConstants.PARAM_TRACING_PICKUPNUMBER);
		String schema = requestContext.getUserProfile().getDbSchema();
		
		
		List<PickupStatusDetail> list = probillMapper.selectPickupStatusDetailByPickupNumber(schema, pickupNumber);
		
		rlt.addResult(ContextConstants.PARAM_TRACING_PICKUPSTATUS, list);
		
		return rlt;
	}

	@Override
	public ResultContext retrieveProbillsByPickupDate(RequestContext requestContext) throws BusinessException {
		ResultContext rlt = new ResultContext();
		SecureUser user = requestContext.getUserProfile();
		String schema = user.getDbSchema();
		String userType = StringUtils.upperCase(StringUtils.trimToEmpty(user.getType()));
		Date beginDate = toObject(requestContext.getParameter(ContextConstants.PARAM_DATE_BEGIN), Date.class);
		Date endDate = toObject(requestContext.getParameter(ContextConstants.PARAM_DATE_END), Date.class);
		String paraminfo = MessageFormat.format("PARAM_DATE_BEGIN:{0}, PARAM_DATE_END{1}", beginDate, endDate);
		log.debug(paraminfo);
		if(beginDate == null || endDate == null){
			
			throw new BusinessException("Parameter is NULL: " + paraminfo);
		}
		
		
		List<Probill> list = null;
		//TODO retrieve users group account.
		List<String> accounts = CollectionUtils.arrayToList(new String[]{user.getAccount()});
		int begin = Integer.parseInt(DateUtil.toDateString(beginDate, DateUtil.DB_DATE_FORMAT));
		int end = Integer.parseInt(DateUtil.toDateString(endDate, DateUtil.DB_DATE_FORMAT));
		if(UserType.PARTNER.toString().equals(userType)){
			log.debug("--> User type is Partner");
			list = probillMapper.selectPartnerProbillByPickupDate(schema, accounts, begin, end);
		}else{
			log.debug("--> User type is NOT Partner:" + userType);
			list = probillMapper.selectProbillByPickupDate(schema, accounts, begin, end);
		}
		
		//retrieve received by
		
		assimbleReceivedBy(list, user);
		
		rlt.addResult(ContextConstants.PARAM_PROBILL, list);
		
		return rlt;
	}

	@Override
	public ResultContext retrieveProbillsByDeliveryDate(RequestContext requestContext) throws BusinessException{
		ResultContext rlt = new ResultContext();
		SecureUser user = requestContext.getUserProfile();
		String schema = user.getDbSchema();
		String userType = user.getType();
		Date beginDate = toObject(requestContext.getParameter(ContextConstants.PARAM_DATE_BEGIN), Date.class);
		Date endDate = toObject(requestContext.getParameter(ContextConstants.PARAM_DATE_END), Date.class);
		
		
		String paraminfo = MessageFormat.format("PARAM_DATE_BEGIN:{0}, PARAM_DATE_END{1}", beginDate, endDate);
		log.debug(paraminfo);
		if(beginDate == null || endDate == null){
			
			throw new BusinessException("Parameter is NULL: " + paraminfo);
		}
		
		//TODO retrieve users group account.
		List<Probill> list = null;
		List<String> accounts = CollectionUtils.arrayToList(new String[]{user.getAccount()});
		int begin = Integer.parseInt(DateUtil.toDateString(beginDate, DateUtil.DB_DATE_FORMAT));
		int end = Integer.parseInt(DateUtil.toDateString(endDate, DateUtil.DB_DATE_FORMAT));
		if(UserType.PARTNER .toString().equals(userType)){
			log.debug("--> User type is Partner");
			list = probillMapper.selectPartnerProbillByDeliveryDate(schema, accounts, begin, end);
		}else{
			log.debug("--> User type is NOT Partner:" + userType);
			list = probillMapper.selectProbillByDeliveryDate(schema, accounts, begin, end);
		}
		
		//retrieve received by
		
		assimbleReceivedBy(list, user);

		rlt.addResult(ContextConstants.PARAM_PROBILL, list);
		return rlt;
	}

	@Override
	public ResultContext generateImage(RequestContext requestContext) {
		
		String probillNumber = (String) requestContext.getParameter(ContextConstants.PARAM_PROBILLNO);
		String imageId = (String) requestContext.getParameter(ContextConstants.PARAM_IMAGE_ID);
		String sysCode = requestContext.getUserProfile().getSystemId();
		
		probillMapper.callImageProc(imageId, probillNumber, sysCode);
		log.debug("image procedure called -> " + imageId);
		
		ResultContext result = new ResultContext();
		result.addResult(ContextConstants.PARAM_RETURN_CODE, "OK");
		
		return result;
	}

	@Override
	public ResultContext retrieveImageInfo(RequestContext requestContext) {
		
		String probillNumber = (String) requestContext.getParameter(ContextConstants.PARAM_PROBILLNO);
		String sysCode = requestContext.getUserProfile().getSystemId();
		
		List<Attachment> images = retrieveImages(probillNumber, sysCode);
		
		ResultContext result = new ResultContext();
		result.addResult(ContextConstants.PARAM_IMAGES, images);
		
		return result;
	}
	
	@Override
	public ResultContext retrieveImageIds(RequestContext requestContext) {
		
		String probillNumber = (String) requestContext.getParameter(ContextConstants.PARAM_PROBILLNO);
		String sysCode = requestContext.getUserProfile().getSystemId();
		
		List<Attachment> images = retrieveImages(probillNumber, sysCode);
		Set<String> imageIds = new HashSet<String>();
		for (Attachment attachement:images) {
			imageIds.add(trimToEmpty(attachement.getId()));
		}
		ResultContext result = new ResultContext();
		result.addResult(ContextConstants.PARAM_IMAGES, new ArrayList<String>(imageIds));
		
		return result;
	}

	@Override
	public ResultContext retrieveProbillNumberByPage(RequestContext requestContext) {
		Page<String> pageIn = (Page<String>) requestContext.getPage();
		
		List<String> probills = probillMapper.selectProbillNumberByPickupDate(pageIn);
		pageIn.setResults(probills);
		ResultContext result = new ResultContext();
		result.setPage(pageIn);
		
		return result;
	}

}
