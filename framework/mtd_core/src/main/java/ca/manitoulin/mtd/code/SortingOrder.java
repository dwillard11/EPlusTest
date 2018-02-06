package ca.manitoulin.mtd.code;

/**
 * Sorting order option
 * @author Bob Yu
 */
public enum SortingOrder {
	ASC("ASC"),
	DESC("DESC");
	
	private String dialet;
	
	SortingOrder(String d){
		dialet = d;
	}

	@Override
	public String toString() {		
		return dialet;
	}
	
}
