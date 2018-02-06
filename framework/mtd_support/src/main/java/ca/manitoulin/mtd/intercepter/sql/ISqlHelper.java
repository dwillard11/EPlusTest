package ca.manitoulin.mtd.intercepter.sql;

import java.util.List;

import ca.manitoulin.mtd.code.SortingOrder;

public interface ISqlHelper {

	String getPageSql(String originalSql, int pageNo, int pageSize, List<String> sortedBy, List<SortingOrder> sortingOrder);
	String getTotalCountSql(String originalSql);
	//String getDataFilterSql(String originalSql, String filter);
	
}
