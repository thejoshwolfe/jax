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
}
