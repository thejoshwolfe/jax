package net.wolfesoftware.jax.semalysizer;

import java.lang.reflect.Modifier;
import net.wolfesoftware.jax.codegen.MethodInfo;

public class Method extends TakesArguments
{
    public String id;
    public boolean isStatic;
    public Method(Type declaringType, Type returnType, String id, Type[] argumentSignature, boolean isStatic)
    {
        super(declaringType, argumentSignature, returnType);
        this.id = id;
        this.isStatic = isStatic;
    }

    @Override
    public String getMethodCode()
    {
        return declaringType.getTypeName() + '/' + id + getDescriptor();
    }
    @Override
    public short getFlags()
    {
        return MethodInfo.ACC_PUBLIC | MethodInfo.ACC_STATIC;
    }
    @Override
    public String getName()
    {
        return id;
    }

    public static Method getMethod(java.lang.reflect.Method method)
    {
        // this lies about the flags in getFlags() but that's fine because we don't ever need them for runtime methods
        return new Method(RuntimeType.getType(method.getDeclaringClass()), RuntimeType.getType(method.getReturnType()), method.getName(), RuntimeType.getTypes(method.getParameterTypes()), Modifier.isStatic(method.getModifiers()));
    }

    public static final Method UNKNOWN = new Method(UnknownType.INSTANCE, UnknownType.INSTANCE, "", new Type[0], false);
}
