package ca.manitoulin.mtd.dto.tracing;

public class ProbillStoredProcedureParam {
	
	private String probillNumber;
	private String systemCode;
	private String imageId;
	private String returnCode;
	
	public String getProbillNumber() {
		return probillNumber;
	}
	public void setProbillNumber(String probillNumber) {
		this.probillNumber = probillNumber;
	}
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	@Override
	public String toString() {
		return "ProbillStoredProcedureParam [probillNumber=" + probillNumber + ", systemCode=" + systemCode
				+ ", imageId=" + imageId + ", returnCode=" + returnCode + "]";
	}
	

}
