package ca.manitoulin.mtd.code;

/**
 * User account category
 * @author Bob Yu
 *
 */
public enum UserAccountType {
	CurrentAccount("C"),
	GlobalAccount("G");

	
	private String dialet;
	
	UserAccountType(String d){
		dialet = d;
	}

	@Override
	public String toString() {		
		return dialet;
	}
	
}
