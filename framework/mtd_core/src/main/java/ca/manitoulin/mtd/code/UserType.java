package ca.manitoulin.mtd.code;

/**
 * User category
 * @author Bob Yu
 *
 */
public enum UserType {
	EMPLOYEE("EMPLOYEE"),
	CUSTOMER("CUSTOMER"),
	PARTNER("PARTNER"),
	SALES("SALES"),
	ADMIN("ADMIN");
	
	private String dialet;
	
	UserType(String d){
		dialet = d;
	}

	@Override
	public String toString() {		
		return dialet;
	}
	
}
