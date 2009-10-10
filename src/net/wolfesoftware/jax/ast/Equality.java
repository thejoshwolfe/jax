package net.wolfesoftware.jax.ast;

public class Equality extends ComparisonOperator
{
    public Equality(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    public static final int TYPE = 0x0e36034f;
    public int getElementType()
    {
        return TYPE;
    }

    protected String getOperator()
    {
        return "==";
    }
}
