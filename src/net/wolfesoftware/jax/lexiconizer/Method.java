package net.wolfesoftware.jax.lexiconizer;

import java.lang.reflect.Modifier;
import net.wolfesoftware.jax.codegen.MethodInfo;

public class Method extends TakesArguments
{
    public Type declaringType;
    public String id;
    public boolean isStatic;
    public Method(Type declaringType, Type returnType, String id, Type[] argumentSignature, boolean isStatic)
    {
        super(argumentSignature, returnType);
        this.declaringType = declaringType;
        this.id = id;
        this.isStatic = isStatic;
    }

    @Override
    public String getMethodCode()
    {
        return declaringType.getTypeName() + '/' + id + getDescriptor();
    }
    public short getFlags()
    {
        return MethodInfo.ACC_PUBLIC | MethodInfo.ACC_STATIC;
    }
    public String getName()
    {
        return id;
    }
    public String toString()
    {
        return getMethodCode();
    }

    public static Method getMethod(java.lang.reflect.Method method)
    {
        // this lies about the flags in getFlags() but that's fine because we don't ever need them for runtime methods
        return new Method(RuntimeType.getType(method.getDeclaringClass()), RuntimeType.getType(method.getReturnType()), method.getName(), RuntimeType.getTypes(method.getParameterTypes()), Modifier.isStatic(method.getModifiers()));
    }

    public static final Method UNKNOWN = new Method(UnknownType.INSTANCE, UnknownType.INSTANCE, "", new Type[0], false);
}
