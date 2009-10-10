package net.wolfesoftware.jax.ast;

public class Addition extends BinaryOperatorElement
{
    public Addition(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    public static final int TYPE = 0x0d88032d;
    public int getElementType()
    {
        return TYPE;
    }

    protected String getOperator()
    {
        return "+";
    }
}
