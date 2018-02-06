package ca.manitoulin.mtd.web.listener;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.log4j.Logger;

import ca.manitoulin.mtd.constant.ContextConstants;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.util.ApplicationSession;

/**
 * Update user system saved in thread scope once user http session changed.
 * @author Bob Yu
 *
 */
public class SessionAttributeListener implements HttpSessionAttributeListener {
	
	private static final Logger log = Logger.getLogger(SessionAttributeListener.class);

	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
		String key = arg0.getName();
		switch (key){
		case ContextConstants.SESSION_USERPROFILE:
			ApplicationSession.set((SecureUser) arg0.getValue());
			log.info("## User Session saved in ThreadLocal! ##");
			break;
		default:
			break;
		}
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		String key = arg0.getName();
		switch (key){
		case ContextConstants.SESSION_USERPROFILE:
			ApplicationSession.remove();
			log.info("## User Session removed in ThreadLocal! ##");
			break;
		default:
			break;
		}
		
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		String key = arg0.getName();
		switch (key){
		case ContextConstants.SESSION_USERPROFILE:
			ApplicationSession.set((SecureUser) arg0.getValue());
			log.info("## User Session updated in ThreadLocal! ##");
			break;
		default:
			break;
		}
		
	}

	

}
