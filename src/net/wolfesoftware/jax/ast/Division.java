package net.wolfesoftware.jax.ast;

public class Division extends BinaryOperatorElement
{
    public Division(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    public static final int TYPE = 0x0e2b0346;
    public int getElementType()
    {
        return TYPE;
    }

    protected String getOperator()
    {
        return "/";
    }
}
