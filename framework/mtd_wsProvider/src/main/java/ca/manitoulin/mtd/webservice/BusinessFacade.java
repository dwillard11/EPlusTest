package ca.manitoulin.mtd.webservice;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;

import ca.manitoulin.mtd.dto.RequestContext;
import ca.manitoulin.mtd.dto.ResultContext;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.exception.BusinessException;
import ca.manitoulin.mtd.util.ReflectionUtil;
import ca.manitoulin.mtd.util.json.JsonHelper;

/**
 * Generic facade, uses reflection to invoke back-end service
 * @author Bob Yu
 *
 */
@Service
@WebService(endpointInterface="ca.manitoulin.mtd.webservice.IBusinessFacade")
public class BusinessFacade extends ApplicationObjectSupport implements IBusinessFacade {

	private static final Logger log = Logger.getLogger(BusinessFacade.class);
	
	/* (non-Javadoc)
	 * @see ca.manitoulin.mtd.webservice.IBusinessFacade#execute(ca.manitoulin.mtd.dto.RequestContext)
	 */
	@Override
	public String execute(String contextStr) throws IOException {
		
		RequestContext ctx = JsonHelper.toObject(contextStr, RequestContext.class);
		
        Class serviceClass = ctx.getServiceClass();
        String methodInvoked = ctx.getMethodName();
		
		ResultContext result = new ResultContext();
        
    	
		//check the user session is valid or not
		boolean isValidRequest = true;
		if(serviceClass == null || methodInvoked == null){
			log.error("REQUEST REJECTED: SERVICE OR METHOD IS NOT PROVIDED");
			isValidRequest = false;
		}
			
		//TODO user validation
		SecureUser user = ctx.getUserProfile();
		if(user == null)
			isValidRequest = false;
		
		if(!isValidRequest){
			log.error(".");
            log.error("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            log.error("+   USER ID = " + ((user == null) ? null : user.getId()));
            log.error("+   SERVICE = " + serviceClass.getName());
            log.error("+   METHOD = " + methodInvoked);
            log.error("+   INVALID REQUEST!");
            log.error("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            log.error(".");
            result.setSuccess(Boolean.FALSE);
            result.addErrorMessage("Invalid request!");
            return JsonHelper.toJsonString(result);
		}
		
        
        //Do reflection                
        long begin = System.currentTimeMillis();
        try{
        	            
            Object target = this.getApplicationContext().getBean(serviceClass);
            //invoke the service
        	result = (ResultContext) ReflectionUtil.invokeMethod(target, methodInvoked, new Class[]{ctx.getClass()}, new Object[]{ctx});
        	
        	

        	
        	result.setSuccess(Boolean.TRUE);
        	
           	log.info(".");
            log.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            log.info("+   USER ID = " + user.getId());
            log.info("+   SERVICE = " + serviceClass.getName());
            log.info("+   METHOD = " + methodInvoked);
            log.info("+   TIME = " + (System.currentTimeMillis() - begin) / 1000 + " SECONDS");
            log.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            log.info(".");
            
        } catch (Exception e){
        	
        	result.setSuccess(Boolean.FALSE);

        	Throwable rootCause = null;
        	
        	if(e instanceof InvocationTargetException){
        		InvocationTargetException itException = (InvocationTargetException) e;
        		Throwable t = itException.getTargetException();
        		if(t instanceof BusinessException){
        			BusinessException bizExp = (BusinessException) t;
        			rootCause = bizExp;
                	result.addErrorMessage(bizExp.getMessageKey(), bizExp.getMessageArgs());
        		}else{
        			rootCause = t;
        			result.addErrorMessage("Unknown error occured in Webservice side");
        			
        		}
        	}else{
        		rootCause = e;
            	result.addErrorMessage("Unhandled error occured in Webservice side");
        	}
        	
        	
        	
        	//logging
        	log.error(".");
            log.error("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            log.error("+   USER ID = " + user.getId());
            log.error("+   SERVICE = " + serviceClass.getName());
            log.error("+   METHOD = " + methodInvoked);
            log.error("+");
            HashMap params = ctx.getParameters();
            if (params != null) {
                Set paramsSet = params.keySet();
                Iterator it = paramsSet.iterator();
                while (it.hasNext()) {
                    String paramKey = (String) it.next();
                    Object obj = params.get(paramKey);
                    log.error("+   [PARAMETER]: [KEY]=" + paramKey + " [VALUE]=" + obj);
                }
            }
            log.error("+ " + rootCause);
            log.error("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            log.error(".");       	
        }finally{
        	
        	result.setServiceClass(serviceClass);
            result.setMethodName(methodInvoked);
            //If developer does not change Locale in the backend service, use the Locale in the requestContext.
            if(result.getLocale() == null) 
            	result.setLocale(ctx.getLocale());
        }
        
        return JsonHelper.toJsonString(result);
    }
}
