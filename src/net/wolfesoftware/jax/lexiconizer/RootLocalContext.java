package net.wolfesoftware.jax.lexiconizer;

import net.wolfesoftware.jax.ast.Id;
import net.wolfesoftware.jax.tokenizer.Lang;

public class RootLocalContext extends LocalContext
{
    public int variableCapacity = 0;
    public int stackSize = 0;
    public int stackCapacity = 0;
    private int nextLabelNumber = 0;
    private LocalType classContext;

    public RootLocalContext(LocalType classContext, boolean isStatic)
    {
        super(null);
        this.classContext = classContext;
        if (!isStatic)
            addLocalVariable(new Id(Lang.KEYWORD_THIS), classContext, null);
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
    public LocalType getClassContext()
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
