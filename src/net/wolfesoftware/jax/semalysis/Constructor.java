package net.wolfesoftware.jax.semalysis;

public class Constructor extends TakesArguments
{
    public Constructor(Type declaringType, Type[] argumentSignature)
    {
        super(declaringType, argumentSignature, RuntimeType.VOID);
    }

    @Override
    public String getMethodCode()
    {
        return declaringType.getTypeName() + "/" + getName() + getDescriptor();
    }
    @Override
    public String getName()
    {
        return "<init>";
    }
}
