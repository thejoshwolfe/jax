package net.wolfesoftware.java.jax.lexiconizer;

import java.util.*;
import net.wolfesoftware.java.jax.ast.Id;

public class LocalContext
{
    public final LocalContext parentContext;
    private final RootLocalContext rootContext;
    public final HashMap<String, LocalVariable> localVariables = new HashMap<String, LocalVariable>();
    public final ArrayList<LexicalException> errors;

    private final int parentVariableCount;

    public LocalContext(ArrayList<LexicalException> errors, LocalContext parentContext)
    {
        this.errors = errors;
        this.parentContext = parentContext;
        if (parentContext == null) {
            parentVariableCount = 0;
            rootContext = (RootLocalContext)this;
        } else {
            parentVariableCount = parentContext.getVariableCount();
            rootContext = parentContext.rootContext;
        }
    }

    public void addLocalVariable(Id id, Type type)
    {
        if (localVariables.containsKey(id))
            errors.add(new LexicalException());
        int number = getVariableCount();
        id.variable = new LocalVariable(id.name, type, number);
        localVariables.put(id.name, id.variable);
        rootContext.ensureVariableCapacity(getVariableCount());
    }

    private int getVariableCount()
    {
        return parentVariableCount + localVariables.size();
    }

    public LocalVariable getLocalVariable(String name)
    {
        return localVariables.get(name);
    }
}
