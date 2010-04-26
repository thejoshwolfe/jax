package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Type;

public abstract class AbstractAssignment extends ParseElement
{
    public String operator;
    public Expression rightExpression;
    public AbstractAssignment(String operator, Expression rightExpression)
    {
        this.operator = operator;
        this.rightExpression = rightExpression;
    }
    public abstract Type getLeftType();
}
