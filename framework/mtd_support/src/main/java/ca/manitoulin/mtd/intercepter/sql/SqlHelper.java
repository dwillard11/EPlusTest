package ca.manitoulin.mtd.intercepter.sql;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.manitoulin.mtd.code.SortingOrder;

public abstract class SqlHelper implements ISqlHelper {

	abstract public String getPageSql(String originalSql, int pageNo, int pageSize, List<String> sortedBy, List<SortingOrder> sortingOrder);


	protected String combineSortingFragment(List<String> sortedBy, List<SortingOrder> sortingOrder){
		if(sortedBy == null || sortedBy.size() == 0) return StringUtils.EMPTY;
		StringBuilder sb = new StringBuilder();
		sb.append(" ORDER BY ");
		for(int i = 0; i < sortedBy.size(); i++){
			sb.append(sortedBy.get(i));
			//If sorting order is not specified, default ASC
			if(sortingOrder == null || sortingOrder.size() <= i){
				sb.append(" ").append(SortingOrder.ASC);
			}else{
				sb.append(" ").append(sortingOrder.get(i));
			}
			sb.append(",");
		}
		String combined = sb.toString();
		combined = StringUtils.left(combined, combined.length() - 1);
		return combined;
		
	}
	
	public String getTotalCountSql(String originalSql) {
		
		//replace space and next line
		String sql = originalSql.replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " "); 
		
		//If the original statement contains ORDER BY, should be removed first.
		//sql = StringHelper.replaceIgnoreCase(sql, "order by", "ORDER BY");
		//sql = StringUtils.substringBeforeLast(sql, "ORDER BY");
		
		String finalSql = new StringBuffer().append("SELECT COUNT(1) FROM (").append(sql).append(") RS_TOTAL").toString();
		return finalSql;
	}


}
