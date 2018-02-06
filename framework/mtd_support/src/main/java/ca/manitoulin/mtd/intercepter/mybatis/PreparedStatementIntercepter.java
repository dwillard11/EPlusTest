package ca.manitoulin.mtd.intercepter.mybatis;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.log4j.Logger;

import ca.manitoulin.mtd.code.DatabaseType;
import ca.manitoulin.mtd.constant.ApplicationConstants;
import ca.manitoulin.mtd.dto.Page;
import ca.manitoulin.mtd.intercepter.sql.DB2sqlHelper;
import ca.manitoulin.mtd.intercepter.sql.ISqlHelper;
import ca.manitoulin.mtd.intercepter.sql.MysqlHelper;
import ca.manitoulin.mtd.util.ReflectionUtil;


/**
 * Pagination  intercepter for Mybatis 
 * 
 * @author Bob Yu
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PreparedStatementIntercepter implements Interceptor {
	private static final Logger log = Logger.getLogger(PreparedStatementIntercepter.class);

	@SuppressWarnings("rawtypes")
	public Object intercept(Invocation invocation) throws Throwable {        
        
		log.debug("- PreparedStatementInterceptor intercept BEGIN-");
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();    
        BoundSql boundSql = statementHandler.getBoundSql();  
        Object obj = boundSql.getParameterObject();
        
        Page page = null;
        
        
        if(obj != null && obj instanceof Map){
        	Map paramMap = (Map) obj;
        	//Indicate @Param("FILTER")
        	/*
        	String[] filter = (String[]) paramMap.get(ApplicationConfig.PARAM_FILTER);
        	if(filter != null && filter.length > 0 && StringUtils.isNotEmpty(filter[0])){
        		log.debug("-- @Param(\"FILTER\") indicated, begin swap sql statement");
        		doDataFilter(filter[0], invocation);
        	}
        	*/
        	//Indicate @Param(PAGE)
        	/*page = (Page) paramMap.get(ApplicationConstants.PARAM_PAGE);
        	if(page != null){
        		log.debug("-- @Param(\"PAGE\") indicated, automatically paging will start");
        	}*/
        }
        
        //Whether the parameter is an instance of Page?
        if (obj instanceof Page<?>) {  
        	log.debug("-- parameter is a Page object, automatically paging will start");
        	page = (Page) obj;
        }
        
        //Do paging
        if(page != null){        	
        	doPaging(page, invocation);  
        }
        
        log.debug("- PreparedStatementInterceptor intercept COMPLETE-");
		return invocation.proceed(); 
	}

	/*
	private void doDataFilter(String filterStatement, Invocation invocation) {
		log.debug("-- data filter interceptor beging working ");
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();    
        BoundSql boundSql = statementHandler.getBoundSql(); 
        String originalSql = boundSql.getSql();  
        ISqlHelper pageHelper = getSQLHelper();   
        String newSql = pageHelper.getDataFilterSql(originalSql, filterStatement);
        ReflectionUtil.setFieldValue(boundSql, "sql", newSql); 
        
		log.debug("-- data filter interceptor done ");
		
	}
*/
	
	
	private void doPaging(Page page, Invocation invocation) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		
		log.debug("-- paging interceptor beging working ");
		
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();    
        BoundSql boundSql = statementHandler.getBoundSql(); 
        
		ISqlHelper pageHelper = getSQLHelper();    	
        
        //Query number of total records, to calculate number of pages.
        if(page.isAutoCalTotalRecord()){
        	log.debug("--> automatically calculate total count");
        	int totalRecord = getTotalRecord(page, pageHelper, statementHandler, (Connection)invocation.getArgs()[0]);
        	page.setTotalRecord(totalRecord);
        }
        
        //Wrap the statement with record limitation.      
        String originalSql = boundSql.getSql();  
        String newSql = pageHelper.getPageSql(originalSql, page.getPageNo(), page.getPageSize(), page.getSortedBy(), page.getSortingOrder());
        
        
        ReflectionUtil.setFieldValue(boundSql, "sql", newSql);  
        
        log.debug("-- paging interceptor done ");
	}
	
	private int getTotalRecord(Page<?> page, ISqlHelper pageHelper, StatementHandler statementHandler, Connection connection) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		StatementHandler delegate = (StatementHandler) ReflectionUtil.obtainFieldValue(statementHandler, "delegate");            
        MappedStatement mappedStatement = (MappedStatement) ReflectionUtil.obtainFieldValue(delegate, "mappedStatement");
        
        BoundSql boundSql = statementHandler.getBoundSql();  
        String sql = boundSql.getSql();   
        String countSql = pageHelper.getTotalCountSql(sql);

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();  
        Object paramObject = boundSql.getParameterObject();
        BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, paramObject); 
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, paramObject, countBoundSql); 
        
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int totalRecord = 0;
		try {
			pstmt = connection.prepareStatement(countSql);
			parameterHandler.setParameters(pstmt);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				totalRecord = rs.getInt(1);
			}
			log.debug("--> number of total records is: " + totalRecord);
		} catch (SQLException e) {
			log.error("UNABLE TO COUNT TOTAL RECORD:", e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				log.error("UNABLE TO CLOSE CONNECTION:", e);
			}
		}
		return totalRecord;

	}
	
	private ISqlHelper getSQLHelper(){
		ISqlHelper pageHelper;
		//Choose correct DB helper
        String db = ApplicationConstants.getConfig("jdbc.type");
        DatabaseType databaseType = DatabaseType.valueOf(db);
        if(databaseType == null){
        	String err = "CONFIGRATION ERROR: jdbc.type in config.properties is not defined";
        	log.error(err);
        	throw new RuntimeException(err);
        }
        
        switch(databaseType){
        case ORACLE:
        	throw new RuntimeException("Oracle is not supported yet!");
        case MYSQL:
        	pageHelper = new MysqlHelper();
        	break;
        case DB2:
        	pageHelper = new DB2sqlHelper();
        	break;
        default:
        	String err = "CONFIGRATION ERROR: jdbc.type in config.properties is not supported: " + databaseType;
        	log.error(err);
        	throw new RuntimeException(err);
        }
        return pageHelper;
	}
	
	public Object plugin(Object target) {
		return Plugin.wrap(target, this); 
	}

	public void setProperties(Properties arg0) {
		//do nothing

	}}
