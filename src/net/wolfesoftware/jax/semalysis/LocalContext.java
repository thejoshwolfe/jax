package net.wolfesoftware.jax.semalysis;

import java.util.*;

public class LocalContext
{
    public final LocalContext parentContext;
    private final RootLocalContext rootContext;
    protected final HashMap<String, LocalVariable> localVariableMap = new HashMap<String, LocalVariable>();
    protected BranchDestination returnDestination = null;
    protected BranchDestination breakDestination = null;
    protected BranchDestination continueDestination = null;
    private int nextLocalVariableNumber;

    protected LocalContext(LocalContext parentContext)
    {
        this.parentContext = parentContext;
        if (parentContext == null) {
            // I am the root
            rootContext = (RootLocalContext)this;
            nextLocalVariableNumber = 0;
        } else {
            // I am not the root
            rootContext = parentContext.rootContext;
            nextLocalVariableNumber = -1;
        }
    }

    public final LocalVariable addLocalVariable(String variableName, Type type, ArrayList<SemalyticalError> errors)
    {
        // redeclaration is not a fatal error (it's even allowed in C)
        if (getLocalVariable(variableName) != null)
            errors.add(new SemalyticalError(variableName, "Redeclaration of local variable"));
        LocalVariable variable = new LocalVariable(this, variableName, type);
        localVariableMap.put(variableName, variable);
        return variable;
    }
    public final SecretLocalVariable addSecretLocalVariable(Type type)
    {
        return new SecretLocalVariable(this, type);
    }

    public final int getNextLocalVariableNumber(int typeSize)
    {
        int number = getCurrentLocalVariableNumber();
        nextLocalVariableNumber += typeSize;
        rootContext.ensureLocalVariableCapacity(nextLocalVariableNumber);
        return number;
    }

    private int getCurrentLocalVariableNumber()
    {
        if (nextLocalVariableNumber == -1)
            nextLocalVariableNumber = parentContext.getCurrentLocalVariableNumber();
        return nextLocalVariableNumber;
    }

    public final LocalContext makeSubContext()
    {
        return new LocalContext(this);
    }

    public LocalVariable getLocalVariable(String name)
    {
        LocalVariable rtnValue = localVariableMap.get(name);
        return rtnValue != null ? rtnValue : parentContext.getLocalVariable(name);
    }

    public LocalType getClassContext()
    {
        return rootContext.getClassContext();
    }
    public void pushOperand(Type operandType)
    {
        rootContext.pushOperand(operandType);
    }
    public void popOperands(int n)
    {
        rootContext.popOperands(n);
    }
    public void popOperand()
    {
        rootContext.popOperand();
    }
    public boolean isOperandStackEmpty()
    {
        return rootContext.isOperandStackEmpty();
    }
    public String toString()
    {
        return rootContext.toString();
    }

    public BranchDestination getReturnDestination()
    {
        return returnDestination != null ? returnDestination : parentContext.getReturnDestination();
    }
    public void setReturnDestination(BranchDestination branchDestination)
    {
        returnDestination = branchDestination;
    }
    public BranchDestination getBreakDestination()
    {
        return breakDestination != null ? breakDestination : parentContext.getBreakDestination();
    }
    public void setBreakDestination(BranchDestination branchDestination)
    {
        breakDestination = branchDestination;
    }
    public BranchDestination getContinueDestination()
    {
        return continueDestination != null ? continueDestination : parentContext.getContinueDestination();
    }
    public void setContinueDestination(BranchDestination branchDestination)
    {
        continueDestination = branchDestination;
    }

}
