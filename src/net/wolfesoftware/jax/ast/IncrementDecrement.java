package net.wolfesoftware.jax.ast;

public abstract class IncrementDecrement extends ParseElement
{
    public Expression expression;
    public IncrementDecrement(Expression expression)
    {
        this.expression = expression;
    }
}
