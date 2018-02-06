package ca.manitoulin.mtd.code;

/**
 * Document category, used in probill image
 * @author Bob Yu
 *
 */
public enum DocumentType {
	BOL("Bill of Lading Images"),
	POD("Proof of Delivery Images"),
	IPOD("Final Proof of Delivery Images"),
	EPOD("EPOD Images"),
	BRD("Back Door Receiving Report"),
	CUS("Customs Documents Images"),
	PAPS("PAPS Images"),
	PARS("PARS Images"),
	OTHER("Other Images");
	
	private String dialet;
	
	DocumentType(String d){
		this.dialet = d;
	}

	@Override
	public String toString() {		
		return dialet;
	}
	
	public static DocumentType valueByCode(String v) {
		for (DocumentType a : values()) {
			if (a.name().equals(v))
				return a;
		}
		return OTHER;
	}
}
