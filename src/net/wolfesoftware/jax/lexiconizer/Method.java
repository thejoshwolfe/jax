package net.wolfesoftware.jax.lexiconizer;

public abstract class Method extends TakesArguments
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

    @Override
    public String getMethodCode()
    {
        StringBuilder builder = new StringBuilder(declaringType.getTypeName());
        builder.append('/').append(id).append('(');
        for (Type type : argumentSignature)
            builder.append(type.getTypeCode());
        builder.append(')');
        builder.append(returnType.getTypeCode());
        return builder.toString();
    }
}
