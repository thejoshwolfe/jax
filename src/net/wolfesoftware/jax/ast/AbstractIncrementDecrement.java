package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Type;

public abstract class AbstractIncrementDecrement extends ParseElement
{
    public String operator;
    protected AbstractIncrementDecrement(String operator)
    {
        this.operator = operator;
    }

    public abstract Type getAssignmentTargetType();
}
