package net.wolfesoftware.java.jax.lexiconizer;

public class PrimitiveType extends Type
{

    public PrimitiveType(String id)
    {
        super(null, id);
    }

    @Override
    public Method resolveMethod(String name, Type[] argumentSignature)
    {
        throw new RuntimeException();
    }
}
