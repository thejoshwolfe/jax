package net.wolfesoftware.jax.semalysis;

import java.util.LinkedList;

public class UnknownType extends Type
{
    private UnknownType()
    {
        super("unknown", "unknown");
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
    public static final int TYPE = 0x1b2a0493;
    public int getType()
    {
        return TYPE;
    }

    @Override
    public boolean isInstanceOf(Type type)
    {
        return true;
    }

    @Override
    public Field resolveField(String name)
    {
        return null;
    }

    public static final UnknownType INSTANCE = new UnknownType();
}
