package net.wolfesoftware.jax.semalysizer;

import java.util.*;
import net.wolfesoftware.jax.ast.Id;

public class LocalContext
{
    public final LocalContext parentContext;
    private final RootLocalContext rootContext;
    public final HashMap<String, LocalVariable> localVariables = new HashMap<String, LocalVariable>();
    private int wideVariableCount = 0;

    private final int parentVariableCount;

    public LocalContext(LocalContext parentContext)
    {
        this.parentContext = parentContext;
        if (parentContext == null) {
            parentVariableCount = 0;
            rootContext = (RootLocalContext)this;
        } else {
            parentVariableCount = parentContext.getVariableCount();
            rootContext = parentContext.rootContext;
        }
    }

    public void addLocalVariable(Id id, Type type, ArrayList<SemalyticalException> errors)
    {
        if (localVariables.containsKey(id))
            errors.add(new SemalyticalException(id, "Redefinition of local variable"));
        int number = getVariableCount();
        id.variable = new LocalVariable(id.name, type, number);
        localVariables.put(id.name, id.variable);
        if (type.getSize() == 2)
            wideVariableCount++;
        rootContext.ensureVariableCapacity(getVariableCount());
    }

    private int getVariableCount()
    {
        return parentVariableCount + localVariables.size() + wideVariableCount;
    }

    public LocalVariable getLocalVariable(String name)
    {
        LocalVariable rtnValue = localVariables.get(name);
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
    public String toString()
    {
        return rootContext.toString();
    }
}
