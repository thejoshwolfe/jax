package net.wolfesoftware.jax.semalysis;

import java.util.*;
import net.wolfesoftware.jax.ast.Id;

public class LocalContext
{
    public final LocalContext parentContext;
    private final RootLocalContext rootContext;
    protected final HashMap<String, LocalVariable> localVariables = new HashMap<String, LocalVariable>();
    private final ArrayList<SecretLocalVariable> secretLocalVariables = new ArrayList<SecretLocalVariable>();
    private final ArrayList<LocalContext> subContexts = new ArrayList<LocalContext>();

    private int wideVariableCount = 0;


    protected LocalContext(LocalContext parentContext)
    {
        this.parentContext = parentContext;
        if (parentContext == null) {
            rootContext = (RootLocalContext)this;
        } else {
            rootContext = parentContext.rootContext;
        }
    }

    public void addLocalVariable(Id id, Type type, ArrayList<SemalyticalException> errors)
    {
        // redefinition is not a fatal error (it's even allowed in C)
        if (localVariables.containsKey(id))
            errors.add(new SemalyticalException(id, "Redefinition of local variable"));
        id.variable = new LocalVariable(id.name, type);
        localVariables.put(id.name, id.variable);
        if (type.getSize() == 2)
            wideVariableCount++;
    }

    public SecretLocalVariable addSecretLocalVariable(Type type)
    {
        SecretLocalVariable secretLocalVariable = new SecretLocalVariable(type);
        secretLocalVariables.add(secretLocalVariable);
        return secretLocalVariable;
    }

    public LocalVariable getLocalVariable(String name)
    {
        LocalVariable rtnValue = localVariables.get(name);
        return rtnValue != null ? rtnValue : parentContext.getLocalVariable(name);
    }

    public LocalContext makeSubContext()
    {
        LocalContext subContext = new LocalContext(this);
        subContexts.add(subContext);
        return subContext;
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
