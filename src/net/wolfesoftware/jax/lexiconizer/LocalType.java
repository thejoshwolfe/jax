package net.wolfesoftware.jax.lexiconizer;

import java.util.*;

public class LocalType extends Type
{
    public final LinkedList<Constructor> constructors = new LinkedList<Constructor>();
    private final HashMap<String, LinkedList<Method>> methods = new HashMap<String, LinkedList<Method>>();
    private final HashMap<String, Field> fields = new HashMap<String, Field>();
    public LocalType(String fullName, String id)
    {
        super(fullName, id);
    }

    public void addMethod(Method method)
    {
        LinkedList<Method> list = methods.get(method.id);
        if (list == null)
        {
            list = new LinkedList<Method>();
            methods.put(method.id, list);
        }
        list.add(method);
    }
    @Override
    public Method resolveMethod(String name, Type[] argumentSignature)
    {
        LinkedList<Method> overloads = methods.get(name);
        if (overloads == null)
            return null;
        for (Method m : overloads)
        {
            if (m.argumentSignature.length != argumentSignature.length)
                continue;
            for (int i = 0; i < argumentSignature.length; i++)
                if (m.argumentSignature[i] != argumentSignature[i])
                    continue;
            return m;
        }
        return null;
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
}
