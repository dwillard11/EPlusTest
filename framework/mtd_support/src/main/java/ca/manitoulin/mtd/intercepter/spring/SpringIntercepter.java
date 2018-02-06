package ca.manitoulin.mtd.intercepter.spring;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import ca.manitoulin.mtd.util.ReflectionUtil;


/**
 * Super class of all spring intercepter
 * @author Bob Yu
 *
 */
public abstract class SpringIntercepter {

	protected Method getJoinPointMethod(ProceedingJoinPoint joinPoint){
		MethodSignature joinPointObject = (MethodSignature) joinPoint.getSignature(); 

		//The method is a proxy, which can not provide Annotations and ParameterAnnotations.
		Method proxyMethod = joinPointObject.getMethod();  
		
		Object inspectBean  = joinPoint.getTarget();
		Method realMethod = ReflectionUtil.obtainAccessibleMethod(inspectBean, proxyMethod.getName(), proxyMethod.getParameterTypes());
		
		return realMethod;
	}

}
