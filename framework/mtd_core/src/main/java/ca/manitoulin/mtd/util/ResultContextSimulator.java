package ca.manitoulin.mtd.util;

import java.util.ArrayList;
import java.util.List;

import ca.manitoulin.mtd.dto.security.Organization;
import ca.manitoulin.mtd.dto.security.SecureUser;

public class ResultContextSimulator {
	
	protected  void gengerateUserSession(){
		SecureUser user = new SecureUser();
		user.setUid("test");
		user.setCompany("TESTCMP");
		user.setCustomer("TESTCUS");
		user.setDepId(1);
		user.setReferLanguage("chinese");
		Organization dep = new Organization();
		dep.setId(1);
		List<Organization> deps = new ArrayList<Organization>();
		deps.add(dep);
		user.setDepartments(deps);
		ApplicationSession.set(user);
	}

}
