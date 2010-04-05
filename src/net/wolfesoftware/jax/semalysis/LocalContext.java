package net.wolfesoftware.jax.semalysis;

import java.util.*;
import net.wolfesoftware.jax.ast.Id;

public class LocalContext
{
    public final LocalContext parentContext;
    private final RootLocalContext rootContext;
    protected final HashMap<String, LocalVariable> localVariableMap = new HashMap<String, LocalVariable>();
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

    public final void addLocalVariable(Id id, Type type, ArrayList<SemalyticalError> errors)
    {
        // redefinition is not a fatal error (it's even allowed in C)
        if (getLocalVariable(id.name) != null)
            errors.add(new SemalyticalError(id, "Redefinition of local variable"));
        id.variable = new LocalVariable(this, id.name, type);
        localVariableMap.put(id.name, id.variable);
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
        LocalContext subContext = new LocalContext(this);
        return subContext;
    }

    public LocalVariable getLocalVariable(String name)
    {
        LocalVariable rtnValue = localVariableMap.get(name);
        return rtnValue != null ? rtnValue : parentContext.getLocalVariable(name);
    }

    public String nextLabel()
    {
        return rootContext.nextLabel();
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
}
