package net.wolfesoftware.jax.ast;

public abstract class ShortCircuitOperator extends BinaryOperatorElement
{
    public ShortCircuitOperator(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }
}
