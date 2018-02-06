package ca.manitoulin.mtd.intercepter.sql;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.manitoulin.mtd.code.SortingOrder;

public class DB2sqlHelper extends SqlHelper implements ISqlHelper {
	
	public String getPageSql(String originalSql, int pageNo, int pageSize, List<String> sortedBy, List<SortingOrder> sortingOrder) {
		int offset = (pageNo - 1) * pageSize;
		
		String sortingSegment = combineSortingFragment(sortedBy, sortingOrder);
		
		StringBuilder sb = new StringBuilder("SELECT RS_.* FROM (SELECT ROW_.*, rownumber() over(");
		
		if(!StringUtils.isEmpty(sortingSegment)){
			sb.append(sortingSegment);
		}
		sb.append(" ) as ROWNUM_ FROM (")
			.append(originalSql).append(") ROW_ ) RS_ WHERE ROWNUM_ > ").append(offset)
			.append(" AND ROWNUM_ <= ").append(offset + pageSize);
		
		String finalSql = sb.toString();
		
		return finalSql;
	}



	
}
