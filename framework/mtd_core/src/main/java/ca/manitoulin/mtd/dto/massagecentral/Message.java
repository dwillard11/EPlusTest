package ca.manitoulin.mtd.dto.massagecentral;

import static ca.manitoulin.mtd.util.DateUtil.LONG_DATE_FORMAT;
import static ca.manitoulin.mtd.util.DateUtil.TIME_SHORT_FORMAT;
import static ca.manitoulin.mtd.util.DateUtil.toDateString;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ca.manitoulin.mtd.dto.AbstractDTO;

public class Message extends AbstractDTO {

	private Long id;
	private Long probillNo;
	private Date activityDate;
	private Date activityTime;
	private String activity;
	private String information;
	private char auDelete;
	
	private List<Map<String, String>> detailData;

	/**
	 * Prefix "0" to probill number
	 * @return
	 */
	public String getFormattedProbillNumber(){
		return StringUtils.leftPad(probillNo+"", 10, "0");
	}
	public char getAuDelete() {
		return auDelete;
	}

	public void setAuDelete(char auDelete) {
		this.auDelete = auDelete;
	}

	public String getFormattedActivityDate() {
		return toDateString(activityDate, LONG_DATE_FORMAT);

	}

	public String getFormattedActivityTime() {
		return toDateString(activityTime, TIME_SHORT_FORMAT);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProbillNo() {
		return probillNo;
	}

	public void setProbillNo(Long probillNo) {
		this.probillNo = probillNo;
	}

	public Date getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(Date activityDate) {
		this.activityDate = activityDate;
	}

	public Date getActivityTime() {
		return activityTime;
	}

	public void setActivityTime(Date activityTime) {
		this.activityTime = activityTime;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", probillNo=" + probillNo + ", activityDate=" + activityDate + ", activityTime="
				+ activityTime + ", activity=" + activity + ", information=" + information + ", auDelete=" + auDelete
				+ "]";
	}

	public List<Map<String, String>> getDetailData() {
		return detailData;
	}

	public void setDetailData(List<Map<String, String>> detailData) {
		this.detailData = detailData;
	}


}
