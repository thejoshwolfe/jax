package net.wolfesoftware.java.jax.lexiconizer;

import java.util.*;

public class ClassContext extends Type
{
    public ClassContext(String packageId, String id)
    {
        super(packageId, id);
    }

    private HashMap<String, LinkedList<Method>> methods = new HashMap<String, LinkedList<Method>>();
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
        LinkedList<Method> list = methods.get(name);
        if (list == null)
            return null;
        for (Method m : list)
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
}
