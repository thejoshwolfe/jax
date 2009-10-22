package net.wolfesoftware.jax.ast;

public class ShortCircuitOr extends ShortCircuitOperator
{
    public ShortCircuitOr(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    @Override
    protected String getOperator()
    {
        return "||";
    }

    public static final int TYPE = 0x29ee05a5;
    public int getElementType()
    {
        return TYPE;
    }
}
