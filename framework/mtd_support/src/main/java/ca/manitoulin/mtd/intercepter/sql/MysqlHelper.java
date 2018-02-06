package ca.manitoulin.mtd.intercepter.sql;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.manitoulin.mtd.code.SortingOrder;

public class MysqlHelper extends SqlHelper implements ISqlHelper {
	
	public String getPageSql(String originalSql, int pageNo, int pageSize, List<String> sortedBy, List<SortingOrder> sortingOrder) {
		int offset = (pageNo - 1) * pageSize;
		int limit = pageSize;
		
		String sortingSegment = combineSortingFragment(sortedBy, sortingOrder);
		
		StringBuilder sb = new StringBuilder(originalSql);
		
		if(!StringUtils.isEmpty(sortingSegment)){
			sb.append(sortingSegment);
		}
		
		String finalSql = sb.append(" limit ")
				.append(offset).append(", ").append(limit).toString();
		return finalSql;
	}

	
}
