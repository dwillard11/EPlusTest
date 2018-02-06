package ca.manitoulin.mtd.dto.misc;

import ca.manitoulin.mtd.dto.AbstractDTO;


public class Announcement extends AbstractDTO {
	
	private static final long serialVersionUID = -1749577291219073941L;
	private String title;
	private String dateBegin;
	private String content;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDateBegin() {
		return dateBegin;
	}
	public void setDateBegin(String dateBegin) {
		this.dateBegin = dateBegin;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "Announcement [title=" + title + ", dateBegin=" + dateBegin + ", content=" + content + "]";
	}
	
}
