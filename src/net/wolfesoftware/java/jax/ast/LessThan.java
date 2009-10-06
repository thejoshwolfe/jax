package net.wolfesoftware.java.jax.ast;

public class LessThan extends ComparisonOperator
{
    public LessThan(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    public static final int TYPE = 0x0dd40323;
    public int getElementType()
    {
        return TYPE;
    }

    protected String getOperator()
    {
        return "<";
    }
}
