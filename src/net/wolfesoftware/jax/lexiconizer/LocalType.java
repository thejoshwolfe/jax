package net.wolfesoftware.jax.lexiconizer;

import java.util.*;
import net.wolfesoftware.jax.codegen.ClassFile;

public class LocalType extends Type
{
    public final LinkedList<Constructor> constructors = new LinkedList<Constructor>();
    private final LinkedList<Method> methods = new LinkedList<Method>();
    private final HashMap<String, Field> fields = new HashMap<String, Field>();
    public LocalType(String fullName, String id)
    {
        super(fullName, id);
    }

    public void addMethod(Method method)
    {
        methods.add(method);
    }
    @Override
    protected LinkedList<Method> getMethods()
    {
        return methods;
    }
    @Override
    protected LinkedList<Constructor> getConstructors()
    {
        return constructors;
    }
    @Override
    public Field resolveField(String name)
    {
        return fields.get(name);
    }

    @Override
    public boolean isInstanceOf(Type type)
    {
        // TODO implement this
        return false;
    }

    public static final int TYPE = 0x1144038e;
    public int getType()
    {
        return TYPE;
    }

    public short getFlags()
    {
        return ClassFile.ACC_PUBLIC;
    }

    public Type getParent()
    {
        return RuntimeType.OBJECT;
    }

    public Type[] getInterfaces()
    {
        return new Type[0];
    }
}
