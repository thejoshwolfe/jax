package net.wolfesoftware.java.jax.lexiconizer;


public class RootLocalContext extends LocalContext
{
    public int capacity = 0;
    private int nextLabelNumber = 0;
    private LocalType classContext;

    public RootLocalContext(LocalType classContext)
    {
        super(null);
        this.classContext = classContext;
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
    
    @Override
    public LocalType getClassContext()
    {
        return classContext;
    }
}
