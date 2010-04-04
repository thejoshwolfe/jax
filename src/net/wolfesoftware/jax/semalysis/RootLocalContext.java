package net.wolfesoftware.jax.semalysis;

import java.util.ArrayList;
import net.wolfesoftware.jax.ast.Id;
import net.wolfesoftware.jax.tokenization.Lang;

public final class RootLocalContext extends LocalContext
{
    private final ArrayList<Type> operandStack = new ArrayList<Type>();
    public int stackSize = 0;
    public int stackCapacity = 0;
    private int localVariableCapacity = -1; // needs to be calculated
    private int nextLabelNumber = 0;
    private LocalType classContext;

    public RootLocalContext(LocalType classContext, boolean isStatic)
    {
        super(null);
        this.classContext = classContext;
        if (!isStatic)
            addLocalVariable(new Id(Lang.KEYWORD_THIS), classContext, null);
    }

    public int getLocalVariableCapacity()
    {
        if (localVariableCapacity == -1)
            localVariableCapacity = internalGetLocalVariableCapacity();
        return localVariableCapacity;
    }

    @Override
    public LocalVariable getLocalVariable(String name)
    {
        return localVariableMap.get(name);
    }

    public void numberLocalVariables()
    {
        internalNumberLocalVariables(0);
    }
    @Override
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
    public void pushOperand(Type operandType)
    {
        operandStack.add(operandType);
        stackSize += operandType.getSize();
        ensureStackCapacity(stackSize);
    }

    @Override
    public void popOperands(int n)
    {
        for (int i = 0; i < n; i++)
            popOperand();
    }

    @Override
    public Type popOperand()
    {
        Type operandType = operandStack.remove(operandStack.size() - 1);
        stackSize -= operandType.getSize();
        return operandType;
    }

    @Override
    public boolean isOperandStackEmpty()
    {
        return operandStack.isEmpty();
    }

    private void ensureStackCapacity(int size)
    {
        if (stackCapacity < size)
            stackCapacity = size;
    }

    public String toString()
    {
        return "stack: [" + stackSize + "," + stackCapacity + "]";
    }
}
