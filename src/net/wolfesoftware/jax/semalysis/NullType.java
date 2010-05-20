package net.wolfesoftware.jax.semalysis;

import java.util.LinkedList;

public class NullType extends Type
{
    private NullType()
    {
        super("null", "null");
    }

    @Override
    protected LinkedList<Method> getMethods()
    {
        return new LinkedList<Method>();
    }
    @Override
    protected LinkedList<Constructor> getConstructors()
    {
        return new LinkedList<Constructor>();
    }
    public static final int TYPE = 0x0e4f033e;
    public int getType()
    {
        return TYPE;
    }

    @Override
    public boolean isInstanceOf(Type type)
    {
        return !type.isPrimitive();
    }

    @Override
    public Field resolveField(String name)
    {
        return null;
    }

    @Override
    public Type getParent()
    {
        // don't think should ever be called.
        throw null;
    }

    public static final NullType INSTANCE = new NullType();
}
