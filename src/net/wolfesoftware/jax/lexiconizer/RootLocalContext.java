package net.wolfesoftware.jax.lexiconizer;

public class RootLocalContext extends LocalContext
{
    public int variableCapacity = 0;
    public int stackSize = 0;
    public int stackCapacity = 0;
    private int nextLabelNumber = 0;
    private Type classContext;

    public RootLocalContext(Type classContext)
    {
        super(null);
        this.classContext = classContext;
    }

    public void ensureVariableCapacity(int capacity)
    {
        this.variableCapacity = Math.max(this.variableCapacity, capacity);
    }

    public LocalVariable getLocalVariable(String name)
    {
        return localVariables.get(name);
    }

    public String nextLabel()
    {
        return "label" + nextLabelNumber++;
    }

    @Override
    public Type getClassContext()
    {
        return classContext;
    }

    @Override
    public void modifyStack(int delta)
    {
        stackSize += delta;
        stackCapacity = Math.max(stackCapacity, stackSize);
    }

    public String toString()
    {
        return "stack: [" + stackSize + "," + stackCapacity + "]";
    }
}
