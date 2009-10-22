package net.wolfesoftware.jax.ast;

public class ShortCircuitAnd extends ShortCircuitOperator
{
    public ShortCircuitAnd(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    @Override
    protected String getOperator()
    {
        return "&&";
    }

    public static final int TYPE = 0x2fc505f7;
    public int getElementType()
    {
        return TYPE;
    }
}
