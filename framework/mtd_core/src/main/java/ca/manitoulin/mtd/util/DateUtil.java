package ca.manitoulin.mtd.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static final String LONG_DATE_FORMAT = "yyyy/MM/dd";
	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String TIME_SHORT_FORMAT = "HH:mm";
	public static final String DB_DATE_FORMAT = "yyyyMMdd";
	public static final String DB_TIME_FORMAT = "HHmm";
	public static final String DB_DATE_FORMAT_MMDDYYYY = "MMddyy";
    public static final String DB_DATE_FORMAT_DDMMYYYY = "dd/MM/yyyy";
    public static final String LONG_DATE_FORMAT_SHOW = "yyyy-MM-dd";
	public static final String DATE_TIME_SHOW = "yyyy-MM-dd HH:mm";
    public static final String DATE_TIME_FARMAT_DDMMYYHHMM = "dd/MM/yyyy HH:mm";
	public static final String DATE_TIME_FARMAT_YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
	public static final String DATE_TIME_FARMAT_HHMMDDMMYYYY = "HH:mm dd/MM/yyyy";
	private static final Logger log = Logger.getLogger(DateUtil.class);

	public static String toDateString(Date date, String pattern) {
		if (date == null)
			return StringUtils.EMPTY;
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	public static Date toDate(String dateStr, String pattern) {
		if (StringUtils.isBlank(dateStr) || "0".equals(dateStr)) {
			return null;
		}
		
		if(dateStr.length() < pattern.length()){
			dateStr = StringUtils.leftPad(dateStr, pattern.length(), "0");
		}
		
		
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date date;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			log.error("Unable to parse date: " + dateStr);
			return null;
		}
		return date;
	}

	public static String toDayOfWeek(Date date){
		if(date == null) return StringUtils.EMPTY;
		DateFormat df = new SimpleDateFormat("EEEE");
		return df.format(date);
	}
	
	public static String changeFormat(String dateStr, String originalPattern, String toPattern){
		Date date = DateUtil.toDate(dateStr, originalPattern);
		return  DateUtil.toDateString(date, toPattern);
	}

	public static String displayDBDate(String dateStr){
		return changeFormat(dateStr, DB_DATE_FORMAT,  LONG_DATE_FORMAT);
	}
	
	public static String displayDBTime(String dateStr){
		return changeFormat(dateStr, DB_TIME_FORMAT,  TIME_SHORT_FORMAT);
	}
}
