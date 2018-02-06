package ca.manitoulin.mtd.dto.misc;

import ca.manitoulin.mtd.dto.AbstractDTO;
import ca.manitoulin.mtd.dto.operationconsole.TripDocument;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class CommunicationEmail extends AbstractDTO {

    private Long id;
    private Integer tripId;
    private String refId;
    private String departmentShortName;
    private Integer departmentId;
    private String category;
    private String type;
    private String subject;
    private String content;
    private String email;
    private Date created;
    private String mailFrom;
    private String mailTo;
    private String mailCc;
    private String mailBcc;
    private String readStatus;
    private String processedStatus;
    private String label;
    private String defaultEmail;

    private Integer numberOfAttachments;

    private List<TripDocument> attachments;

    public void addAttachment(TripDocument doc) {
        if (attachments == null)
            attachments = new ArrayList<TripDocument>();
        attachments.add(doc);
    }

    public String getTripRefNo() {
        if (isEmpty(this.refId) ||isEmpty(this.departmentShortName)) return EMPTY;
        return this.departmentShortName + "-" + this.refId;
    }
    public Integer getCloneTripId() {return this.tripId;}
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        if (StringUtils.isNotEmpty(content)) {
            return content.replace("\n", "<br/>").replace("\r", "<br/>");
        }
        return content;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Integer getNumberOfAttachments() {
        return numberOfAttachments;
    }

    public void setNumberOfAttachments(Integer numberOfAttachments) {
        this.numberOfAttachments = numberOfAttachments;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getMailCc() {
        return mailCc;
    }

    public void setMailCc(String mailCc) {
        this.mailCc = mailCc;
    }

    public List<TripDocument> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<TripDocument> attachments) {
        this.attachments = attachments;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }


    public String getProcessedStatus() {
        return processedStatus;
    }

    public void setProcessedStatus(String processedStatus) {
        this.processedStatus = processedStatus;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getDepartmentShortName() {
        return departmentShortName;
    }

    public void setDepartmentShortName(String departmentShortName) {
        this.departmentShortName = departmentShortName;
    }

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

    public String getDefaultEmail() {
        return defaultEmail;
    }

    public void setDefaultEmail(String defaultEmail) {
        this.defaultEmail = defaultEmail;
    }

    public String getMailBcc() {
        return mailBcc;
    }

    public void setMailBcc(String mailBcc) {
        this.mailBcc = mailBcc;
    }
}
