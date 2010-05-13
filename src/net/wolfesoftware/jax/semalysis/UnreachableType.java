package net.wolfesoftware.jax.semalysis;

import java.util.LinkedList;

public class UnreachableType extends Type
{
    private UnreachableType()
    {
        super("unreachable", "unreachable");
    }

    @Override
    protected LinkedList<Constructor> getConstructors()
    {
        return new LinkedList<Constructor>();
    }

    @Override
    protected LinkedList<Method> getMethods()
    {
        return new LinkedList<Method>();
    }

    @Override
    public Type getParent()
    {
        return RuntimeType.OBJECT;
    }

    public static final int TYPE = 0x2f7c05fd;
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

    public static final UnreachableType INSTANCE = new UnreachableType();
}
