package com.proxy.util;

import java.lang.reflect.Method;

public class ReflectUtil {
	
	public static Object invoke(Method method , Object receiver , Object... args){
		try {
			return method.invoke(receiver, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
