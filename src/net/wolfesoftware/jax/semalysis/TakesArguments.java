package net.wolfesoftware.jax.semalysis;

public abstract class TakesArguments
{
    public Type declaringType;
    public Type[] argumentSignature;
    public Type returnType;
    public TakesArguments(Type declaringType, Type[] argumentSignature, Type returnType)
    {
        this.declaringType = declaringType;
        this.argumentSignature = argumentSignature;
        this.returnType = returnType;
    }

    public abstract String getMethodCode();

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
    public String toString()
    {
        return getMethodCode();
    }

    public abstract String getName();
}
