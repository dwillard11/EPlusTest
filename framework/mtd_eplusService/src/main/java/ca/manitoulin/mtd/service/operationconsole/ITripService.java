package ca.manitoulin.mtd.service.operationconsole;

import ca.manitoulin.mtd.dto.misc.CommunicationEmail;
import ca.manitoulin.mtd.dto.operationconsole.*;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.exception.ConcurrentException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ITripService {
	String getDivisionCodeByTripId(Integer tripId);

	List<Freight> retrieveFreightsByQuoteId(Integer quoteId);
	
	Freight createFreight(Freight freight);
	
	Freight updateFreight(Freight freight);
	
	void removeFreight(Integer id);
	
	Freight retrieveFreightById(Integer freightId);
	
	List<Trip> retrieveQuoteListByDepartmentAndStatus(Integer departmentId, String statusCode, String searchKey, String startDate, String endDate);
	
	List<Trip> retrieveQuotes(List<Integer> deparmentIds, String statusCode);
	
	List<Trip> retrieveTrips(List<Integer> deparmentIds, String statusCode);

	List<Invoice> retrieveInvoices(List<Integer> deparmentIds, Date dateFrom, Date dateTo);
	/**
	 * Create a quote in draft status
	 * @return
	 */
	Trip touchQuote(Integer departmentId);
	
	/**
	 * create a new quote
	 * @param trip
	 * @return
	 */
	Trip createQuote(Trip trip) throws BusinessException ;
	
	/**
	 * Update a quote
	 * @param trip
	 * @return
	 */
	Trip updateQuote(Trip trip) throws BusinessException;
	
	/**
	 * Retrieve trip info
	 * @param tripId
	 * @param includeFullProfile - default false, if true, freights\ conditions ... will be included in the POJO
	 * @return
	 */
	Trip retrieveTripById(String loadType, Integer tripId, Boolean includeFullProfile);


	List<Condition> retrieveQuoteTreeByTripID(int tripID);

	Condition retrieveConditionById(Integer id) throws BusinessException;

	void addCondition(Condition condition) throws BusinessException;

	void updateCondition(Condition condition) throws BusinessException;

	void updateConditionForQuotePdf(Condition condition) throws BusinessException;

	void updateConditionSequence(Integer id, Integer categorySequence, Integer sequence) throws Exception;

	void removeCondition(Integer id) throws BusinessException;
	
	/**
	 * Clean all T&C under a trip, all import all templates
	 * @param tripId
	 * @return newly generated T&C 
	 */
	List<Condition> resetConditions(Integer tripId);
	
	/**
	 * create T&C with the specified templates.
	 * @param tripId
	 * @param quoteTemplateIdList
	 * @return
	 */
	List<Condition> addConditions(Integer tripId, List<Integer> quoteTemplateIdList);
	
	/**
	 * Prepare a trip record in draft status
	 * @return
	 */
	Trip touchTrip(Integer departmentId);
	/**
	 * Retrieve trips
	 * @param departmentId
	 * @param category OPEN/CLOSE
	 * @return
	 */
	List<Trip> retrieveTrips(Integer departmentId, String category, String searchKey, String startDate, String endDate, String searchAWB, String chargeCode);

	List<Trip> retrieveTripList(Integer departmentId, String category, String searchKey, String startDate, String endDate, String searchAWB, String eventDesc, String chargeCode, String chargeDesc);
	
	/**
	 * Update a trip record. Freights will be included.
	 * @param trip
	 * @return
	 */
	Trip saveTrip(Trip trip) throws BusinessException;
	
	List<TripEvent> retrieveEvents(Integer tripId);
	
	TripEvent createEvent(TripEvent event);
	
	TripEvent updateEvent(TripEvent event);
	
	void updateEventToComplte(Integer[] eventId, Integer markedComplete);

	void updateTripEventSequence(Integer id, Integer categorySequence, Integer sequence) throws Exception;

	void removeEvent(Integer eventId);
	
	void removeEvent(Integer[] eventIds);

	Integer hasCostDataByEventIds(String eventIds);
	
	TripEvent retrieveEventById(Integer eventId);

	List<TripEventNotify> retrieveEventNotifiesByEventId(Integer eventId);
	
	List<TripEvent> resetEvents(Integer tripId);
	
	/**
	 * create event with the specified templates.
	 * @param tripId
	 * @param eventTemplateIdList
	 * @return
	 */
	List<TripEvent> addEvents(Integer tripId, List<Integer> eventTemplateIdList);

    List<Map<String, Object>> retrieveTripEventTree(List<TripEvent> tripEvents);

    List<TripNote> retrieveNotes(Integer tripId);
    
    TripNote createNote(String text, Integer tripId);

	TripNote updateNote(TripNote note);

	void deleteNote(Integer id) throws Exception;
    
    List<TripDocument> retrieveDocuments(Integer tripId, String noEmail);
    
    /**
     * 
     * @param doc
     * @param onlyKeepOneInThisType -when TRUE, will delete other files in the document type! default FALSE
     * @return
     */
    TripDocument createDocument(TripDocument doc, boolean onlyKeepOneInThisType);
    
    TripDocument copyAttachmentToDocument(Integer docId, String newName, String newType) throws IOException;
        
    TripDocument retrieveDocumentById(Integer docId);
    
    List<TripCost> retrieveTripCostsByQuoteId(Integer tripId);

	List<TripCost> retrieveTripCostsByEventId(Integer eventId);
	
    TripCost createTripCost(TripCost tripCost);
	
    TripCost updateTripCost(TripCost tripCost);
	
	void removeTripCost(Integer id);
	
	TripCost retrieveTripCostById(Integer id);
	
	Trip savePickUp(Trip trip);
	
	Trip savePickUpAsPreview(Trip trip);

	Trip saveQuotePdfData(Trip trip);
	
	Trip saveBOL(Trip trip);
	
	List<Invoice> retrieveInvoices(Integer departmentId, Date dateFrom, Date dateTo);
	
	List<Invoice> retrieveInvoicesByTrip(Integer tripId);
	
	Invoice saveInvoice(Invoice invoice);
	
	Invoice retrieveInvoice(Integer invoiceId);
	
	Invoice prepareBlankInvoice(Integer tripId);
	
	void removeInvoice(Integer invoiceId);
	
	void replicateEmailAddressForAllEvents(List<TripEventNotify> emails, Integer currentEventId, Integer tripId);
	
	TripEventNotify createEventNotify(TripEventNotify notify);

	TripEventNotify updateEventNotify(TripEventNotify notify);
	
	void removeEventNotify(Integer notifyId);
	
	Trip retrieveTripForEditing(String loadType, Integer tripId, Boolean includeFullProfile) throws ConcurrentException;

	void releaseAllConcurrencyLock(String userId);

	List<Trip> selectLockedTripAndQuotes(List<Integer> departmentIds) throws Exception;

	void deleteTripLockStatusByID(Integer tripId) throws Exception;

    int getDocVersionByType(Integer tripid, String type);
    
    List<CommunicationEmail> retrieveEmailsByTrip(Integer tripId);


	List<CommunicationEmail> searchEmails(Integer tripId, Date dateFrom, Date dateTo,  String label, String key, String includeDelete);

	/**
     * 
     * @deprecated Use retrieveAllUnlinkedEmails(List<Integer> deparmentIds);
     * @return
     */
    List<CommunicationEmail> retrieveAllUnlinkedEmails();
    
    List<CommunicationEmail> retrieveAllUnlinkedEmails(List<Integer> deparmentIds);

	List<CommunicationEmail>  retrieveAllUnlinkedEmailsByConditions(Date dateFrom, Date dateTo, String label, String searchKey, String includeDelete, String includeProcessed, String includeOut, List<Integer> deparmentIds);
	
	void updateEmailLabel(Long emailId, String label);
    
    void markEmailAsRead(Long emailId);
    
    void markEmailAsUnRead(Long emailId);
    
    CommunicationEmail sendEmail(CommunicationEmail email, String action);
    
    void sendEventNotification(Integer tripId, String eventSubject) throws BusinessException;

	void unremoveEmail(Long id);

    void removeEmail(Long id);
    
    //void updateEmailLink(Long emailId, String tripRefNumber, String tag) throws BusinessException;
    void linkEmailToTrip(Long emailId, String tripRefNumber) throws BusinessException;
    
    void relinkEmailToTrip(Long emailId, String tripRefNumber) throws BusinessException;
    
    void unklinkEmail(Long emailId);
    
    CommunicationEmail retrieveEmail(Long id);

	List<TripDocument> retrieveTripDocumentsByType(Integer tripId, String docType);

	void removeTripDocument(Integer id);

    boolean isExistTripTemplate(String templateName) throws BusinessException;

	void saveTripTemplateName(Integer tripId, String templateName, Integer eventTemplateId);
	
	/**
	 * 
	 * @param tripId
	 * @return
	 */
	Trip createTripFromTemplate(Integer tripId);
	
	/**
	 * Update Measure Code of trip/quote
	 * @param tripid
	 * @param measureCode
	 */
	void updateTripMeasureSystem(Integer tripid, String measureCode) throws BusinessException;

    List<Trip> retrieveTripTemplates(Integer deptId);

	void markEmailAsProcess(Long emailId);

	void markEmailAsUnProcess(Long emailId);

	void updateTripDivision(Integer tripid, Integer departmentId) throws BusinessException;

	String getTripEventSubject(Integer tripId);

	void updateTripEventSubject(Integer tripId, String eventSubject);
}
