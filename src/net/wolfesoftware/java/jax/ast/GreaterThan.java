package net.wolfesoftware.java.jax.ast;

public class GreaterThan extends ComparisonOperator
{
    public GreaterThan(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    public static final int TYPE = 0x199d0456;
    public int getElementType()
    {
        return TYPE;
    }

    protected String getOperator()
    {
        return ">";
    }
}
