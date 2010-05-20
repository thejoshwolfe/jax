package net.wolfesoftware.jax.ast;

public abstract class ComparisonOperator extends BinaryOperatorElement
{
    public ComparisonOperator(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }
}
