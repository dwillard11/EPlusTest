package ca.manitoulin.mtd.code;

import org.apache.commons.lang3.StringUtils;

/**
 * System code
 * @author Bob Yu
 *
 */
public enum SystemCode {
	
	MULTIMODAL("MLS"),
	TANDEM("TAN"),
	DUKE("DUK"),
	RAINBOW("RTL"),
	MANITOULIN("MAN");
	
	private String dialet;
	
	SystemCode(String d){
		dialet = d;
	}

	@Override
	public String toString() {		
		return dialet;
	}
	
	public static SystemCode valueByCompanyCode(String v) {
		if(v == null) return null;
		v = StringUtils.upperCase(v);
		for (SystemCode a : values()) {
			if (a.name().equals(v))
				return a;
		}
		return SystemCode.MANITOULIN;
	}
	
	

}
