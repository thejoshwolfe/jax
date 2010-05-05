package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public abstract class StaticFieldIncrementDecrement extends AbstractIncrementDecrement
{
    public Field field;

    public StaticFieldIncrementDecrement(Field field, String operator)
    {
        super(operator);
        this.field = field;
    }

    @Override
    public Type getAssignmentTargetType()
    {
        return field.returnType;
    }
}
