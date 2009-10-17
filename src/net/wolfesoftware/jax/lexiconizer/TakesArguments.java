package net.wolfesoftware.jax.lexiconizer;

public abstract class TakesArguments
{
    public Type[] argumentSignature;
    public TakesArguments(Type[] argumentSignature)
    {
        this.argumentSignature = argumentSignature;
    }

    public abstract String getMethodCode();
}
