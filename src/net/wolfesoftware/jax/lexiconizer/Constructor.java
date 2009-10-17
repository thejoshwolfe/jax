package net.wolfesoftware.jax.lexiconizer;

public class Constructor extends TakesArguments
{
    public Type type;
    public Constructor(Type type, Type[] argumentSignature)
    {
        super(argumentSignature);
        this.type = type;
    }

    @Override
    public String getMethodCode()
    {
        StringBuilder builder = new StringBuilder(type.getTypeName());
        builder.append("<init>(");
        for (Type type : argumentSignature)
            builder.append(type.getTypeCode());
        builder.append(")V");
        return builder.toString();
    }
}
