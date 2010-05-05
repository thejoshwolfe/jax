package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public abstract class InstanceFieldIncrementDecrement extends AbstractIncrementDecrement
{
    public Expression leftExpression;
    public Field field;

    public InstanceFieldIncrementDecrement(Expression leftExpresion, Field field, String operator)
    {
        super(operator);
        this.leftExpression = leftExpresion;
        this.field = field;
    }

    @Override
    public Type getAssignmentTargetType()
    {
        return field.returnType;
    }
}
