package net.wolfesoftware.jax.lexiconizer;

public abstract class Field
{
    public Type declaringType;
    public Type returnType;
    protected Field(Type declaringType, Type returnType)
    {
        this.declaringType = declaringType;
        this.returnType = returnType;
    }
}
