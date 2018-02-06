package ca.manitoulin.mtd.intercepter.spring;

import java.util.Date;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import ca.manitoulin.mtd.dto.AbstractDTO;
import ca.manitoulin.mtd.dto.security.SecureUser;
import ca.manitoulin.mtd.util.ApplicationSession;

@Aspect
@Component
public class AuditIntercepter extends SpringIntercepter{

	private static final Logger log = Logger.getLogger( AuditIntercepter.class );
	
	@Pointcut("execution(* ca.manitoulin.*.dao..*Mapper.insert*(..))")
    public void generateAuditInfoWhenInsert() {
    }
	
	@Pointcut("execution(* ca.manitoulin.*.dao..*Mapper.update*(..))")
    public void generateAuditInfoWhenUpdate() {
    }
	
	@Before(value = "generateAuditInfoWhenInsert()")
	public void doBeforeInsert(JoinPoint joinPoint) {
		doGenerateAuditInfo(joinPoint);
	}
	@Before(value = "generateAuditInfoWhenUpdate()")
	public void doBeforeUpdate(JoinPoint joinPoint) {
		doGenerateAuditInfo(joinPoint);
	}
	
	private void doGenerateAuditInfo(JoinPoint joinPoint){
		log.debug("-- AuditIntercepter triggered -- ");
		Object[] args = joinPoint.getArgs();
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof AbstractDTO) {
				log.debug("-- AbstractDTO found in arguments");
				AbstractDTO pojo = (AbstractDTO) args[i];
				SecureUser currentUser = ApplicationSession.get();
				pojo.setCurrentCompany(currentUser.getCompany());
				pojo.setCurrentCustomer(currentUser.getCustomer());
				pojo.setUpdatedBy(currentUser.getUid());
				pojo.setUpdateTime(new Date());
				log.debug("-- Audit info set");
			}
		}
	}
	
}
