package ca.manitoulin.mtd.code;

/**
 * privilege type
 * @author Bob Yu
 *
 */
public enum PrivilegeType {

	MENU("M"),
	FUNCTION("F");
	
	private String value;
	
	PrivilegeType(String d){
		value = d;
	}

	@Override
	public String toString() {		
		return value;
	}
	
	public static PrivilegeType valueByCode(String v) {
		for (PrivilegeType a : values()) {
			if (a.toString().equals(v))
				return a;
		}
		return null;
	}
}
