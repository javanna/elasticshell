package org.elasticsearch.shell.rhino;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


public class MethodUtils {

    private static boolean sawSecurityException;

    private MethodUtils() {

    }

    public static Method[] getMethodList(Class<?> clazz) {
        Method[] methods = null;
        try {
            // getDeclaredMethods may be rejected by the security manager
            // but getMethods is more expensive
            if (!sawSecurityException) {
                methods = clazz.getDeclaredMethods();
            }

        } catch (SecurityException e) {
            // If we get an exception once, give up on getDeclaredMethods
            sawSecurityException = true;
        }
        if (methods == null) {
            methods = clazz.getMethods();
        }
        int count = 0;
        for (int i=0; i < methods.length; i++) {
            if (sawSecurityException
                    ? methods[i].getDeclaringClass() != clazz
                    : !Modifier.isPublic(methods[i].getModifiers()))
            {
                methods[i] = null;
            } else {
                count++;
            }
        }
        Method[] result = new Method[count];
        int j=0;
        for (int i=0; i < methods.length; i++) {
            if (methods[i] != null)
                result[j++] = methods[i];
        }
        return result;
    }

    public static Method findSingleMethod(Method[] methods, String name) {
        for (int i = 0, N = methods.length; i != N; ++i) {
            Method method = methods[i];
            if (method != null && name.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }
}
