package ca.manitoulin.mtd.ad.filter;

import static javax.naming.Context.INITIAL_CONTEXT_FACTORY;
import static javax.naming.Context.PROVIDER_URL;
import static javax.naming.Context.SECURITY_AUTHENTICATION;
import static javax.naming.Context.SECURITY_CREDENTIALS;
import static javax.naming.Context.SECURITY_PRINCIPAL;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.util.Hashtable;

import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.log4j.Logger;


public class ActivityDirectoryFilter {
	private static final Logger log = Logger.getLogger(ActivityDirectoryFilter.class);

	/**
	 * Verify user account with given domain info
	 * 
	 * @param userName
	 * @param password
	 * @param domain
	 *            could be SECURE.MANITOULINTRANSPORT.COM or XXX.COM
	 * @return
	 */
	public static boolean authenticate(String userName, String password, String domain) {

		if (isBlank(userName) || isBlank(password) || isBlank(domain)) {
			log.error("validation params can not be null!!!==>" + "username->" + userName + " password->" + password
					+ " domain->" + domain);
			return false;
		} else {
			userName = trimToNull(userName);
			domain = trimToNull(domain);
		}
		log.debug("username->" + userName + " domain->" + domain);
		String principal = userName + '@' + domain;
		DirContext ctx = null;
		Hashtable<String, String> HashEnv = initADServer(domain, principal, password);
		try {
			ctx = new InitialDirContext(HashEnv);
			log.debug("Authenticate Success!");
			return true;
		} catch (Exception e) {
			log.error("authenticate fail principal:" + principal + " domain:" + domain);
			return false;
		} finally {
			if (HashEnv!=null) {
				HashEnv.clear();
			}
			if (null != ctx) {
				try {
					ctx.close();
					ctx = null;
				} catch (Exception e) {
					log.error("close ctx fail" + e.getMessage());
				} finally {
					ctx = null;
				}
			}
		}
	}

	private static Hashtable<String, String> initADServer(String host, String username, String password) {
		Hashtable<String, String> HashEnv = new Hashtable<String, String>();
		HashEnv.put(SECURITY_AUTHENTICATION, "simple");
		HashEnv.put(SECURITY_PRINCIPAL, username);
		HashEnv.put(SECURITY_CREDENTIALS, password);
		HashEnv.put(INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		HashEnv.put("com.sun.jndi.ldap.connect.timeout", "3000");
		HashEnv.put(PROVIDER_URL, "ldap://" + host);
		return HashEnv;
	}

	
}
