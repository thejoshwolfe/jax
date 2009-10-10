package net.wolfesoftware.jax.ast;

public class LessThanOrEqual extends ComparisonOperator
{
    public LessThanOrEqual(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    public static final int TYPE = 0x2e4805dc;
    public int getElementType()
    {
        return TYPE;
    }

    protected String getOperator()
    {
        return "<=";
    }
}
