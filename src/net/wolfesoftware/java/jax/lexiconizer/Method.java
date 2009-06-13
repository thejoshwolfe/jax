package net.wolfesoftware.java.jax.lexiconizer;

public class Method
{
    public Type returnType;
    public String id;
    public Type[] argumentSignature;
    public Method(Type returnType, String id, Type[] argumentSignature)
    {
        this.returnType = returnType;
        this.id = id;
        this.argumentSignature = argumentSignature;
    }
}
