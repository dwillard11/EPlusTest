package ca.manitoulin.mtd.constant;

import java.util.Locale;

public interface ContextConstants {

	// Locale
	Locale LOCALE_DEFAULT = Locale.CANADA;
	Locale LOCALE_CANADA_FRENCH = Locale.CANADA_FRENCH;
	Locale LOCALE_CHINESE = Locale.SIMPLIFIED_CHINESE;
	Locale LOCALE_GERMAN = Locale.GERMAN;
	Locale LOCALE_SPANISH = new Locale("es", "ES");

	// Message category
	String MSG_ERROR = "ERROR";
	String MSG_INFO = "INFO";
	String MSG_WARN = "WARN";

	// Session
	String SESSION_USERPROFILE = "SESSION_USERPROFILE";
	
	// Security
	String ROLE_CATEGORY_ADMIN ="ADM";
	String ROLE_CATEGORY_FINANCE ="FIN";

	// Messages in AJAX
	String KEY_RESULT = "result";
	String KEY_SHARE_DESTINATION = "share_destination";
	String KEY_SHARE_PARAMS = "share_params";
	String KEY_MESSAGE = "msg";
	String ERROR_MSG_NO_USER_FOUND_IN_PMTUSER = "Error, no user found in pmtuser!!!";
	String ERROR_MSG_UID_SHOULD_NOT_BE_NULL = "Error, UID can not be null!!!";

	// AJAX response
	String RESPONSE_SUCCESS = "success";
	String RESPONSE_SHARE_SUCCESS = "share_success";
	String RESPONSE_ERROR = "error";
	String RESPONSE_DUPLICATE = "duplicate";

	// Proxy parameters
	String PARAM_ACCOUNT_TYPE = "PARAM_ACCOUNT_TYPE";
	String PARAM_DATE_BEGIN = "PARAM_DATE_BEGIN";
	String PARAM_DATE_END = "PARAM_DATE_END";
	String CRUD_OBJECT = "CRUD_OBJECT";
	String PARAM_SCHEMA = "schema";
	String PARAM_USERNAME = "username";
	String PARAM_USERID = "userid";
	String PARAM_COMPANY = "company";
	String PARAM_INFORMATION = "information";
	String PARAM_PROBILLNO = "probillNo";
	
	String PARAM_MESSAGEIDS = "PARAM_MESSAGEIDS";
	String PARAM_MESSAGE_ID = "messageId";
	String PARAM_MESSAGE_DETAIL = "PARAM_MESSAGE_DETAIL";
	String PARAM_MESSAGE_SUBSCRIPTION = "PARAM_MESSAGE_SUBSCRIPTION";
	String PARAM_MESSAGE_SUBSCRIPTION_IDS = "PARAM_MESSAGE_SUBSCRIPTION_IDS";
	String PARAM_MESSAGE_SUBSCRIPTION_ID = "PARAM_MESSAGE_SUBSCRIPTION_ID";

	String PARAM_ADVANCESEARCH = "advanceSearch";
	String PARAM_MESSAGE_LIST = "PARAM_MESSAGE_LIST";
	String PARAM_ACTIVITY = "activity";

	String PARAM_ACTIVITYDATESTR = "activityDateStr";
	String PARAM_ACTIVITYTIMESTR = "activityTimeStr";
	String PARAM_ACTIVITYDATE = "activityDate";
	String PARAM_ACTIVITYTIME = "activityTime";
	String PARAM_SORTCONDITION = "sortCodition";
	String PARAM_SORTREVERSE = "sortReverse";
	String PARAM_USER_GROUS = "groups";
	String PARAM_USER_SELECTED_GROUS = "selectedGroup";
	String PARAM_USER_TOP_MENU = "topMenus";
	String PARAM_USER_FIRST_LEVEL_SIDE_MENU = "firstLevelSideMenus";
	String PARAM_USER_SECOND_LEVEL_SIDE_MENU = "secondLevelSideMenus";
	String PARAM_USER_SIDE_MENUS = "sideMenus";

	String PARAM_RECORDS = "records";
    String PARAM_CLIENTS_RECORDS = "clients";
    String PARAM_CONSIGNEES_RECORDS = "consignees";
    String PARAM_CONSIGNEES2_RECORDS = "consignees2";
    String PARAM_CONSIGNEES3_RECORDS = "consignees3";
    String PARAM_SHIPPERS_RECORDS = "shippers";
    String PARAM_SHIPPERS2_RECORDS = "shippers2";
    String PARAM_SHIPPERS3_RECORDS = "shippers3";
    String PARAM_BROKERS_RECORDS = "brokers";
    String PARAM_BROKERS2_RECORDS = "brokers2";
    String PARAM_AGENTS_RECORDS = "agents";
    String PARAM_CONTACTS_RECORDS = "contacts";
    String PARAM_SELECTED_CLIENT = "selected_client";
    String PARAM_SELECTED_CONSIGNEE = "selected_consignee";
    String PARAM_SELECTED_CONSIGNEE2 = "selected_consignee2";
    String PARAM_SELECTED_CONSIGNEE3 = "selected_consignee3";
    String PARAM_SELECTED_SHIPPER = "selected_shipper";
    String PARAM_SELECTED_SHIPPER2 = "selected_shipper2";
    String PARAM_SELECTED_SHIPPER3 = "selected_shipper3";
    String PARAM_SELECTED_BROKER = "selected_broker";
    String PARAM_SELECTED_BROKER2 = "selected_broker2";
    String PARAM_SELECTED_BILLED_CLIENT = "selected_billed_client";
    String PARAM_SELECTED_DROP_CONSIGNEE = "selected_drop_consignee";
    String PARAM_SELECTED_DROP_SHIPPER = "selected_drop_shipper";
    String PARAM_SELECTED_AGENT = "selected_agent";
    String PARAM_SELECTED_CONTACT = "selected_contact";
    String PARAM_RECORDS_EXTRA = "records_extra";
	String PARAM_TOTALRECORD = "totalRecord";
	String PARAM_BEGIN_DATE = "beginDate";
	String PARAM_END_DATE = "endDate";

	String PARAM_PROBILL = "probill";
	String PARAM_TRACING_BOL = "PARAM_TRACING_BOL";
	String PARAM_TRACING_SHIPPER = "PARAM_TRACING_SHIPPER";
	String PARAM_TRACING_PO = "PARAM_TRACING_PO";
	String PARAM_TRACING_PICKUPNUMBER = "PARAM_TRACING_PICKUPNUMBER";
	String PARAM_TRACING_PICKUPSTATUS = "PARAM_TRACING_PICKUPSTATUS";
	String PARAM_MESSAGE_IDS = "PARAM_MESSAGE_IDS";
	String PARAM_MESSAGESUBSCRIPTION_ADVANCESEARCH = "PARAM_MESSAGESUBSCRIPTION_ADVANCESEARCH";

	String PARAM_MESSAGESUBSCRIPTIONS = "PARAM_MESSAGESUBSCRIPTIONS";
	String PARAM_USER = "sessionuser";

	String PARAM_NUMBER_TYPE_BOL = "bol";
	String PARAM_NUMBER_TYPE_SHIPPER = "shipper";
	String PARAM_NUMBER_TYPE_POL = "poNumber";
	String PARAM_RETURN_CODE = "PARAM_RETURN_CODE";
	String PARAM_IMAGE_ID = "PARAM_IMAGE_ID";
	String PARAM_IMAGES = "PARAM_IMAGES";

	// Use this schema when you test!!!
	String SCHEMA_TEST = "LTL400TST3";
	String SCHEMA_PRD = "LTL400V1A3";

	int LENGTH_OF_PROBILLNO = 25;

	String LAKEY_PROBILLNUMBER_MUST_BE_NUMBERIC = "1790";
	String LAKEY_PROBILLNUMBER_NOT_FOUND = "1707";
	String LAKEY_BOL_SHIPPER_PO_NEED_VALUE = "2865";
	String LAKEY_NO_MATCH_FOUND_FOR_PICKUPNUMBER = "error.tracing.noMatchFoundForPickupNumber";
	String LAKEY_NO_MATCH_FOUND_FOR_BOL = "error.tracing.noMatchFoundForBOL";
	String LAKEY_NO_MATCH_FOUND_FOR_SHIPPERNUMBER = "error.tracing.noMatchFoundForShipperNumber";
	String LAKEY_NO_MATCH_FOUND_FOR_PURCHASEORDERNUMBER = "error.tracing.noMatchFoundForPurchaseOrderNumber";

	String LAKEY_YOU_MUST_ENTER_AN_EMAIL_ADDRESS = "6158";
	String LAKEY_YOU_APPEAR_TO_HAVE_AN_INVALID_EMAIL_ADDRESS_ENTERED = "2541";
	String LAKEY_YOU_MUST_SELECT_AN_OPTION_FROM_ACTIVITY_NOTIFICATION = "6141";
	String LAKEY_YOU_MUST_SELECT_A_METHOD_OF_NOTIFICATION = "6142";
	String LAKEY_PLEASE_SELECT_ONLY_ONE_ACTIVITY_NOTIFICATION = "6161";
	String LAKEY_PLEASE_SPECIFY_DATE_RANGE = "error.tracing.specifyDateRange";
	String LAKEY_STARTING_DATE_SHOULD_BE_EARLIER_THAN_ENDING_DATE = "error.tracing.StartDateAfterEndDate";
	String LAKEY_SPECIFY_DATA_RANGE_WITHIN_ONE_MONTH = "error.tracing.SpecifyDataRangeWithinOneMonth";
	String LAKEY_SPECIFY_DATA_RANGE_WITHIN_FOUR_MONTH = "error.tracing.SpecifyDataRangeWithinFourMonth";
	String LAKEY_NO_RECORDS_FOUND = "8624";
	String TRIGGER_RESCHEDULED_STATUS = "RSC";
	String TRIGGER_DELIVERY_STATUS = "DLVY";

	String NOTIFICATION_BOTH_EMAIL_MESSAGECENTRAL = "B";
	String NOTIFICATION_EMAIL = "E";
	String NOTIFICATION_MESSAGECENTRAL = "M";

	int DEFAULT_DATE_RANGE = 30;

	String PARAM_ROLES_TABLE = "roles_table";
	String PARAM_DEPARTMENTS = "departments";

    String LAKEY_ERROR_COMMON_REQUIED = "error.common.required";
    String LAKEY_ERROR_SEC_USER_NOT_EXIST = "error.sec.userNotExist";
    String LAKEY_ERROR_COMMON_DUPLICATED = "error.common.duplicated";

	String CUSTOMER_TYPE_CLIENT = "CLIENT";
	String CUSTOMER_TYPE_CONSIGNEE = "CONSIGNEE";
    String CUSTOMER_TYPE_SHIPPER = "SHIPPER";
    String CUSTOMER_TYPE_BROKER = "BROKER";
    String CUSTOMER_TYPE_ALL = "ALL";

}
