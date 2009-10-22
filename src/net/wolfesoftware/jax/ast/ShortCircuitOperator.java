package net.wolfesoftware.jax.ast;

public abstract class ShortCircuitOperator extends BinaryOperatorElement
{
    public String label1;
    public String label2;
    public ShortCircuitOperator(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }
}
