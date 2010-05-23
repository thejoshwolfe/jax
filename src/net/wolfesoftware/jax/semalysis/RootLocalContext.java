package net.wolfesoftware.jax.semalysis;

import java.util.ArrayList;
import net.wolfesoftware.jax.tokenization.Lang;

public final class RootLocalContext extends LocalContext
{
    private final ArrayList<Type> operandStack = new ArrayList<Type>();
    public int stackSize = 0;
    public int stackCapacity = 0;
    public int localVariableCapacity = 0;
    private LocalType classContext;

    public RootLocalContext(LocalType classContext, boolean isStatic)
    {
        super(null);
        this.classContext = classContext;
        if (!isStatic)
            addLocalVariable(Lang.KEYWORD_THIS, classContext, null);
    }

    @Override
    public LocalVariable getLocalVariable(String name)
    {
        return localVariableMap.get(name);
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

    public Type peekOperandType()
    {
        return operandStack.get(operandStack.size() - 1);
    }

    @Override
    public void popOperand()
    {
        stackSize -= operandStack.remove(operandStack.size() - 1).getSize();
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

    public void ensureLocalVariableCapacity(int size)
    {
        if (localVariableCapacity < size)
            localVariableCapacity = size;
    }

    public String toString()
    {
        return "stack: [" + stackSize + "," + stackCapacity + "]";
    }
    
    @Override
    public BranchDestination getReturnDestination()
    {
        return returnDestination;
    }
    @Override
    public BranchDestination getBreakDestination()
    {
        return breakDestination;
    }
    @Override
    public BranchDestination getContinueDestination()
    {
        return continueDestination;
    }
}
