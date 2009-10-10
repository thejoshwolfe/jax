package net.wolfesoftware.jax.ast;

public class Inequality extends ComparisonOperator
{
    public Inequality(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    public static final int TYPE = 0x15f00426;
    public int getElementType()
    {
        return TYPE;
    }

    protected String getOperator()
    {
        return "!=";
    }
}
