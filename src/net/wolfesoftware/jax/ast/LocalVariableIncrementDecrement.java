package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public abstract class LocalVariableIncrementDecrement extends AbstractIncrementDecrement
{
    public LocalVariable variable;

    public LocalVariableIncrementDecrement(LocalVariable variable, String operator)
    {
        super(operator);
        this.variable = variable;
    }
    @Override
    public Type getAssignmentTargetType()
    {
        return variable.type;
    }
}
