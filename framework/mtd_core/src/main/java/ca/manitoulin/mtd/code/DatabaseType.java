package ca.manitoulin.mtd.code;

/**
 * Database category, used in Mapper inspector
 * @author Bob Yu
 *
 */
public enum DatabaseType {
	ORACLE("ORACLE"),
	DB2("DB2"),
	MYSQL("MYSQL");
	
	private String dialet;
	
	DatabaseType(String d){
		dialet = d;
	}

	@Override
	public String toString() {		
		return dialet;
	}
	
}
