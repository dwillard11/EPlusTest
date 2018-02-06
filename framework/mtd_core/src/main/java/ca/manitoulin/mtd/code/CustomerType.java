package ca.manitoulin.mtd.code;

/**
 * customer type
 * @author Bob Yu
 * @deprecated
 *
 */
public enum CustomerType {
	AIRLINE("AIRLINE"),
	BROKER("BROKER"),
	CLIENT("CLIENT"),
	CONSIGNEE("CONSIGNEE"),
	COURIER("COURIER"),
	DELIVERYAGENT("DELIVERYAGENT"),
	GENERALINFO("GENERALINFO"),
	PICKUP("PICKUP"),
	SHIPPER("SHIPPER");

	private String value;
	
	CustomerType(String d){
		value = d;
	}

	@Override
	public String toString() {		
		return value;
	}
	
	public static CustomerType valueByCode(String v) {
		for (CustomerType a : values()) {
			if (a.toString().equals(v))
				return a;
		}
		return null;
	}
}
