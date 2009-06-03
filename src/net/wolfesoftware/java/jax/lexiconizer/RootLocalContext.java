package net.wolfesoftware.java.jax.lexiconizer;

import java.util.ArrayList;

public class RootLocalContext extends LocalContext
{
    public int capacity = 0;
    private int nextLabelNumber = 0;

    public RootLocalContext(ArrayList<LexicalException> errors)
    {
        super(errors, null);
    }

    public void ensureVariableCapacity(int capacity)
    {
        this.capacity = Math.max(this.capacity, capacity);
    }

    public LocalVariable getLocalVariable(String name)
    {
        return localVariables.get(name);
    }

    public String nextLabel()
    {
        return "label" + nextLabelNumber++;
    }
}
