
package ca.manitoulin.mtd.dto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ca.manitoulin.mtd.constant.ContextConstants;

/**
 * Class to wrap result data in webservice
 * @author Bob Yu
 */
public class ResultContext implements Serializable {

   
	private Class<?> serviceClass;
	
	private String methodName;

    private HashMap<String, Object> results;
    
    private Page<?> page;

    private LocalizedMessage infoMessage;

    private LocalizedMessage  errorMessage;
    
    private Locale locale;
        
    private boolean successInd = false;

    
    public void addInfoMessage(String message){
    	LocalizedMessage msg = new LocalizedMessage(message);
    	infoMessage = msg;
    }
 
    public void addInfoMessage(String messageKey, List<String> args){
    	LocalizedMessage msg = new LocalizedMessage(messageKey, args);
    	infoMessage = msg;
    }
    
    public void addErrorMessage(String message){
    	LocalizedMessage msg = new LocalizedMessage(message);
    	errorMessage = msg;
    }
    
    public void addErrorMessage(String messageKey, List<String> args){
    	LocalizedMessage msg = new LocalizedMessage(messageKey, args);
    	errorMessage = msg;
    }
    
    public void addResult(String key, Object value) {
        if (results == null) {
            results = new HashMap<String, Object>();
        }
        results.put(key, value);
    }

    public Object getResult(String key) {
        if (results != null && results.containsKey(key)) {
            return results.get(key);
        } else {
            return null;
        }
    }

    public void removeResult(String key) {
        if (results != null && results.containsKey(key)) {
            results.remove(key);
        }
    }

   
    public HashMap<String, Object> getResults() {
        return results;
    }


    
    public boolean isSuccess() {
        return successInd;
    }


	public Class<?> getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(Class<?> serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setSuccess(boolean successInd) {
		this.successInd = successInd;
	}

	public Page<?> getPage() {
		return page;
	}

	public void setPage(Page<?> page) {
		this.page = page;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public LocalizedMessage getInfoMessage() {
		return infoMessage;
	}

	public void setInfoMessage(LocalizedMessage infoMessage) {
		this.infoMessage = infoMessage;
	}

	public LocalizedMessage getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(LocalizedMessage errorMessage) {
		this.errorMessage = errorMessage;
	}

	


}
