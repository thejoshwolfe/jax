package net.wolfesoftware.java.jax.ast;

public class GreaterThanOrEqual extends BinaryOperatorElement
{
    public GreaterThanOrEqual(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    public static final int TYPE = 0x4276070f;
    public int getElementType()
    {
        return TYPE;
    }

    protected String getOperator()
    {
        return ">=";
    }
}
