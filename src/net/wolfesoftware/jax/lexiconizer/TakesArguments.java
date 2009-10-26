package net.wolfesoftware.jax.lexiconizer;

public abstract class TakesArguments
{
    public Type[] argumentSignature;
    public Type returnType;
    public TakesArguments(Type[] argumentSignature, Type returnType)
    {
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

    public abstract short getFlags();
    public abstract String getName();
}
