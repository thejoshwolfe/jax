package net.wolfesoftware.java.jax.lexiconizer;

public class Method
{
    public Type returnType;
    public Type[] argumentSignature;
    public Method(Type returnType, Type[] argumentSignature)
    {
        this.returnType = returnType;
        this.argumentSignature = argumentSignature;
    }
}
