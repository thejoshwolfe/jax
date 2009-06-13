package net.wolfesoftware.java.jax.lexiconizer;

import java.util.ArrayList;

public class ClassContext extends Type
{
    public ClassContext()
    {
        super("", "TODO"); // TODO
    }

    private ArrayList<Method> methods = new ArrayList<Method>();
    public void addMethod(Method method)
    {
        methods.add(method);
    }
}
