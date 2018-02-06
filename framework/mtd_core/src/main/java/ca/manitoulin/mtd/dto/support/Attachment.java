package ca.manitoulin.mtd.dto.support;

import static ca.manitoulin.mtd.code.DocumentType.valueByCode;
import static org.apache.commons.lang3.StringUtils.remove;

import java.io.File;

import ca.manitoulin.mtd.dto.AbstractDTO;
import ca.manitoulin.mtd.util.DateUtil;

/**
 * Attachment info
 * @author Bob Yu
 *
 */
public class Attachment extends AbstractDTO {

	private Integer id;
	private String originalFileName;
	private String fileType;
	private Integer filesize;
	private String scanDate;
	private String description;
	private String filename;

	
	private File file;
	
	public String getImageTableHead() {
		return valueByCode(this.fileType).toString();
	}
	
	public String getFormattedScanDate(){
		return DateUtil.displayDBDate(scanDate);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Integer getFilesize() {
		return filesize;
	}
	public void setFilesize(Integer filesize) {
		this.filesize = filesize;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}

	public String getScanDate() {
		return scanDate;
	}

	public void setScanDate(String scanDate) {
		this.scanDate = scanDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	
	
}
