package net.wolfesoftware.java.jax.lexiconizer;

public class LocalMethod extends Method
{
    public LocalMethod(LocalType declaringType, Type returnType, String id, Type[] argumentSignature, boolean isStatic)
    {
        super(declaringType, returnType, id, argumentSignature, isStatic);
    }
}
