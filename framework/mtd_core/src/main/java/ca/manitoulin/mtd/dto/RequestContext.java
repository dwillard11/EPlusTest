
package ca.manitoulin.mtd.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

import ca.manitoulin.mtd.dto.security.SecureUser;

/**
 * Transfer request via webservice
 * @author Bob Yu
 */
public class RequestContext implements Serializable {

	
	private Page<?> page;

	private SecureUser userProfile;

	private Class<?> serviceClass;
	
	private String methodName;

	private HashMap<String, Object> parameters;

	private Locale locale;

	

	public HashMap<String, Object> getParameters() {
		return parameters;
	}
	
	/**
	 * Retrieving parameters set by the caller
	 * 
	 * @param name -
	 *            name of parameter
	 * @return object associated with the 'name'; Null if 'name' not set.
	 */
	public Object getParameter(String name) {
		// Fixing NullPointerException in getParamter() if addParameter() is
		// never invoked.
		if (parameters != null)
		   return parameters.get(name);
		return null;
	}

	
	public void addParameter(String name, Object value) {
		if (this.parameters == null) {
			this.parameters = new HashMap<String, Object>();
		}
		this.parameters.put(name, value);
	}

	
	




	public SecureUser getUserProfile() {
		return userProfile;
	}



	public void setUserProfile(SecureUser userProfile) {
		this.userProfile = userProfile;
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



}
