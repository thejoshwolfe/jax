package net.wolfesoftware.jax.semalysis;

import java.lang.reflect.Modifier;

public class Method extends TakesArguments
{
    public String name;
    public boolean isStatic;
    public Method(Type declaringType, Type returnType, String name, Type[] argumentSignature, boolean isStatic)
    {
        super(declaringType, argumentSignature, returnType);
        this.name = name;
        this.isStatic = isStatic;
    }

    @Override
    public String getMethodCode()
    {
        return declaringType.getTypeName() + '/' + name + getDescriptor();
    }
    @Override
    public String getName()
    {
        return name;
    }

    public static Method getMethod(java.lang.reflect.Method method)
    {
        // this lies about the flags in getFlags() but that's fine because we don't ever need them for runtime methods
        return new Method(RuntimeType.getType(method.getDeclaringClass()), RuntimeType.getType(method.getReturnType()), method.getName(), RuntimeType.getTypes(method.getParameterTypes()), Modifier.isStatic(method.getModifiers()));
    }

    public static final Method UNKNOWN = new Method(UnknownType.INSTANCE, UnknownType.INSTANCE, "", new Type[0], false);
}
