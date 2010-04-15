package net.wolfesoftware.jax.ast;

public abstract class AbstractAssignment extends ParseElement
{
    public String operator;
    public Expression rightExpression;
    public AbstractAssignment(String operator, Expression rightExpression)
    {
        this.operator = operator;
        this.rightExpression = rightExpression;
    }
}
