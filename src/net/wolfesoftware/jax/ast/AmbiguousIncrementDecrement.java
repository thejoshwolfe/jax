package net.wolfesoftware.jax.ast;

public abstract class AmbiguousIncrementDecrement extends ParseElement
{
    public Expression expression;
    public String operator;
    public AmbiguousIncrementDecrement(Expression expression, String operator)
    {
        this.expression = expression;
        this.operator = operator;
    }
}
