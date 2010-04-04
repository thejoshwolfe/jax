package net.wolfesoftware.jax.semalysis;

import java.util.*;
import net.wolfesoftware.jax.ast.Id;

public class LocalContext
{
    public final LocalContext parentContext;
    private final RootLocalContext rootContext;
    private final ArrayList<LocalVariable> localVariableList = new ArrayList<LocalVariable>();
    protected final HashMap<String, LocalVariable> localVariableMap = new HashMap<String, LocalVariable>();
    private final ArrayList<SecretLocalVariable> secretLocalVariables = new ArrayList<SecretLocalVariable>();
    protected final ArrayList<LocalContext> subContexts = new ArrayList<LocalContext>();


    protected LocalContext(LocalContext parentContext)
    {
        this.parentContext = parentContext;
        if (parentContext == null) {
            rootContext = (RootLocalContext)this;
        } else {
            rootContext = parentContext.rootContext;
        }
    }

    public final void addLocalVariable(Id id, Type type, ArrayList<SemalyticalError> errors)
    {
        // redefinition is not a fatal error (it's even allowed in C)
        if (getLocalVariable(id.name) != null)
            errors.add(new SemalyticalError(id, "Redefinition of local variable"));
        id.variable = new LocalVariable(id.name, type);
        localVariableList.add(id.variable);
        localVariableMap.put(id.name, id.variable);
    }

    public final SecretLocalVariable addSecretLocalVariable(Type type)
    {
        SecretLocalVariable secretLocalVariable = new SecretLocalVariable(type);
        secretLocalVariables.add(secretLocalVariable);
        return secretLocalVariable;
    }

    protected final int internalGetLocalVariableCapacity()
    {
        int max = 0;
        for (LocalContext subContext : subContexts) {
            int subMax = subContext.internalGetLocalVariableCapacity();
            if (max < subMax)
                max = subMax;
        }
        for (LocalVariable localVariable : localVariableList)
            max += localVariable.type.getSize();
        return max;
    }

    protected final void internalNumberLocalVariables(int counter)
    {
        for (LocalVariable localVariable : localVariableList) {
            if (localVariable.number == -1) {
                localVariable.number = counter;
                counter += localVariable.type.getSize();
            }
        }
        for (LocalContext subContext : subContexts)
            subContext.internalNumberLocalVariables(counter);
    }

    public final LocalContext makeSubContext()
    {
        LocalContext subContext = new LocalContext(this);
        subContexts.add(subContext);
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
    public Type popOperand()
    {
        return rootContext.popOperand();
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
