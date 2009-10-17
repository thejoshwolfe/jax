package net.wolfesoftware.jax.ast;

public abstract class IncrementDecrement extends ParseElement
{
    public Expression expression;
    /** this field store the expression's content casted to an Id after the Lexiconization phase */
    public Id id;
    public IncrementDecrement(Expression expression)
    {
        this.expression = expression;
    }
}
