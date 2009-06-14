package net.wolfesoftware.java.jax.lexiconizer;

import java.lang.reflect.Modifier;
import java.util.HashMap;

public class RuntimeMethod extends Method
{
    public RuntimeMethod(java.lang.reflect.Method underlyingMethod)
    {
        super(RuntimeType.getType(underlyingMethod.getDeclaringClass()), RuntimeType.getType(underlyingMethod.getReturnType()), underlyingMethod.getName(), convertArgumentSignature(underlyingMethod), Modifier.isStatic(underlyingMethod.getModifiers()));
    }

    private static Type[] convertArgumentSignature(java.lang.reflect.Method underlyingMethod)
    {
        Class<?>[] underlyingTypes = underlyingMethod.getParameterTypes();
        Type[] argumentTypes = new Type[underlyingTypes.length];
        for (int i = 0; i < underlyingTypes.length; i++)
            argumentTypes[i] = RuntimeType.getType(underlyingTypes[i]);
        return argumentTypes;
    }






    private static final HashMap<java.lang.reflect.Method, RuntimeMethod> cache = new HashMap<java.lang.reflect.Method, RuntimeMethod>();
    public static RuntimeMethod getMethod(java.lang.reflect.Method underlyingMethod)
    {
        RuntimeMethod method = cache.get(underlyingMethod);
        if (method == null)
        {
            method = new RuntimeMethod(underlyingMethod);
            cache.put(underlyingMethod, method);
        }
        return method;
    }

    public static RuntimeMethod[] getMethods(String name)
    {
        
        return null;
    }
}
