package net.wolfesoftware.jax.semalysis;

public class StaticInitializer extends TakesArguments
{
    public StaticInitializer(Type declaringType)
    {
        super(declaringType, new Type[0], RuntimeType.VOID);
    }

    @Override
    public String getMethodCode()
    {
        return declaringType.getTypeName() + "/" + getName() + getDescriptor();
    }

    @Override
    public String getName()
    {
        return "<clinit>";
    }
}
