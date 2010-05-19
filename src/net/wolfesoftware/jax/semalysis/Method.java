package net.wolfesoftware.jax.semalysis;

import net.wolfesoftware.jax.codegen.MethodInfo;

public class Method extends TakesArguments
{
    public String name;
    public short modifiers;
    public Method(Type declaringType, Type returnType, String name, Type[] argumentSignature, short modifiers)
    {
        super(declaringType, argumentSignature, returnType);
        this.name = name;
        this.modifiers = modifiers;
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
        Type declaringClass = RuntimeType.getType(method.getDeclaringClass());
        Type returnType = RuntimeType.getType(method.getReturnType());
        Type[] argumentSignature = RuntimeType.getTypes(method.getParameterTypes());
        return new Method(declaringClass, returnType, method.getName(), argumentSignature, (short)method.getModifiers());
    }

    public static final Method UNKNOWN = new Method(UnknownType.INSTANCE, UnknownType.INSTANCE, "", new Type[0], (short)0);
    public boolean isPrivate()
    {
        return (modifiers & MethodInfo.ACC_PRIVATE) != 0;
    }
    public boolean isStatic()
    {
        return (modifiers & MethodInfo.ACC_STATIC) != 0;
    }
}
