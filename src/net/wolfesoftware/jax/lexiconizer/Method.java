package net.wolfesoftware.jax.lexiconizer;

import java.lang.reflect.Modifier;
import net.wolfesoftware.jax.codegen.MethodInfo;

public class Method extends TakesArguments
{
    public Type declaringType;
    public Type returnType;
    public String id;
    public boolean isStatic;
    public Method(Type declaringType, Type returnType, String id, Type[] argumentSignature, boolean isStatic)
    {
        super(argumentSignature);
        this.declaringType = declaringType;
        this.returnType = returnType;
        this.id = id;
        this.isStatic = isStatic;
    }

    public String getDescriptor()
    {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (Type type : argumentSignature)
            builder.append(type.getTypeCode());
        builder.append(')');
        builder.append(returnType.getTypeCode());
        return builder.toString();
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
        return new Method(RuntimeType.getType(method.getDeclaringClass()), RuntimeType.getType(method.getReturnType()), method.getName(), RuntimeType.getTypes(method.getParameterTypes()), Modifier.isStatic(method.getModifiers()));
    }

    public static final Method UNKNOWN = new Method(UnknownType.INSTANCE, UnknownType.INSTANCE, "", new Type[0], false);
}
