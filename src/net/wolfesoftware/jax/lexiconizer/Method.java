package net.wolfesoftware.jax.lexiconizer;

public abstract class Method
{
    public Type declaringType;
    public Type returnType;
    public String id;
    public Type[] argumentSignature;
    public boolean isStatic;
    public Method(Type declaringType, Type returnType, String id, Type[] argumentSignature, boolean isStatic)
    {
        this.declaringType = declaringType;
        this.returnType = returnType;
        this.id = id;
        this.argumentSignature = argumentSignature;
        this.isStatic = isStatic;
    }
}
