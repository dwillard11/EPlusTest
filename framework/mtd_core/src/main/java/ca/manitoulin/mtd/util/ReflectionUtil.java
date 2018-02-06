package ca.manitoulin.mtd.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Tool used in reflection
 *
 * @author : Bob Yu
 * @version : 1.0
 */
public class ReflectionUtil {
	
	private static final Logger log = Logger.getLogger(ReflectionUtil.class);

    private ReflectionUtil(){
    	
    }
    /**
     * copy properties between two objects.
     *
     * @param fromobj
     * @param toobj
     * @param fieldspec
     * @param <T>
     * @throws Exception 
     */
    public static <T> void copyProperties(T fromobj, T toobj, String... fieldspec) throws Exception {
        for (String filename : fieldspec) {
            Object val = ReflectionUtil.invokeGetterMethod(fromobj, filename);
            ReflectionUtil.invokeSetterMethod(toobj, filename, val);
        }

    }

    /**
     * invoke getter
     *
     * @param obj
     * @param propertyName
     * @return
     * @throws Exception 
     */
    public static Object invokeGetterMethod(Object obj, String propertyName) throws Exception {
        String getterMethodName = "get" + StringUtils.capitalize(propertyName);
        return invokeMethod(obj, getterMethodName, null, null);
    }

    /**
     * invoke setter
     *
     * @param obj
     * @param propertyName
     * @param value
     * @throws Exception 
     */
    public static void invokeSetterMethod(Object obj, String propertyName, Object value) throws Exception {
        invokeSetterMethod(obj, propertyName, value, null);
    }

    /**
     * invoke setter with specified parameter types.
     *
     * @param obj
     * @param propertyName
     * @param value
     * @param propertyType
     * @throws Exception 
     */
    public static void invokeSetterMethod(Object obj, String propertyName, Object value, Class<?> propertyType) throws Exception {
        propertyType = propertyType != null ? propertyType : value
                .getClass();
        String setterMethodName = "set" + StringUtils.capitalize(propertyName);
        invokeMethod(obj, setterMethodName, new Class<?>[]{propertyType}, new Object[]{value});
    }

    /**
     * invoke specified method, even it is private or protected.
     *
     * @param obj
     * @param methodName
     * @param parameterTypes
     * @param args
     * @return
     * @throws Exception 
     */
    public static Object invokeMethod(final Object obj,
                                      final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) throws Exception {
        Method method = obtainAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException(
                    "Could not find method [" + methodName
                            + "] on target [" + obj + "].");
        }
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * Obtain method with name and paramter type.
     *
     * @param obj
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method obtainAccessibleMethod(final Object obj,
                                                final String methodName, final Class<?>... parameterTypes) {
        Class<?> superClass = obj.getClass();
        Class<Object> objClass = Object.class;
        for (; superClass != objClass; superClass = superClass.getSuperclass()) {
            Method method = null;
            try {
                method = superClass.getDeclaredMethod(methodName,
                        parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (Exception e) {
                //do nothing, keep searching.
            }
        }
        return null;
    }

    /**
     * Obtain method in specified class.
     * @param klass
     * @param name
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static Method obtainMethod(Class klass, String name){
    	Method[] methods = klass.getDeclaredMethods();
		for(Method m:methods){
			if(m.getName().equals(name)){
				return m;
			}
		}
		return null;
    }
    

    /**
     * obtain value of given field
     *
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object obtainFieldValue(final Object obj, final String fieldName) {
        Field field = obtainAccessibleField(obj, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Devkit: could not find field [" + fieldName + "] on target [" + obj + "]");
        }
        Object retval = null;
        try {
            retval = field.get(obj);
        } catch (Exception e) {
            log.debug(e);
        }
        return retval;

    }

    /**
     * set field value, ignore private , protected limitation and setter.
     *
     * @param obj
     * @param fieldName
     * @param value
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
        Field field = obtainAccessibleField(obj, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Devkit: could not find field [" + fieldName + "] on target [" + obj + "]");
        }
        try {
            field.set(obj, value);
        } catch (Exception e) {
            log.debug(e);
        }
    }

    /**
     * obtain declared fields of given object, and set it to accessible
     *
     * @param obj
     * @param fieldName
     * @return
     */
    public static Field obtainAccessibleField(final Object obj,
                                              final String fieldName) {
        Class<?> superClass = obj.getClass();
        Class<Object> objClass = Object.class;
        for (; superClass != objClass; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                log.debug("field obtained :" + fieldName);
                return field;
            } catch (Exception e) {
                log.debug("Keep searching ..." + e.getMessage());
            }
        }
        return null;
    }
    
    /**
     * obtain all fields
     * @param obj
     * @return
     */
    public static Field[] getAllFields(Object obj){
		Field[] fields = new Field[]{};
		for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			fields = (Field[]) ArrayUtils.addAll(fields, clazz.getDeclaredFields());
		}
		return fields;
	}
}
