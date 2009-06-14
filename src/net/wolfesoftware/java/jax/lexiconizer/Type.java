package net.wolfesoftware.java.jax.lexiconizer;

public abstract class Type
{
    public final String fullName;
    public final String id;

    public Type(String fullName, String id)
    {
        this.fullName = fullName;
        this.id = id;
    }

    public abstract Method resolveMethod(String name, Type[] argumentSignature);
    public abstract Field resolveField(String name);

    
    public abstract int getType();
    
    public String toString()
    {
        return fullName;
    }

    public boolean isPrimitive()
    {
        return false;
    }
}
