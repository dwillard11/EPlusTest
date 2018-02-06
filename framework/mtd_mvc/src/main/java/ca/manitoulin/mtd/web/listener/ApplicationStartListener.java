package ca.manitoulin.mtd.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class ApplicationStartListener implements ServletContextListener {

	private static final Logger log = Logger.getLogger(ApplicationStartListener.class);

	@Override
    public void contextInitialized(ServletContextEvent arg0) {
		long ts1 = System.currentTimeMillis();

		ServletContext servletContext = arg0.getServletContext();  		
		
		//Do something operation when starting up.
		
		long ts2 = System.currentTimeMillis();
		log.warn("\r\n========================================" 
				+ "\r\n WEB CONTEXT INITIALIZED (TIME DURATION " + (ts2 - ts1) + " MS)"
				+ "\r\n========================================");
	}

	@Override
    public void contextDestroyed(ServletContextEvent arg0) {
		log.warn("\r\n========================================" 
				+ "\r\nWEB CONTEXT DESTORYED !" 
				+ "\r\n========================================");
	}

}