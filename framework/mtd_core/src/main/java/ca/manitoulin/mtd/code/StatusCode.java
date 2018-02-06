package ca.manitoulin.mtd.code;

/**
 * Status: active or inactive
 * @author Bob Yu
 *
 */
public enum StatusCode {

	
	YES("Y"),
	NO("N");
	
	private String value;
	
	StatusCode(String d){
		value = d;
	}

	@Override
	public String toString() {		
		return value;
	}
	
	public static StatusCode valueByCode(String v) {
		for (StatusCode a : values()) {
			if (a.toString().equals(v))
				return a;
		}
		return null;
	}
	
}
