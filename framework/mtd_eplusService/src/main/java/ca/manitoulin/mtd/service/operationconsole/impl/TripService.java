package ca.manitoulin.mtd.service.operationconsole.impl;

import static ca.manitoulin.mtd.util.ApplicationSession.get;
import static ca.manitoulin.mtd.util.DateUtil.DATE_TIME_SHOW;
import static ca.manitoulin.mtd.util.DateUtil.toDateString;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Optional.of;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ca.manitoulin.mtd.dao.customer.CustomerMapper;
import ca.manitoulin.mtd.dto.customer.CustomerLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ca.manitoulin.mtd.code.NotificationType;
import ca.manitoulin.mtd.constant.ApplicationConstants;
import ca.manitoulin.mtd.dao.maintenance.TripEventTemplateMapper;
import ca.manitoulin.mtd.dao.misc.CommunicationMapper;
import ca.manitoulin.mtd.dao.operationconsole.ConditionMapper;
import ca.manitoulin.mtd.dao.operationconsole.FreightMapper;
import ca.manitoulin.mtd.dao.operationconsole.InvoiceMapper;
import ca.manitoulin.mtd.dao.operationconsole.TripCostMapper;
import ca.manitoulin.mtd.dao.operationconsole.TripDocumentMapper;
import ca.manitoulin.mtd.dao.operationconsole.TripEventMapper;
import ca.manitoulin.mtd.dao.operationconsole.TripMapper;
import ca.manitoulin.mtd.dao.operationconsole.TripNoteMapper;
import ca.manitoulin.mtd.dao.security.OrganizationMapper;
import ca.manitoulin.mtd.dto.maintenance.TripEventTemplate;
import ca.manitoulin.mtd.dto.misc.CommunicationEmail;
import ca.manitoulin.mtd.dto.operationconsole.Condition;
import ca.manitoulin.mtd.dto.operationconsole.Freight;
import ca.manitoulin.mtd.dto.operationconsole.Invoice;
import ca.manitoulin.mtd.dto.operationconsole.InvoiceDetail;
import ca.manitoulin.mtd.dto.operationconsole.Trip;
import ca.manitoulin.mtd.dto.operationconsole.TripCost;
import ca.manitoulin.mtd.dto.operationconsole.TripDocument;
import ca.manitoulin.mtd.dto.operationconsole.TripEvent;
import ca.manitoulin.mtd.dto.operationconsole.TripEventNotify;
import ca.manitoulin.mtd.dto.operationconsole.TripNote;
import ca.manitoulin.mtd.dto.security.Organization;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.exception.ConcurrentException;
import ca.manitoulin.mtd.exception.security.AuthorizationException;
import ca.manitoulin.mtd.service.maintenance.ICurrencyService;
import ca.manitoulin.mtd.service.notify.INotificationService;
import ca.manitoulin.mtd.service.operationconsole.ITripService;
import ca.manitoulin.mtd.util.ApplicationSession;
import ca.manitoulin.mtd.util.DateUtil;
import ca.manitoulin.mtd.util.IDocumentManager;
import ca.manitoulin.mtd.util.UnitConversionHelper;

@Service
public class TripService implements ITripService {
	private static final Logger log = Logger.getLogger(TripService.class);

	@Autowired
	private FreightMapper freightMapper;

	@Autowired
	private TripMapper tripMapper;

	@Autowired
	private ConditionMapper conditionMapper;

	
	@Autowired
	private TripCostMapper costMapper;

	@Autowired
	private TripNoteMapper noteMapper;

	@Autowired
	private TripEventMapper eventMapper;

	@Autowired
	private TripDocumentMapper docMapper;


	
	@Autowired
	private InvoiceMapper invoiceMapper;
	
	@Autowired
	private CommunicationMapper commMapper;
	
	@Autowired
	private INotificationService noteService;
	
	@Autowired
	private IDocumentManager docManager;
	
	@Autowired
	private ICurrencyService currencyService;

	@Autowired
	private TripEventTemplateMapper tripEventTemplateMapper;

	@Autowired
	private OrganizationMapper orgMapper;

	@Autowired
	private CustomerMapper customerMapper;

	@Override
	public String getDivisionCodeByTripId(Integer tripId){
		return tripMapper.getDivisionCodeByTripId(tripId);
	}

	@Override
	public List<Freight> retrieveFreightsByQuoteId(Integer quoteId) {
		List<Freight> lts = freightMapper.selectFreightByTripId(quoteId);
		return lts;
	}

	@Override
	public Freight createFreight(Freight freight) {

		freightMapper.insertFreight(freight);
		return freight;
	}

	@Override
	public Freight updateFreight(Freight freight) {
		freightMapper.updateFreight(freight);
		return freight;
	}

	@Override
	public void removeFreight(Integer id) {
		freightMapper.deleteFreight(id);
		;

	}

	@Override
	public Freight retrieveFreightById(Integer freightId) {
		Freight freight = freightMapper.selectFreightById(freightId);
		return freight;
	}

	@Override
	public List<Trip> retrieveQuoteListByDepartmentAndStatus(Integer departmentId, String statusCode, String searchKey, String startDate, String endDate) {
		//Filter by status code
		List<Trip> quotes = tripMapper.selectQuotes(null, departmentId, statusCode, searchKey, startDate, endDate, ApplicationSession.get().getReferLanguage());
		return quotes;
	}

	/**
	 * Generate quote reference, e.g. Milton-Q-00010
	 *
	 * @param quoteId
	 * @return
	 */
	private String generateQuoteReference(Integer quoteId) {
		String ref = new String();
		DecimalFormat nf = new DecimalFormat("0000000");
		//ref = departmentName + "-Q-" + nf.format(quoteId);
		ref = nf.format(quoteId);
		return ref;

	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public Trip touchQuote(Integer departmentId) {
		// Insert a record into epTrip
		SecureUser currentUser = get();
		Trip trip = new Trip();
		if (departmentId > 0)
			trip.setDepartmentId(departmentId);
		else
			trip.setDepartmentId(currentUser.getDepId());
		trip.setType("UNKNOWN");
		trip.setStatus("DRAFT");
		trip.setVersion(0);

		tripMapper.insertTrip(trip);

		// generate reference number, and update to the draft
		Integer tripId = trip.getId();
		String refNumber = generateQuoteReference(tripId);
		tripMapper.updateTripReference(tripId, refNumber);
		trip.setRefId(refNumber);
		log.debug("New Quote# udpated:" + refNumber);

		// prepare all T&C
		//this.resetConditions(tripId);
		//log.debug("T & C prepared:" + refNumber);
		trip = this.retrieveTripById("",tripId, false);

		log.info("-- Touch a new Quote in DRAFT status: " + refNumber);
		return trip;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public Trip touchTrip(Integer departmentId) {

		// Insert a record into epTrip
		SecureUser currentUser = get();

		Trip trip = new Trip();
		if (departmentId > 0)
			trip.setDepartmentId(departmentId);
		else
			trip.setDepartmentId(currentUser.getDepId());
		trip.setType("UNKNOWN");
		trip.setStatus("NEW");
		trip.setVersion(0);
		tripMapper.insertTrip(trip);

		// generate reference number, and update to the draft
		Integer tripId = trip.getId();
		String refNumber = generateQuoteReference(tripId);
		tripMapper.updateTripReference(tripId, refNumber);
		trip.setRefId(refNumber);
		log.debug("New Trip# udpated:" + refNumber);

		// prepare all T&C
		//this.resetConditions(tripId);
		//log.debug("T & C prepared:" + refNumber);
		trip = this.retrieveTripById("", tripId, false);

		log.info("-- Touch a new Trip in NEW status: " + refNumber);
		return trip;
	}
	
	private void saveCosts(Trip trip){
		
		HashSet<Integer> costIdToBeUpdate = new HashSet<Integer>();
		if (!equal(null,trip.getCosts())) {
			for (TripCost cost : trip.getCosts()) {
				cost.setTripId(trip.getId());
				if (cost.getId() == null) {
					//costMapper.insertTripCost(cost); -->2017/4/24 save cost in front end
					log.debug("--> New cost, create");
				} else {
					//costMapper.updateTripCost(cost);
					log.debug("--> New cost, update id=" + cost.getId());
	
				}
	            costIdToBeUpdate.add(cost.getId());
	
			}
		}
		
		// Remove costs not exits anymore.
		SecureUser user = get();
		
		List<TripCost> costList = costMapper.selectTripCosts(trip.getId(), user.isAdmin());
		if (costList != null) {
			for (TripCost c : costList) {
				if (costIdToBeUpdate.contains(c.getId()) == false) {
					//costMapper.deleteTripCost(c.getId());
					log.debug("--> delete Cost,  id=" + c.getId());
				}
			}
		}

		log.debug("Costs saved: " + trip.getRefId());
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public Trip saveTrip(Trip trip) throws BusinessException {
		if (trip.getId() == null) {
			throw new BusinessException("PK is NULL in Trip");
		}
		saveFreights(trip);

		//Save costs
		saveCosts(trip);

		//2017-11-14 clear out the Critical Time / Date when the trip status is the following statue
		//2017-12-19 CRITICAL Issue #109 Need to exclude blanking out critical time for OH.
		if (trip.getStatus().equals("DEL") || trip.getStatus().equals("ARC") || trip.getStatus().equals("CLO")) {
			trip.setCriticalTime(null);
		}		//update epTrip
		tripMapper.updateTrip(trip);
		log.debug("Trip saved: " + trip.getRefId());
		
		//Release concurrency lock
		trip.setLockedBy(null);
		trip.setLockStatus("N");
		tripMapper.updateTripLockStatus(trip);
		//TODO asynchronously generate PDF
		return trip;
	}
	
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	private void saveFreights(Trip trip) {
		// save freights
		BigDecimal quoteTotal = new BigDecimal(0.00);
		BigDecimal totalChargeWeight = new BigDecimal(0.00);
		HashSet<Integer> freightIdToBeUpdate = new HashSet<Integer>();
		if (!equal(null, trip.getFreights())) {
			for (Freight freight : trip.getFreights()) {
				freight.setTripId(trip.getId());
				if (freight.getId() == null) {
					//freightMapper.insertFreight(freight); --> 2017/4/24 update to save in front end
					log.debug("--> New freight, create");
				} else {
					//freightMapper.updateFreight(freight);
					log.debug("--> New freight, update id=" + freight.getId());

				}
				freightIdToBeUpdate.add(freight.getId());
				quoteTotal.add(freight.getEstimatedCost());
				totalChargeWeight.add(freight.getActualChargeWt());
			}
		}
		//update total
		trip.setQuoteTotal(quoteTotal);
		//trip.setChargedWeight(totalChargeWeight);
		// Remove freights not exits anymore.
		List<Freight> freightList = freightMapper.selectFreightByTripId(trip.getId());
		if (freightList != null) {
			for (Freight fr : freightList) {
				if (freightIdToBeUpdate.contains(fr.getId()) == false) {
					//freightMapper.deleteFreight(fr.getId());
					log.debug("--> delete freight,  id=" + fr.getId());
				}
			}
		}

		log.debug("Freights saved: " + trip.getRefId());
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public Trip createQuote(Trip trip) throws BusinessException {

		if (trip.getId() == null) {
			throw new BusinessException("PK is NULL in Quote");
		}

		saveFreights(trip);

		//Bob: T & C have been saved once user click save in the t & C tab. -20170310
		//save terms
		//if(trip.getConditions() != null){
		//	for(Condition c : trip.getConditions()){
		//		c.setTripId(trip.getId());
		//		conditionMapper.insertCondition(c);
		//	}
		//}
		//log.debug("Terms & Conditions saved: " + trip.getRefId());

		//update epTrip
		
		if ("DRAFT".equals(trip.getStatus()))
			trip.setStatus("QUO");
		tripMapper.updateQuote(trip);
		log.debug("Trip saved: " + trip.getRefId());
		
		//Release concurrency lock
		trip.setLockedBy(null);
		trip.setLockStatus("N");
		trip.setLocktime(null);
		tripMapper.updateTripLockStatus(trip);

		//TODO asynchronously generate PDF
		return trip;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public Trip updateQuote(Trip trip) throws BusinessException {


		//Clean freights and terms first --> change to update freight
		//freightMapper.deleteFreightByTripId(trip.getId());

		//Bob: T & C have been saved once user click save in the t & C tab. -20170310
		//clean conditions and terms
		//conditionMapper.deleteConditionsByTrip(trip.getId());

		createQuote(trip);

		return trip;
	}

	@Override
	public List<Condition> retrieveQuoteTreeByTripID(int tripID) {
		return conditionMapper.selectConditions(tripID);
	}

	@Override
	public Condition retrieveConditionById(Integer id) throws BusinessException {
		return conditionMapper.selectConditionById(id);
	}

	@Override
	public void addCondition(Condition condition) throws BusinessException {
		setConditionCategorySequence(condition);
		conditionMapper.insertCondition(condition);
	}

	@Override
	public void updateConditionForQuotePdf(Condition condition) throws BusinessException {
		conditionMapper.updateConditionForQuotePdf(condition);
	}

	@Override
	public void updateCondition(Condition condition) throws BusinessException {
		setConditionCategorySequence(condition);
		conditionMapper.updateCondition(condition);
	}

	@Override
	public void updateConditionSequence(Integer id, Integer categorySequence, Integer sequence) throws Exception {
		Condition condition = new Condition();
		condition.setId(id);
		condition.setSequence(sequence);

		SecureUser currentUser = get();
		condition.setUpdatedBy(currentUser.getUid());
		condition.setCurrentCompany(currentUser.getCompany());
		condition.setCurrentCustomer(currentUser.getCompany());
		if (categorySequence >= 1){
			condition.setCategorySequence(categorySequence);
			conditionMapper.updateConditionCategorySequence(condition);
		}
		else{
			conditionMapper.updateConditionSequence(condition);
		}
	}

	private Condition setConditionCategorySequence(Condition condition){
		Integer categorySeq;

		categorySeq = conditionMapper.getConditionCategorySequenceByCategory(condition.getName(), condition.getCategory(), condition.getTripId());
		if (categorySeq==null) {
			Integer id = condition.getId();
			if (id ==null){
				id = 0;
			}
			categorySeq = conditionMapper.getLastConditionCategorySequenceByName(condition.getName(), id, condition.getTripId());
			if (categorySeq==null) {
				categorySeq = 1;
			} else {
				categorySeq = categorySeq + 1;
			}
		}
		condition.setCategorySequence(categorySeq);
		return condition;
	}

	@Override
	public void removeCondition(Integer id) throws BusinessException {
		conditionMapper.deleteCondition(id);
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public List<Condition> addConditions(Integer tripId, List<Integer> quoteTemplateIdList) {
		if (quoteTemplateIdList == null || quoteTemplateIdList.isEmpty())
			return this.resetConditions(tripId);
		conditionMapper.deleteConditionsByTrip(tripId);

		//import T&C
		Trip trip = new Trip();
		trip.setId(tripId);
		trip.setQuoteTemplatesTobeImported(quoteTemplateIdList);
		conditionMapper.insertConditionsWithTemplate(trip);
		//Retrieve generated T&C
		List<Condition> conditions = this.retrieveQuoteTreeByTripID(tripId);

		return conditions;
	}

	@Override
	public Trip retrieveTripById(String loadType, Integer tripId, Boolean includeFullProfile) {
		Trip trip = tripMapper.selectQuoteByID(tripId, ApplicationSession.get().getReferLanguage());

		//Calculate total
		int totalPieces = 0;
		BigDecimal totalWT = new BigDecimal(0);
		StringBuilder dims = new StringBuilder();
		BigDecimal totalCubicFeet = BigDecimal.ZERO;
		BigDecimal totalchargeWT = new BigDecimal(0);

		List<Freight> freights = freightMapper.selectFreightByTripId(tripId);
		if (freights != null) {
			BigDecimal factor = new BigDecimal(0);
			if (trip.getSystemOfMeasure()== null)
				factor = new BigDecimal(166.00);
			else {
				if (trip.getSystemOfMeasure() != null & trip.getSystemOfMeasure().equals("M")) {
					factor = new BigDecimal(6000);
				} else {
					factor = new BigDecimal(166.00);
				}
			}

			for (Freight f : freights) {
				if (f.getActualPieces() != null && f.getActualPieces() > 0){
					totalPieces +=f.getActualPieces();
				} else {
					if ("pickup".equals(loadType)) {
						totalPieces += (f.getEstimatedPieces() != null && f.getEstimatedPieces() > 0) ? f.getEstimatedPieces() : 0;
					}
				}

				if (f.getActualWeight() != null && f.getActualWeight().floatValue() > 0){
					totalWT = totalWT.add(f.getActualWeight());
				} else {
					if ("pickup".equals(loadType)) {
						totalWT = totalWT.add((f.getEstimatedWeight() != null && f.getEstimatedWeight().floatValue() > 0)
								? f.getEstimatedWeight()
								: BigDecimal.ZERO);
					}
				}

				if (!StringUtils.isEmpty(f.getActualDimension()))
					dims.append(f.getActualDimension());
				else {
					if ("pickup".equals(loadType)) {
						if (!StringUtils.isEmpty(f.getEstimatedDimension()))
							dims.append(f.getEstimatedDimension());
					}
				}

				if(f.getActualPieces() != null && f.getActualDimension() != null){
					String dim = f.getActualDimension();
					String unit = trimToEmpty(StringUtils.substringAfterLast(dim, " "));
					String[] d = StringUtils.split(StringUtils.substringBefore(dim, " "), "X");
					if (d.length >= 2) {
						int len = Integer.parseInt(d[0]);
						int width = Integer.parseInt(d[1]);
						int height = Integer.parseInt(d[2]);
						log.info("Prepare to calculate dim:" + d + ", unit=" + unit);

						totalCubicFeet = totalCubicFeet.add(UnitConversionHelper.inchToFeet(new BigDecimal(len), unit)
								.multiply(UnitConversionHelper.inchToFeet(new BigDecimal(width), unit))
								.multiply(UnitConversionHelper.inchToFeet(new BigDecimal(height), unit))
								.multiply(new BigDecimal(f.getActualPieces()))
						).setScale(2, RoundingMode.HALF_EVEN);

						BigDecimal thisWT = new BigDecimal(len)
								.multiply(new BigDecimal(width))
								.multiply(new BigDecimal(height))
								.multiply(new BigDecimal(f.getActualPieces()));

						thisWT = thisWT.divide(factor, 0, BigDecimal.ROUND_HALF_UP);
						totalchargeWT = totalchargeWT.add(thisWT);
					}
				}
				else {
					if ("pickup".equals(loadType)) {
						if(f.getEstimatedPieces() != null && f.getEstimatedDimension() != null) {
							String dim = f.getEstimatedDimension();
							String unit = trimToEmpty(StringUtils.substringAfterLast(dim, " "));
							String[] d = StringUtils.split(StringUtils.substringBefore(dim, " "), "X");
							if (d.length >= 2) {
								int len = Integer.parseInt(d[0]);
								int width = Integer.parseInt(d[1]);
								int height = Integer.parseInt(d[2]);
								log.info("Prepare to calculate dim:" + d + ", unit=" + unit);

								totalCubicFeet = totalCubicFeet.add(UnitConversionHelper.inchToFeet(new BigDecimal(len), unit)
										.multiply(UnitConversionHelper.inchToFeet(new BigDecimal(width), unit))
										.multiply(UnitConversionHelper.inchToFeet(new BigDecimal(height), unit))
										.multiply(new BigDecimal(f.getEstimatedPieces()))
								).setScale(2, RoundingMode.HALF_EVEN);

								BigDecimal thisWT = new BigDecimal(len)
										.multiply(new BigDecimal(width))
										.multiply(new BigDecimal(height))
										.multiply(new BigDecimal(f.getEstimatedPieces()));

								thisWT = thisWT.divide(factor, 0, BigDecimal.ROUND_HALF_UP);
								totalchargeWT = totalchargeWT.add(thisWT);
							}
						}
					}
				}
			}

			trip.setTotalPieces(totalPieces);
			trip.setTotalWeight(totalWT);
			trip.setChargedWeight(totalchargeWT);
			if (!StringUtils.isEmpty(dims))
				trip.setDims(dims.toString());
			else
				trip.setDims("");
			trip.setTotalCubicFeet(totalCubicFeet);
		}

		if (includeFullProfile) {
			log.debug("begin to retrieve other information");
			trip.setFreights(freights);

			SecureUser user = get();
			List<TripCost> costs = costMapper.selectTripCosts(tripId, user.isAdmin());
			trip.setCosts(costs);
		}
		return trip;
	}


	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public List<Condition> resetConditions(Integer tripId) {

		//remove all existing T&C
		conditionMapper.deleteConditionsByTrip(tripId);
		//import all T&C
		Trip trip = new Trip();
		trip.setId(tripId);
		conditionMapper.insertConditionsWithTemplate(trip);
		//Retrieve generated T&C
		List<Condition> conditions = this.retrieveQuoteTreeByTripID(tripId);

		return conditions;
	}

	@Override
	public List<Trip> retrieveTrips(Integer departmentId, String category, String searchKey, String startDate, String endDate, String searchAWB, String chargeCode)  {

		List<Trip> trips = null;
		trips = tripMapper.selectTrips(null, departmentId, category, searchKey, startDate, endDate, searchAWB, chargeCode, ApplicationSession.get().getReferLanguage());
		return trips;
	}

	@Override
	public List<Trip> retrieveTripList(Integer departmentId, String category, String searchKey, String startDate, String endDate, String searchAWB, String eventDesc, String chargeCode, String chargeDesc)  {

		List<Trip> trips = null;
		trips = tripMapper.selectTripList(null, departmentId, category, searchKey, startDate, endDate, searchAWB, eventDesc, chargeCode, chargeDesc, ApplicationSession.get().getReferLanguage());
		return trips;
	}

	@Override
	public List<TripEvent> retrieveEvents(Integer tripId) {
		List<TripEvent> list = eventMapper.selectTripEvents(tripId, ApplicationSession.get().getReferLanguage());	
		//Calculate notify and costs;
		if(list != null){
			for(TripEvent event : list){
				List<TripEventNotify> notifies = eventMapper.selectEventNotifies(event.getId());
				if(notifies != null){
					HashSet<String> emailSet = new HashSet<String>();
					for(TripEventNotify n : notifies){
						emailSet.add(n.getEmail());
					}
					String emailStr = StringUtils.join(emailSet, ",");
					emailStr = emailStr.replace(";", ",");
					emailStr = emailStr.replace(",", ", ");
					emailStr = emailStr.replace("  ", " ");
					event.setJointNotify(emailStr);
					log.debug("Event Notify joint ->" + event.getJointNotify());
				}
				List<TripCost> costs = costMapper.selectTripCostsByEventId(event.getId(), get().isAdmin());
				if(costs != null){
					BigDecimal totalCost = new BigDecimal(0);
					for(TripCost c : costs){
						if (c.getActUsedCost() != null) {
							System.out.println(c.getActUsedCost());
							totalCost = totalCost.add(c.getActUsedCost());
						}
					}
					event.setTotalCosts(totalCost);
					log.debug("Event total USD cost set to ->" + event.getTotalCosts());
				}
			}
			
		}
		return list;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public TripEvent createEvent(TripEvent event) {
		setTripEventSequence(event);
		eventMapper.insertTripEvent(event);
		
		//process POD info
		if(event.getPodDate() != null && event.getPodName() != null){
			Trip trip = new Trip();
			trip.setId(event.getTripId());
			trip.setPodName(event.getPodName());
			trip.setPodTime(event.getPodDate());
			tripMapper.updatePOD(trip);
		}
			
		
		Integer eventId = event.getId();
		log.debug("--> event created, id = " + eventId);
		//create cost
		List<TripCost> costs = event.getCosts();
		if(costs != null){
			for(TripCost cost : costs){
				cost.setEventId(eventId);
				cost.setTripId(event.getTripId());
				//2017-06-29 CR13# if linked event entity is defined, and a cost is auto added to that entity associated
				//cost.setContact(event.getLinkedEntity());
				costMapper.insertTripCost(cost);
			}
			
			log.debug("--> event related cost created, id = " + eventId);
		}
		
		//Update trip next event information
		List<TripEventNotify> notifies = event.getNotifies();
		if(notifies != null){
			for(TripEventNotify notify : notifies){
				notify.setEventId(eventId);
				eventMapper.insertEventNotify(notify);
			}
			log.debug("--> event related notify created, id = " + eventId);
		}
		
		
		updateTripNextEvent(event.getTripId());
		return event;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public TripEvent updateEvent(TripEvent event) {
		setTripEventSequence(event);
		eventMapper.updateTripEvent(event);
		log.info("(1) Event updated, id=" + event.getId());
		
		//2017-06-29 CR13# if linked event entity is defined, and a cost is added auto add that entity associated
		/*
		List<TripCost> costs = costMapper.selectTripCostsByEventId(event.getId(), true);
		if(costs != null){
			for(TripCost cost: costs){
				costMapper.updateLinkedEntity(cost.getId(), event.getLinkedEntity());
				log.debug("--> Cost linked entity updated to " + event.getLinkedEntity() );
			}
		}
		log.info("(2) Related Cost updated, id=" + event.getId());
		*/
		
		//Update trip next event information
		updateTripNextEvent(event.getTripId());
		log.info("(3) Next event info updated, trip id=" + event.getTripId());
		
		//process POD info
		String desc;
		desc = event.getDescription(); //desc = event.getPodName();
		if (StringUtils.isNotEmpty(desc) && desc.length() > 45)
		{
			desc = desc.substring(0, 45);
		}
		if(event.getPodDate() != null && desc != null){
			Trip trip = new Trip();
			trip.setId(event.getTripId());

			trip.setPodName(desc);
			trip.setPodTime(event.getPodDate());
			tripMapper.updatePOD(trip);
		}
		log.info("(4) POD info updated, trip id=" + event.getTripId());
		
		return event;
	}

	@Override
	public void updateTripEventSequence(Integer id, Integer categorySequence, Integer sequence) throws Exception {
		TripEvent tripEvent = new TripEvent();
		tripEvent.setId(id);
		tripEvent.setSequence(sequence);

		SecureUser currentUser = get();
		tripEvent.setUpdatedBy(currentUser.getUid());
		tripEvent.setCurrentCompany(currentUser.getCompany());
		tripEvent.setCurrentCustomer(currentUser.getCompany());
		if (categorySequence >= 1){
			tripEvent.setCategorySequence(categorySequence);
			eventMapper.updateTripEventCategorySequence(tripEvent);
		}
		else{
			eventMapper.updateTripEventSequence(tripEvent);
		}
	}

	private TripEvent setTripEventSequence(TripEvent tripEvent){
		boolean isNewCategory = false;
		Integer id = 0;
		if (tripEvent.getId()!= null){
			id = tripEvent.getId();
		}
		if (id==0) {
			isNewCategory = true;
		} else {
			id = tripEvent.getId();
			TripEventTemplate temp = eventMapper.selectTripEventById(id);
			if (temp != null) {
				if (!temp.getCategory().equals(tripEvent.getCategory())) {
					isNewCategory = true;
				}
			}
		}

		if (isNewCategory){
			Integer itemSeq = 0;
			Integer categorySeq = eventMapper.getTripEventCategorySequenceByCategory(tripEvent.getName(), tripEvent.getCategory(), tripEvent.getTripId());
			if (categorySeq == null) {
				categorySeq = eventMapper.getLastTripEventCategorySequenceByName(tripEvent.getName(), id, tripEvent.getTripId());
				if (categorySeq == null) {
					categorySeq = 1;
				} else {
					categorySeq = categorySeq + 1;
				}
				itemSeq = 1;
			} else {
				itemSeq = eventMapper.getTripEventSequenceByCategory(tripEvent.getName(), tripEvent.getCategory(), tripEvent.getTripId());
				if (itemSeq == null) {
					itemSeq = 1;
				} else {
					itemSeq = itemSeq + 1;
				}
			}
			tripEvent.setCategorySequence(categorySeq);
			tripEvent.setSequence(itemSeq);
		}

		return tripEvent;
	}
	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void removeEvent(Integer eventId) {
		TripEvent event = eventMapper.selectTripEventById(eventId);
		//costMapper.deleteTripCostsByEvent(eventId);
		eventMapper.deleteEventNotifiesWithEventId(eventId);
		eventMapper.deleteTripEvent(eventId);

		//Update trip next event information
		updateTripNextEvent(event.getTripId());

	}
	
	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void removeEvent(Integer[] eventIds) {
		
		for(Integer id : eventIds)
			removeEvent(id);

	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public Integer hasCostDataByEventIds(String eventIds) {
		return eventMapper.hasCostDataByEventIds(eventIds);
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public List<TripEvent> resetEvents(Integer tripId) {
		eventMapper.deleteEventNotifiesWithTripId(tripId, null);
		costMapper.deleteTripEventCostsByTrip(tripId);
		eventMapper.deleteTripEventsByTrip(tripId);
		Trip trip = new Trip();
		trip.setId(tripId);
		eventMapper.insertTripsWithTemplate(trip);

		eventMapper.insertTripEventCostWithTemplate(tripId);

		eventMapper.insertTripEventNotifyWithTemplate(tripId);

		return this.retrieveEvents(tripId);
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public List<TripEvent> addEvents(Integer tripId, List<Integer> eventTemplateIdList) {
		if (eventTemplateIdList == null || eventTemplateIdList.isEmpty())
			return this.resetEvents(tripId);

		eventMapper.deleteEventNotifiesWithTripId(tripId, null);
		costMapper.deleteTripEventCostsByTrip(tripId);
		eventMapper.deleteTripEventsByTrip(tripId);

		//import event
		Trip trip = new Trip();
		trip.setId(tripId);
		trip.setEventTemplatesTobeImported(eventTemplateIdList);

		eventMapper.insertTripsWithTemplate(trip);

		eventMapper.insertTripEventCostWithTemplate(tripId);

		eventMapper.insertTripEventNotifyWithTemplate(tripId);

		//Retrieve generated events
		List<TripEvent> events = this.retrieveEvents(tripId);

		//Update trip next event information
		updateTripNextEvent(tripId);
		
		return events;
	}
	
	/**
	 * Update next event name and event time into EpTrip.
	 * get the next event from last event with actual date. If no actual date to begin with, use the first one.
	 * @param tripId
	 */
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	private void updateTripNextEvent(Integer tripId){
		//Retrieve last complete event
		TripEvent lastCompleteEvent = eventMapper.selectLastCompleteTripEvent(tripId);
		Integer categorySequence = 0;
		Integer sequence = 0;

		if(lastCompleteEvent != null) {
			categorySequence = lastCompleteEvent.getCategorySequence();
			sequence = lastCompleteEvent.getSequence();
		}

		//Retrieve next event
		TripEvent nextEvent = eventMapper.selectNextTripEvent(tripId, categorySequence, sequence);

		if (nextEvent != null)
			tripMapper.updateTripNextEvent(tripId, nextEvent.getItem(), nextEvent.getEstimatedDate());
		else
			tripMapper.updateTripNextEvent(tripId, "", null);
	}

	@Override
	public List<Map<String, Object>> retrieveTripEventTree(List<TripEvent> findAll) {
		List<Map<String, Object>> result = newArrayList();
		String prevLevel1 = "";
		String prevLevel2 = "";
		String thisLevel1;
		String thisLevel2;
		int pid = 0;
		int cpid = 0;
		int categorySeq = 0;
		int itemSeq = 0;

		if (isNotEmpty(findAll)) {
			for (TripEvent quoteTemplate : findAll) {
				thisLevel1 = quoteTemplate.getName();
				Map d = newHashMap();
				d.put("id", quoteTemplate.getId());
				d.put("pId", 0);
				d.put("name", quoteTemplate.getName());
				d.put("t", quoteTemplate.getName());
				d.put("entityId", quoteTemplate.getId());
				d.put("open", "true");
				d.put("type", "event");
				d.put("TripType", quoteTemplate.getType());
				d.put("EventName", quoteTemplate.getName());
				d.put("Category", quoteTemplate.getCategory());
				d.put("customerNotify", quoteTemplate.getCustomerNotify());
				d.put("IsComplete", "N");
				if (!prevLevel1.equals(thisLevel1)) {
					categorySeq = 0;
					itemSeq = 0;
					result.add(d);
					pid = quoteTemplate.getId();
				}
				thisLevel2 = quoteTemplate.getCategory();
				Map data = newHashMap();
				data.put("id", quoteTemplate.getId() + 1000000);
				data.put("pId", pid);
				data.put("name", quoteTemplate.getCategory());
				data.put("t", quoteTemplate.getCategory());
				data.put("open", "true");
				data.put("entityId", quoteTemplate.getId());
				data.put("type", "category");
				data.put("TripType", quoteTemplate.getType());
				data.put("EventName", quoteTemplate.getName());
				data.put("Category", quoteTemplate.getCategory());
				data.put("customerNotify", quoteTemplate.getCustomerNotify());
				data.put("IsComplete", "N");
				if (!prevLevel2.equals(thisLevel2) || !prevLevel1.equals(thisLevel1)) {
					itemSeq = 0;
					categorySeq = categorySeq + 1;
					data.put("categorySeq", categorySeq);
					result.add(data);
					cpid = quoteTemplate.getId() + 1000000;
				}
				Map item = newHashMap();
				item.put("id", quoteTemplate.getId() + 100000000);
				item.put("pId", cpid);
				String name;

				name = quoteTemplate.getItem();
				if (quoteTemplate.getActualDate() != null) {
					name = name + " " + toDateString(quoteTemplate.getActualDate(), DATE_TIME_SHOW);
					item.put("IsComplete", "Y");
				} else {
					item.put("IsComplete", "N");
					if (quoteTemplate.getEstimatedDate() != null)
						name = name + " " + toDateString(quoteTemplate.getEstimatedDate(), DATE_TIME_SHOW);
				}
				if (!isBlank(quoteTemplate.getLinkedEntityAddress1()))
					name = name + " (" +trimToEmpty(quoteTemplate.getLinkedEntityAddress1())+")";

				item.put("name", name);

				item.put("t", quoteTemplate.getItem());
				item.put("open", "true");
				item.put("entityId", quoteTemplate.getId());
				item.put("type", "item");
				item.put("TripType", quoteTemplate.getType());
				item.put("EventName", quoteTemplate.getName());
				item.put("Category", quoteTemplate.getCategory());
				item.put("customerNotify", quoteTemplate.getCustomerNotify());
				itemSeq = itemSeq + 1;
				item.put("itemSeq", itemSeq);
				result.add(item);
				prevLevel1 = thisLevel1;
				prevLevel2 = thisLevel2;
			}
		}

		return result;
	}

	@Override
	public List<TripNote> retrieveNotes(Integer tripId) {
		SecureUser currentUser = get();
		List<TripNote> notes = noteMapper.selectTripNotes(tripId);
		for (TripNote item : notes) {
			if (item.getUpdatedBy().equals(currentUser.getUid())){
				item.setCanEdit(true);
			} else {
				item.setCanEdit(false);
			}
		}

		return notes;
	}

	@Override
	public TripNote createNote(String text, Integer tripId) {
		SecureUser user = get();
		TripNote note = new TripNote();
		note.setContent(text);
		note.setCreatedBy(user.getFullName());
		note.setStatus(null);
		note.setTripId(tripId);
		noteMapper.insertTripNote(note);
		return note;
	}

	@Override
	public TripNote updateNote(TripNote note) {
		SecureUser currentUser = get();
		note.setUpdatedBy(currentUser.getUid());
		note.setCurrentCompany(currentUser.getCompany());
		note.setCurrentCustomer(currentUser.getCompany());
		noteMapper.updateTripNote(note);
		return note;
	}

	@Override
	public void deleteNote(Integer id) throws Exception {
		of(id);
		noteMapper.deleteTripNote(id);
	}

	@Override
	public List<TripDocument> retrieveDocuments(Integer tripId, String noEmail) {
		List<TripDocument> docs = docMapper.selectTripDocuments(tripId, noEmail, ApplicationSession.get().getReferLanguage());
		return docs;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public TripDocument createDocument(TripDocument doc, boolean onlyKeepOneInThisType) {
		//retrieve other records in this type;
		List<TripDocument> docs = docMapper.selectTripDocumentsByType(doc.getTripId(), doc.getFileType(), ApplicationSession.get().getReferLanguage());
		if (docs != null && !docs.isEmpty()) {			
			for (TripDocument d : docs) {
				//Override the file with same name, or remove all file with the same type in case onlyKeepOneInThisType=true
				if(onlyKeepOneInThisType ||
						(doc.getOriginalFileName().equals(d.getOriginalFileName()))){
					
					this.removeTripDocument(d);
				}
				
			}
		}

		SecureUser currentUser = get();
		doc.setUpdatedBy(currentUser.getUid());
		doc.setCurrentCompany(currentUser.getCompany());
		doc.setCurrentCustomer(currentUser.getCompany());

		//If document existing, override
		docMapper.insertTripDocument(doc);
		return doc;
	}
	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public TripDocument copyAttachmentToDocument(Integer docId, String newName, String newType) throws IOException{
		TripDocument doc = this.retrieveDocumentById(docId);
		
		//replicate attachment file to another;
		log.debug("--> begin to replicate attachment");
		File attachement = null;
		
		CommunicationEmail email = this.retrieveEmail(doc.getCommunicationId());
		Integer tripId;
		tripId = doc.getTripId();
		if (tripId == null || tripId == 0) {
			tripId = email.getTripId();
			doc.setTripId(tripId);
		}
		if(email != null && "In".equalsIgnoreCase(email.getType())){
			log.info("--> This is an attachment of an Email in. Try to retrieve file from attachment folder!");
			attachement = new File(docManager.getEmailAttachmentFolderPath() + File.separator + doc.getFilename());
		}else{
			log.info("--> This is an attachment of a trip!");
			attachement = docManager.getDocument(tripId, doc.getFilename());
		}
		String newFilePath = docManager.getFilePathToSave(tripId, doc.getFilename());
		FileUtils.copyFile(attachement, new File(newFilePath));

		log.debug("--> begin to replicate document record");
		doc.setFilename(StringUtils.substringAfterLast(newFilePath, File.separator));		
		doc.setFileType(newType);
		doc.setOriginalFileName(newName);
		doc.setCommunicationId(null);
		
		this.createDocument(doc, false);
		return doc;
	}
    @Override
    public List<TripDocument> retrieveTripDocumentsByType(Integer tripId, String docType) {


        //retrieve other records in this type;
        return docMapper.selectTripDocumentsByType(tripId, docType, ApplicationSession.get().getReferLanguage());

    }
	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void removeTripDocument(Integer id){
		TripDocument d = docMapper.selectTripDocument(id, ApplicationSession.get().getReferLanguage());
		if(d != null)
			this.removeTripDocument(d);
	}

	@Override
	public boolean isExistTripTemplate(String templateName) throws BusinessException{
		log.debug("Input Template Name =>" + templateName);
		if (isEmpty(templateName)) throw new BusinessException("Template name is empty");
		return tripMapper.selectTemplateName(trimToEmpty(templateName)) > 0;
	}

    @Override
    public void saveTripTemplateName(Integer tripId, String templateName, Integer eventTemplateId) {
        log.debug("update template name of trip==>tripId:"+tripId+" Template Name:" + templateName);
        tripMapper.updateTripTemplateName(tripId,templateName, eventTemplateId);
    }

    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	private void removeTripDocument(TripDocument d){
		//remove physical file
		try {
			File f = docManager.getDocument(d.getTripId(), d.getFilename());
			f.delete();
			log.debug("Documents file deleted from the disk, file=" + d.getFilename());
		} catch (Exception e) {
			log.error(e);
		}
		docMapper.deleteTripDocument(d.getId());

		log.debug("Documents removed, id=" + d.getId());
	}
	

	@Override
	public TripEvent retrieveEventById(Integer eventId) {
		TripEvent event = eventMapper.selectTripEventById(eventId);
		Trip trip = this.retrieveTripById("", event.getTripId(), false);
		event.setPodDate(trip.getPodTime());
		event.setPodName(trip.getPodName());
		
		return event;
	}

	@Override
	public List<TripEventNotify> retrieveEventNotifiesByEventId(Integer eventId) {
		return eventMapper.selectEventNotifies(eventId);
	}

	@Override
	public List<TripCost> retrieveTripCostsByQuoteId(Integer tripId) {
		SecureUser user = get();
		return costMapper.selectTripCosts(tripId, user.isAdmin());
	}

	@Override
	public List<TripCost> retrieveTripCostsByEventId(Integer eventId) {
		return costMapper.selectTripCostsByEventId(eventId, get().isAdmin());
	}

	@Override
	public TripCost createTripCost(TripCost tripCost) {
		updateLinkedEntityAndContactToEvent(tripCost);

		costMapper.insertTripCost(tripCost);
		return tripCost;
	}

	@Override
	public TripCost updateTripCost(TripCost tripCost) {
		updateLinkedEntityAndContactToEvent(tripCost);

		costMapper.updateTripCost(tripCost);
		return tripCost;
	}


	private void updateLinkedEntityAndContactToEvent(TripCost tripCost) {
		if (tripCost.getEventId()!=null && tripCost.getEventId() > 0){
			TripEvent event = eventMapper.selectTripEventById(tripCost.getEventId());
			if (event.getLinkedEntity() == null || event.getLinkedEntity() == 0){
				event.setLinkedEntity(tripCost.getLinkedEntity());
				event.setLinkedEntityContact(tripCost.getLinkedEntityContact());
				eventMapper.updateTripEvent(event);
			}
		}
	}

	@Override
	public void removeTripCost(Integer id) {
		costMapper.deleteTripCost(id);

	}

	@Override
	public TripCost retrieveTripCostById(Integer id) {

		return costMapper.selectTripCostById(id);
	}

	@Override
	public TripDocument retrieveDocumentById(Integer docId) {

		return docMapper.selectTripDocument(docId, ApplicationSession.get().getReferLanguage());
	}

	@Override
	public Trip savePickUp(Trip trip) {
		tripMapper.updatePickup(trip);
		return trip;
	}

	@Override
	public Trip savePickUpAsPreview(Trip trip) {
		//TODO
		//Insert when temp record not exists in EpTripTemp
		//else update
		return null;
	}

	@Override
	public Trip saveQuotePdfData(Trip trip) {
		tripMapper.updateQuotePdfData(trip);
		return trip;
	}

	@Override
	public Trip saveBOL(Trip trip) {
		tripMapper.updateBOL(trip);
		return trip;
	}

	@Override
	public List<Invoice> retrieveInvoices(Integer departmentId, Date dateFrom, Date dateTo) {
		List<Invoice> invoices = invoiceMapper.selectInvoices(null, null, departmentId, dateFrom, dateTo, ApplicationSession.get().getReferLanguage());
		return invoices;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public Invoice saveInvoice(Invoice invoice) {
		if(invoice == null) return null;
		
		//Calcuate net profit	
		
		BigDecimal amountWithoutTax = invoice.getInvoiceSubtotal();
		log.info("-->(1)Amount without tax in " + invoice.getBillingCurrency() + " is " + amountWithoutTax);
		//retrieve all costs in USD
		List<TripCost> costs = this.retrieveTripCostsByQuoteId(invoice.getTripId());
		BigDecimal totalCostsInUSD = new BigDecimal(0);
		//using point in time when invoice created for conversion
		BigDecimal usdRate = null;
		if(invoice.getId() == null){
			
			//for newly created invoice, use current rate
			usdRate = currencyService.retrieveUSDRate(invoice.getBillingCurrency());
			log.debug("New invoice, use CURRENCY excharge rate:" + usdRate);
		}else{
			//else use saved excharge rate
			Invoice existingInvoice = invoiceMapper.selectInvoiceById(invoice.getId());
			usdRate = existingInvoice.getExchangeRate();
			log.debug("Existing invoice, use invoice excharge rate:" + usdRate);
		}
		
		if(usdRate == null) 
			usdRate = new BigDecimal(1);
		
		log.info("-->USD Rate for " + invoice.getBillingCurrency() + " is " + usdRate);
		
		//tally up all costs entered for a trip in USD (using point in time currency conversion)
		if(costs != null){
			for(TripCost cost: costs){
				
				if(cost.getActCost() == null)
					continue;
				
				BigDecimal currentUSDRateOfCost = currencyService.retrieveUSDRate(cost.getActCurrency());
				if(cost.getActCost() == null || currentUSDRateOfCost == null)
					continue;
				BigDecimal costInUSD = cost.getActCost().multiply(currentUSDRateOfCost);
				totalCostsInUSD = totalCostsInUSD.add(costInUSD);
			}
		}
		log.info("-->(2)Costs in USD is " + totalCostsInUSD);
		
		//convert total cost USD back to {invoiced currency} (using point in time when invoice created for conversion)
		BigDecimal totalCosts =  totalCostsInUSD.divide(usdRate, 2, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
		log.info("-->(3)Costs in " + invoice.getBillingCurrency() + " is " + totalCosts);
		
		BigDecimal netProfit = amountWithoutTax.subtract(totalCosts);
		log.info("-->(4)net profit in " + invoice.getBillingCurrency() + " is " + netProfit);		
		
		invoice.setNetProfit(netProfit);
		invoice.setNetProfitCurrency(invoice.getBillingCurrency());		
				
		if(invoice.getId() != null){
			//update invoice
			invoiceMapper.updateInvoice(invoice);
			log.debug("--> Invoice updated " + invoice.getId());
			
			//Clear all invoice detail
			invoiceMapper.deleteAllInvoiceDetails(invoice.getId());
			
		}else{
			//Save excharge rate with creation
			invoice.setExchangeRate(usdRate);
			
			invoiceMapper.insertInvoice(invoice);
			log.debug("--> Invoice created " + invoice.getId());
			
			
		}
		
		//Update Trip
		Trip trip = tripMapper.selectQuoteByID(invoice.getTripId(), ApplicationSession.get().getReferLanguage());
		// 2017/9/19 Trip level totals need to be updated after a new invoice is added.
		trip = invoice.updateToTrip(trip);
		tripMapper.updateTrip(trip);
		log.debug("--> Trip updated " + trip.getId());
		
		//create invoice details		
		for(InvoiceDetail detail : invoice.getDetails()){
			detail.setInvoiceId(invoice.getId());
			invoiceMapper.insertInvoiceDetail(detail);
		}
		
		return invoice;
	}

	@Override
	public Invoice retrieveInvoice(Integer invoiceId) {
		
		Invoice invoice = invoiceMapper.selectInvoiceById(invoiceId);
		//Retrieve details
		List<InvoiceDetail> details = invoiceMapper.selectInvoiceDetails(invoiceId, ApplicationSession.get().getReferLanguage());
		invoice.setDetails(details);
		//Retrieve trip
		Trip trip = this.retrieveTripById("", invoice.getTripId(), true);
		invoice.replicateTripInfo(trip);
		return invoice;
	}

	@Override
	public Invoice prepareBlankInvoice(Integer tripId) {
		Invoice invoice = new Invoice();
		//Retrieve trip
		Trip trip = this.retrieveTripById("", tripId, true);
		//CRITICAL Issue #135– Updated 2018-01-24: recheck the billing company when creating an invoice
		//if (trip.getBilledClientId() == null || trip.getBilledClientId() == 0) {
			if (trip.getClientId() != null && trip.getClientId() > 0) {
				CustomerLocation client = customerMapper.selectCustomerLocationById(trip.getClientId());
				if (client.getBillingCompany() != null && client.getBillingCompany() > 0) {
					trip.setBilledClientId(client.getBillingCompany());
				} else {
					trip.setBilledClientId(trip.getClientId());
				}
			}
		//}
		invoice.replicateTripInfo(trip);
		//Prepare invoice number
		List<Invoice> existingInvoice = this.retrieveInvoicesByTrip(tripId);
		int countOfInvoice = (existingInvoice == null || existingInvoice.isEmpty()) ? 0 : existingInvoice.size();
		
		String invoiceRefNumber = "INV-" + trip.getDepartmentShortName() + "-" + trip.getRefId() + "-" + (countOfInvoice++);
		invoice.setRefNum(invoiceRefNumber);
		
		return invoice;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void removeInvoice(Integer invoiceId) {
		invoiceMapper.deleteAllInvoiceDetails(invoiceId);
		invoiceMapper.deleteInvoice(invoiceId);
		log.info("--> invoice record deleted");
		//Remove generated document
		List<TripDocument> pdfFiles = docMapper.selectTripDocumentsByRefName(String.valueOf(invoiceId));
		if(pdfFiles != null && pdfFiles.size() > 0){
			TripDocument pdf = pdfFiles.get(0);
			//The following method will remove both record and pdf file.
			this.removeTripDocument(pdf.getId());
		}
		log.info("--> invoice PDF file removed");
	}
	
	@Override
	public List<Trip> retrieveQuotes(List<Integer> departmentIds, String statusCode) {
		List<Trip> quotes = tripMapper.selectQuotes(departmentIds, 0, statusCode, null, null, null, ApplicationSession.get().getReferLanguage());
		return quotes;
	}

	@Override
	public List<Trip> retrieveTrips(List<Integer> departmentIds, String statusCode) {
		List<Trip> trips = null;
		trips = tripMapper.selectTrips(departmentIds, 0, statusCode, null, null, null, null, null, ApplicationSession.get().getReferLanguage());
		return trips;
	}

	@Override
	public List<Invoice> retrieveInvoices(List<Integer> departmentIds, Date dateFrom, Date dateTo) {
		List<Invoice> invoices = invoiceMapper.selectInvoices(null, departmentIds,null, dateFrom, dateTo, ApplicationSession.get().getReferLanguage());
		return invoices;
	}

	@Override
	public List<Invoice> retrieveInvoicesByTrip(Integer tripId) {
		List<Invoice> invoices = invoiceMapper.selectInvoices(tripId, null,null, null,null, ApplicationSession.get().getReferLanguage());
		return invoices;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void replicateEmailAddressForAllEvents(List<TripEventNotify> emails, Integer currentEventId, Integer tripId) {
		//Remove all other email address under this trip
		eventMapper.deleteEventNotifiesWithTripId(tripId, currentEventId);
		log.debug("-> delete all other email address except " + currentEventId);
		//Insert emails for every events
		List<TripEvent> events = eventMapper.selectTripEvents(tripId, ApplicationSession.get().getReferLanguage());	
		if(events != null){
			for(TripEvent event : events){
				if( event.getId().equals(currentEventId) == false){
					for(TripEventNotify n : emails){
						n.setEventId(event.getId());
						eventMapper.insertEventNotify(n);
					}
				}
			}
		}
		log.debug("-> replicate email address complent " + currentEventId);
	}

	@Override
	public TripEventNotify createEventNotify(TripEventNotify notify) {
		eventMapper.insertEventNotify(notify);
		return notify;
	}

	@Override
	public TripEventNotify updateEventNotify(TripEventNotify notify) {
		eventMapper.updateEventNotify(notify);
		return notify;
	}

	@Override
	public void removeEventNotify(Integer notifyId) {
		eventMapper.deleteEventNotify(notifyId);
		
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public Trip retrieveTripForEditing(String loadType, Integer tripId, Boolean includeFullProfile) throws ConcurrentException {
		SecureUser currentUser = get();
		Trip trip = this.retrieveTripById(loadType, tripId, includeFullProfile);
		int releaseSeconds = Integer.parseInt(ApplicationConstants.getConfig("sys.currentLock.releaseTime"));

		//Release other locked of current user
		this.releaseAllConcurrencyLock(currentUser.getUid());
		
		//if locked by others, throw current exception
		if(	"Y".equals(trip.getLockStatus()) 
				&& ! currentUser.getUid().equals(trip.getLockedBy())				){
			log.info("--> Trip is locked by other user: trip id=" + tripId + ", lock user=" + trip.getLockedBy());

			//check the lock time
			Date utcNow = trip.getCurrentUTCTime();
			if(utcNow.after(DateUtils.addSeconds(trip.getLocktime(), releaseSeconds))){
				log.info("--> invalid lock time, lock will be override, trip.getLocktime()=" + trip.getLocktime());
				trip.setLockedBy(currentUser.getUid());
				trip.setLockStatus("Y");
				tripMapper.updateTripLockStatus(trip);
			}else{
				log.info("--> Lock still valid, trip.getLocktime()=" + trip.getLocktime());
				throw new ConcurrentException("The Quote/Trip Is Being Editted By Other Users.");
			}
		}else{
			trip.setLockedBy(currentUser.getUid());
			trip.setLockStatus("Y");
			tripMapper.updateTripLockStatus(trip);
			log.info("--> Lock the trip for editing trip id=" + tripId);
		}
			
		return trip;
	}

	@Override
	public void releaseAllConcurrencyLock(String userId) {
		
		if(userId == null){
			SecureUser currentUser = get();
			userId = currentUser.getUid();
		}
		
		tripMapper.deleteTripLockStatusByUUID(userId);
		log.info("--> release current user's lock, user id=" + userId);
	}

	@Override
	public List<Trip> selectLockedTripAndQuotes(List<Integer> departmentIds) throws Exception {
		List<Trip> trips = null;
		trips = tripMapper.selectLockedTripAndQuotes(departmentIds, ApplicationSession.get().getReferLanguage());
		return trips;
	}

	@Override
	public void deleteTripLockStatusByID(Integer tripId) throws Exception {
		tripMapper.deleteTripLockStatusByID(tripId);
	}

	@Override
	public int getDocVersionByType(Integer tripid, String type) {
		if (equal(null, tripid) || equal(null, type)) return  1;
        List<TripDocument> documents = docMapper.selectTripDocumentsByType(tripid,type, ApplicationSession.get().getReferLanguage());
        return equal(documents,null)?1:documents.size()+1;
	}

	@Override
	public List<CommunicationEmail> retrieveEmailsByTrip(Integer tripId) {
		return searchEmails(tripId, null, null,null,null, "N");
	}

	@Override
	public List<CommunicationEmail> searchEmails(Integer tripId, Date dateFrom, Date dateTo,  String label, String key, String includeDelete) {

		List<CommunicationEmail> list = commMapper.searchEmails(tripId, dateFrom,dateTo,label, key, includeDelete.equals("Y"));
		
		List<CommunicationEmail> returnedList = new ArrayList<CommunicationEmail>();
		SecureUser currentUser = get();
		
		if (null != list) {
			for (CommunicationEmail email : list) {
				//Deleted Email is invisible to Non-admin user
				if(currentUser.isAdmin() == false && "Deleted".equals(email.getProcessedStatus()))
					continue;
				
				
				List<TripDocument> attachments = docMapper.selectTripDocumentsByEmail(email.getId(), ApplicationSession.get().getReferLanguage());
				email.setAttachments(attachments);
				returnedList.add(email);
			}
		}
		return returnedList;
	}

	@Override
	public List<CommunicationEmail> retrieveAllUnlinkedEmails() {
		List<CommunicationEmail> list = retrieveAllUnlinkedEmails(null);
		return list;
	}
	
	@Override
	public List<CommunicationEmail> retrieveAllUnlinkedEmails(List<Integer> deparmentIds){
		List<CommunicationEmail> list = this.retrieveAllUnlinkedEmailsByConditions(null, null, null, null, "", "", "", deparmentIds);
		return list;
	}

	@Override
	public List<CommunicationEmail> retrieveAllUnlinkedEmailsByConditions(Date dateFrom, Date dateTo,  String label, String key, String includeDelete, String includeProcessed, String includeOut, List<Integer> deparmentIds) {
		List<CommunicationEmail> list = commMapper.selectUnlinkedByConditions(dateFrom,  dateTo,  label, key, includeDelete, includeProcessed, includeOut, deparmentIds, get().isAdmin());
		if (null != list) {
			for (CommunicationEmail email : list) {
				if (email.getId() != null && email.getId() > 0) {
					List<TripDocument> attachments = docMapper.selectTripDocumentsByEmail(email.getId(), ApplicationSession.get().getReferLanguage());
					email.setAttachments(attachments);
				}
			}
		}
		return list;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void updateEmailLabel(Long emailId, String label){
		commMapper.updateLabel(emailId, label);
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public CommunicationEmail sendEmail(CommunicationEmail email, String action) {
		// Save email
		if (action.equals("save")) {
			email.setType("Draft");
			email.setReadStatus("R");
		} else {
			email.setType("Out");
			email.setReadStatus("R");
			email.setProcessedStatus("Processed");
		}

		String emailFrom;
		emailFrom = ApplicationConstants.getConfig("mail.default.from");
		int deptId = email.getDepartmentId();
		if (deptId > 0){
			Organization dep = orgMapper.selectDepartmentById(deptId);
			if (dep.getDefaultEmail()!=null && !dep.getDefaultEmail().equals("")){
				emailFrom = dep.getDefaultEmail();
			}
		}
		email.setMailFrom(emailFrom);
		email.setEmail(email.getMailTo());
		email.setDepartmentId(email.getDepartmentId());
		if (email.getId()!=null && email.getId() > 0){
			commMapper.update(email);
		} else {
			commMapper.insert(email);
		}

		// Save attachment in case it has
		// TODO, set file type. in this moment, file type is NULL, so users can not see the email attachments in Trip Generic Documents.
		if(email.getAttachments() != null){
			ArrayList<TripDocument> clonedAttachments = new ArrayList<TripDocument>();
			
			for(TripDocument doc : email.getAttachments()){
				log.debug("--> begin to retrieve email attachment id=" + doc.getId());
				doc = docMapper.selectTripDocument(doc.getId(), ApplicationSession.get().getReferLanguage());
				if(doc == null){
					log.error("Document not found! Please make sure id is saved after uploading: id=" + doc.getId());
				}
				
				//2017-06-19 Change Request #23 need ability to choose from uploaded documents to attach for new outbound email
				//If communicationId is not null, means it is a reused file.
				if(doc.getCommunicationId() != null && !email.getId().equals(doc.getCommunicationId())){
					log.info("Found a attachment reused from another document: Id=" + doc.getId());
					doc.setCommunicationId(email.getId());
					docMapper.insertDocumentFromUploaded(doc);
				}else{
					log.info("Newly uploaded attachment, communication id is null: Id=" + doc.getId());
					doc.setCommunicationId(email.getId());
					//doc.setType();
					docMapper.updateTripDocument(doc);
				}
				clonedAttachments.add(doc);
			}
			
			email.setAttachments(clonedAttachments);
			log.info("--> --> Attachment updated");
		}

		if (action.equals("save")) {
			return email;
		}

		// Send out email
		try {
			noteService.sendEmail(email);
		} catch (Exception e) {
			log.error("Failed to send email", e);
		}
		return email;
	}
	
	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void removeEmail(Long id){
		//remove email
		CommunicationEmail email = commMapper.selectById(id);
		if (email!=null) {
			if (email.getProcessedStatus().equals("Deleted")) {
				//Remove attachment and files(if has)
				List<TripDocument> attachments = docMapper.selectTripDocumentsByEmail(id, ApplicationSession.get().getReferLanguage());
				if(attachments != null){
					for(TripDocument doc : attachments){
						this.removeTripDocument(doc);
					}
					log.info("Attachments deleted!");
				}else{
					log.info("No Attachment requires delete!");
				}

				commMapper.deletePyshically(id);
			}
			else
				commMapper.delete(id);
		}
		log.info("Email deleted:" + id);
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void unremoveEmail(Long id){
		//remove email
		CommunicationEmail email = commMapper.selectById(id);
		if (email!=null) {
			commMapper.undelete(id);
		}
		log.info("Email undeleted:" + id);
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void linkEmailToTrip(Long emailId, String tripRefNumber) throws BusinessException{
		
		Trip linkedTrip = validateTripBeforeLinkEmail(tripRefNumber);
		CommunicationEmail email = new CommunicationEmail();
		email.setId(emailId);
		email.setTripId(linkedTrip.getId());
		email.setDepartmentId(linkedTrip.getDepartmentId());
		commMapper.updateLink(email);
	}
	    
	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public    void relinkEmailToTrip(Long emailId, String tripRefNumber) throws BusinessException{
		Trip linkedTrip = validateTripBeforeLinkEmail(tripRefNumber);
		CommunicationEmail email = new CommunicationEmail();
		email.setId(emailId);
		email.setTripId(linkedTrip.getId());
		email.setDepartmentId(linkedTrip.getDepartmentId());
		email.setProcessedStatus("Processed");
		email.setReadStatus("U");
		commMapper.updateLink(email);
	}
	    
	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public    void unklinkEmail(Long emailId){
		
		CommunicationEmail email = new CommunicationEmail();
		email.setId(emailId);
		email.setTripId(null);
		email.setProcessedStatus("UnProcessed");
		email.setReadStatus("U");
		commMapper.updateLink(email);
	}
	    

	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	private Trip validateTripBeforeLinkEmail( String tripRefNumber) throws BusinessException {
		tripRefNumber = StringUtils.trimToEmpty(tripRefNumber);

		String formattedTripNum = "";
		formattedTripNum = tripRefNumber;

		//When input AXI-393849, accept
		if(StringUtils.contains(tripRefNumber, "-")){
			tripRefNumber = StringUtils.substringAfterLast(tripRefNumber, "-");
		}
		if(!StringUtils.isNumeric(tripRefNumber)){
			throw new BusinessException(tripRefNumber + " Is Invalid!");
		}
		/*
		if(!StringUtils.contains(formattedTripNum, "-")){
			formattedTripNum = this.generateQuoteReference(Integer.parseInt(tripRefNumber));
		}
		*/
		int findTrip = tripMapper.findTripByRefNumber(formattedTripNum);
		if(findTrip == 0)
			throw new BusinessException("Trip " + tripRefNumber + " Not Exists!");
		if(findTrip > 1)
			throw new BusinessException("Duplicated trip reference number found, please add division label in front of trip reference number to identify division. Eg. MIL-");

		Trip trip = tripMapper.selectTripByRefNumber(formattedTripNum);
		if(trip == null)
			throw new BusinessException("Trip " + tripRefNumber + " Not Exists!");
		
		//Verify department 
		SecureUser currentUser = get();
		if(currentUser.isAdmin() == false && currentUser.isInDep(trip.getDepartmentId()) == false){
			log.error("User is not allowed to access the trip which not belongs to his department, trip id=" + trip.getId());
			throw new AuthorizationException("You Are Not Allowed To Link This Trip!");
		}
		
		return trip;
		/*
		CommunicationEmail email = new CommunicationEmail();
		email.setId(emailId);
		email.setReadStatus("U");
		if (equal("link",tag)) {
			email.setTripId(trip.getId());
			email.setProcessedStatus("Processed");
		}
		if (equal("unlink",tag))  {
			email.setTripId(null);
			email.setProcessedStatus("UnProcessed");
		}
		*/
		//commMapper.updateLink(email);
	}

	@Override
	public CommunicationEmail retrieveEmail(Long id) {
		CommunicationEmail email =  commMapper.selectById(id);
		if (null != email) {
			List<TripDocument> attachments = docMapper.selectTripDocumentsByEmail(email.getId(), ApplicationSession.get().getReferLanguage());
			email.setAttachments(attachments);
		}
		return email;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void sendEventNotification(Integer tripId, String eventSubject) throws BusinessException {
		Trip trip = this.retrieveTripById("", tripId, true);
		Map<String, Object> mailParam = new HashMap<String, Object>();
		mailParam.put("Trip", trip);
		
		//Retrieve events with actual date;
		List<TripEvent> eventsWithActualDate = eventMapper.selectEventWithActualDate(tripId, "1", ApplicationSession.get().getReferLanguage());		
		if(eventsWithActualDate == null || eventsWithActualDate.isEmpty()){
			throw new BusinessException("No event need to be notified!");
		}
		mailParam.put("EventList", eventsWithActualDate);
		
		TripEvent latestEvent = eventsWithActualDate.get(eventsWithActualDate.size() - 1);	
		mailParam.put("CourierName", latestEvent.getLinkedEntityName());
		mailParam.put("MobilePhone", latestEvent.getLinkedEntityCell());
		
		//added on 29 Aug 2017
		/*We only want to show the below fields when all these conditions are met
		a) trip type == hand carry
		b) and if the linked entity is a courier
		*/
		Boolean showDutiesAndTaxes = "HCS".equalsIgnoreCase(trip.getType());
		mailParam.put("showDutiesAndTaxes", showDutiesAndTaxes);
		Boolean showCouriter = "HCS".equalsIgnoreCase(trip.getType()) && "COURIER".equalsIgnoreCase(latestEvent.getLinkedEntityType());
		mailParam.put("showCouriter", showCouriter);
		
		List<TripEventNotify> notify = eventMapper.selectEventNotifies(latestEvent.getId());
		if(notify == null || notify.isEmpty()){
			throw new BusinessException("No receiptant of event " + latestEvent.getItem());
		}
		
		HashSet<String> recipientSet = new HashSet<String>();
		for(TripEventNotify tripEventNotify : notify){
			recipientSet.add(StringUtils.trimToEmpty(tripEventNotify.getEmail()).toLowerCase());
		}

		tripMapper.updateTripEventSubject(tripId, eventSubject);

		CommunicationEmail email = noteService.notify(trip.getDepartmentId(), recipientSet.toArray(new String[recipientSet.size()]), NotificationType.EMAIL, "eventnote", eventSubject, mailParam);
		email.setDepartmentId(trip.getDepartmentId());
		email.setTripId(tripId);
		email.setCategory("EVENT NOTIFY");
		commMapper.insert(email);
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public Trip createTripFromTemplate(Integer tripId) {
		Trip newTrip = touchTrip(0);
		Trip template = this.retrieveTripById("", tripId, false);
		
		template.setId(newTrip.getId());
		template.setAuthorizationNo(null);
		template.setAuthorizedBy(null);
		template.setCriticalTime(null);
		template.setNote(null);
		template.setDeliveryDate(null);
		template.setPickupDate(null);
		tripMapper.updateTrip(template);
		
		//Replicate events
		if(template.getEventTemplateId() != null){
			log.info("-> Import events from sepecified template");
			List<Integer> eventTemplates = new ArrayList<Integer>();

			TripEventTemplate temp = tripEventTemplateMapper.selectTripEventTemplateById(template.getEventTemplateId());
			if (temp != null) {
				List<TripEventTemplate> templates = tripEventTemplateMapper.selectTripEventTemplates(temp.getType(), temp.getName());
				for (TripEventTemplate item : templates) {
					eventTemplates.add(item.getId());
				}
			}
			else {
				eventTemplates.add(template.getEventTemplateId());
			}
			newTrip.setEventTemplatesTobeImported(eventTemplates);
			
			eventMapper.insertTripsWithTemplate(newTrip);

			eventMapper.insertTripEventCostWithTemplate(newTrip.getId());

			eventMapper.insertTripEventNotifyWithTemplate(newTrip.getId());
		}else{
			log.debug("--> Replicate template to a new Trip->" + template.getId());
			List<TripEvent> events = eventMapper.selectTripEvents(newTrip.getId(), ApplicationSession.get().getReferLanguage());	
			if(events != null){
				for(TripEvent event: events){
					List<TripEventNotify> notifies = eventMapper.selectEventNotifies(event.getId());
					
					event.setActualDate(null);
					event.setEstimatedDate(null);
					event.setLinkedEntity(null);
					event.setTripId(newTrip.getId());
					
					eventMapper.insertTripEvent(event);
					log.debug("--> Replicate event -> " + event.getId());
					//Replicate event notify
					if(notifies != null){
						for(TripEventNotify notify : notifies){
							notify.setEventId(event.getId());
							eventMapper.insertEventNotify(notify);
							log.debug("--> Replicate event notify -> " + notify.getId());
						}
						log.debug("--> Replicate event notify info complete -> " + event.getId());
					}
				}
				log.debug("--> Replicate events complete -> " +template.getId());
			}
		}
		
		
		
		//Make sure returned object is the new one
		template.setRefId(newTrip.getRefId());
		template.setDepartmentShortName(newTrip.getDepartmentShortName());
		template.setVersion(newTrip.getVersion());
		
		return template;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void updateTripMeasureSystem(Integer tripid, String measureCode) throws BusinessException {
		// Update EPTrip
		Trip trip = new Trip();
		trip.setId(tripid);
		trip.setSystemOfMeasure(measureCode);
		tripMapper.updateTripUOM(trip);
		log.debug("--> Trip system of measure updated, id =" + tripid);
		// Update EpFreight
		List<Freight> freights = freightMapper.selectFreightByTripId(tripid);
		if(freights != null){
			for(Freight freight : freights){
				
				String sizeUnit = null;
				String weightUnit = null;
				//convert dimension
				if("M".equalsIgnoreCase(measureCode)){
					//Dims should be CM, UOM should be KG
					sizeUnit = "CM";
					weightUnit = "KG";
				}else if("I".equalsIgnoreCase(measureCode)){
					//Dims should be Inches, UOM should be LBS
					sizeUnit = "Inches";
					weightUnit = "LBS";
				}else{
					throw new BusinessException("Unknown measure system: " + measureCode);
				}
				
				freight.setEstimatedDimension(convertFreightDims(freight.getEstimatedDimension(), sizeUnit));
				freight.setActualDimension(convertFreightDims(freight.getActualDimension(), sizeUnit));
				
				freight.setEstimatedWeight(convertFreightWeight(freight.getEstimatedWeight(), freight.getEstimatedUOM(), weightUnit));
				freight.setActualWeight(convertFreightWeight(freight.getActualWeight(), freight.getActualUOM(), weightUnit));
				//The following settings MUST be the last step!
				freight.setEstimatedUOM(weightUnit);
				freight.setActualUOM(weightUnit);
				
				//Calculate charge weight!
				freight.setEstimatedChargeWt(calculateChargeWeight(freight.getEstimatedDimension(), freight.getEstimatedPieces()));
				freight.setActualChargeWt(calculateChargeWeight(freight.getActualDimension(), freight.getActualPieces()));
				
				freightMapper.updateFreight(freight);
			}
		}
	}
	
	private BigDecimal calculateChargeWeight(String dim, Integer pieces){
		BigDecimal chargeableWeight = new BigDecimal(0);
		if(dim == null) return chargeableWeight;
		if(pieces == null) return chargeableWeight;
		
		String[] tmp = StringUtils.split(dim, " ");
		String originalUnit = tmp[1];
		
		String[] size = StringUtils.split(tmp[0], "X");
		BigDecimal volume = new BigDecimal(1);
		//L*W*H
		for(int i = 0; i < size.length; i++){
			volume = volume.multiply((new BigDecimal(Float.parseFloat(size[i]))));
		}
		//*pieces
		volume = volume.multiply(new BigDecimal(pieces));

		if("Inches".equalsIgnoreCase(originalUnit)){
			//volume / 166
			chargeableWeight =  volume.divide(new BigDecimal(166),0, BigDecimal.ROUND_HALF_UP);
		}else{
			// volume / 6000
			chargeableWeight = volume.divide(new BigDecimal(6000),0, BigDecimal.ROUND_HALF_UP);
		}		
		return chargeableWeight;
		
	}
	
	private BigDecimal convertFreightWeight(BigDecimal inputWeight, String inputUnit, String outputUnit){
		if(inputWeight == null) return null;
		if(inputUnit.equalsIgnoreCase(outputUnit)) return inputWeight;
		
		if("LBS".equalsIgnoreCase(inputUnit)){
			return UnitConversionHelper.lbsToKG(inputWeight);
		}else{
			return UnitConversionHelper.kgToLBS(inputWeight);
		}
	}
	
	private String convertFreightDims(String inputStr, String targetUnit){
		if(inputStr == null || inputStr.equals("")) return null;
		String[] tmp = StringUtils.split(inputStr, " ");
		if (tmp.length == 1) return null;
		String originalUnit = tmp[1];
		
		//Target Unit equals original unit, do nothing.
		if(targetUnit.equalsIgnoreCase(tmp[1])) return inputStr;
		
		String[] size = StringUtils.split(tmp[0], "X");
		
		for(int i = 0; i < size.length; i++){
			if("CM".equalsIgnoreCase(originalUnit)){
					//CM -> Inches
				size[i] = String.valueOf(UnitConversionHelper.cmToInch(new BigDecimal(Float.parseFloat(size[i]))));
			}else{
				//Inches -> CM
				size[i] = String.valueOf(UnitConversionHelper.inchToCM(new BigDecimal(Float.parseFloat(size[i]))));
			}
		}
			
		return StringUtils.join(size, "X") + " " + targetUnit;
	}

	@Override
	public List<Trip> retrieveTripTemplates(Integer deptId) {
		return tripMapper.selectTripTemplates(deptId);
	}

	@Override
	public void markEmailAsRead(Long emailId) {
		commMapper.updateReadStatus(emailId, "R");
	}

	@Override
	public void markEmailAsUnRead(Long emailId) {
		commMapper.updateReadStatus(emailId, "U");
	}

	@Override
	public void markEmailAsProcess(Long emailId) {
		commMapper.updateProcessStatus(emailId, "Processed");
	}

	@Override
	public void markEmailAsUnProcess(Long emailId) {
		commMapper.updateProcessStatus(emailId, "UnProcessed");
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)	
	public void updateEventToComplte(Integer[] eventId, Integer markedComplete) {
		Integer tripId = 0;
		for(Integer id : eventId){
			TripEvent event = eventMapper.selectTripEventById(id);
			tripId = event.getTripId();
			event.setMarkedComplete(markedComplete);
			eventMapper.updateEventCompleteInfo(event);
		}
		if (tripId!=null && tripId > 0)
			updateTripNextEvent(tripId);
	}

	@Override
	@Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
	public void updateTripDivision(Integer tripId, Integer departmentId) throws BusinessException {
		// Update EPTrip
		Trip trip = new Trip();
		trip.setId(tripId);
		trip.setDepartmentId(departmentId);
		tripMapper.updateTripDivision(trip);
		log.debug("--> Trip Division updated, id =" + tripId);
	}

	@Override
	public String getTripEventSubject(Integer tripId) {
		log.debug("get Event Subject of trip==>tripId:"+tripId);
		return tripMapper.getTripEventSubject(tripId, ApplicationSession.get().getReferLanguage());
	}

	@Override
	public void updateTripEventSubject(Integer tripId, String eventSubject) {
		log.debug("update Event Subject of trip==>tripId:"+tripId);
		tripMapper.updateTripEventSubject(tripId, eventSubject);
	}
}
