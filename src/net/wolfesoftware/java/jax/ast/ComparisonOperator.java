package net.wolfesoftware.java.jax.ast;

public abstract class ComparisonOperator extends BinaryOperatorElement
{
    public String label1 = null;
    public String label2 = null;
    
    public ComparisonOperator(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }
}
