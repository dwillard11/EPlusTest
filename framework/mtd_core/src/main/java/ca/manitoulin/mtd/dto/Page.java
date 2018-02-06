package ca.manitoulin.mtd.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import ca.manitoulin.mtd.code.SortingOrder;
import ca.manitoulin.mtd.constant.ApplicationConstants;

/**
 * Container for pagination
 * @author Bob Yu
 */
public class Page<T> implements Serializable{

	private int pageNo = 1;
	private int pageSize;
	private int totalRecord;

	private List<T> results;
	private Map<String, Object> params;
	
	private Boolean autoCalTotalRecord = true;

	private List<String> sortedBy;
	private List<SortingOrder> sortingOrder;
	



	public Page(){
		params = new HashMap<String, Object>();		
		results = Collections.EMPTY_LIST;
	}
	
	/**
	 * 
	 * @param pageNo 
	 * @param pageSize 
	 */
	public Page(int pageNo,int pageSize){
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		params = new HashMap<String, Object>();		
		results = Collections.EMPTY_LIST;
	}
	
	/**
	 * 
	 * @param pageNo 
	 * @param pageSize
	 * @param params
	 */
	public Page(int pageNo,int pageSize,HashMap<String, Object> params){
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.params = params;
		results = Collections.EMPTY_LIST;
	}
	
	/**
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param params
	 * @param sortedBy
	 * @param sortingOrder
	 */
	public Page(int pageNo,int pageSize,HashMap<String, Object> params, List<String> sortedBy, List<SortingOrder> sortingOrder){
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.params = params;
		this.sortedBy = sortedBy;
		this.sortingOrder = sortingOrder;
		results = Collections.EMPTY_LIST;
	}	
	
	/**
	 * set query mechanism, when executing query, inspector will append where key= param at the end of the statement.
	 * @param key field name using in SQL
	 * @param param filter value
	 */
	public void setParameter(String key, Object param){
		params.put(key, param);
	}
	

	
	/**
	 * calculate the number of total page
	 * @return
	 */
	@JsonIgnore
	public int getTotalPage(){
		int size = getPageSize();
	    int totalPage = totalRecord % size==0 ? totalRecord / size : (totalRecord / size + 1);  
	    return totalPage;
	}
	
	/**
	 * if pageSize is 0, it use default size
	 * @return
	 */
	public int getPageSize() {
		if(pageSize == 0)
			pageSize = Integer.parseInt(ApplicationConstants.getConfig("page.defaultsize"));
		return pageSize;
	}
	
	
	public Boolean isAutoCalTotalRecord() {
		return autoCalTotalRecord;
	}

	public void setAutoCalTotalRecord(Boolean autoCalTotalRecord) {
		this.autoCalTotalRecord = autoCalTotalRecord;
	}	
	public Map<String, Object> getParams() {
		return params;
	}

	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	public List<T> getResults() {
		return results;
	}
	public void setResults(List<T> results) {
		this.results = results;
	}

	public List<String> getSortedBy() {
		return sortedBy;
	}

	public void setSortedBy(List<String> sortedBy) {
		this.sortedBy = sortedBy;
	}

	public List<SortingOrder> getSortingOrder() {
		return sortingOrder;
	}

	public void setSortingOrder(List<SortingOrder> sortingOrder) {
		this.sortingOrder = sortingOrder;
	}

	



	
}
