package net.wolfesoftware.jax.semalysizer;

import java.util.ArrayList;
import net.wolfesoftware.jax.ast.Id;
import net.wolfesoftware.jax.tokenizer.Lang;

public class RootLocalContext extends LocalContext
{
    public int variableCapacity = 0;
    private final ArrayList<Type> operandStack = new ArrayList<Type>();
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
    public void pushAndPopOperand(Type operandType)
    {
        int intermediateStackSize = stackSize + operandType.getSize();
        ensureStackCapacity(intermediateStackSize);
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
