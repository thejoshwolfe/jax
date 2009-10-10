package net.wolfesoftware.jax.ast;

public class Subtraction extends BinaryOperatorElement
{
    public Subtraction(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    public static final int TYPE = 0x1aea048f;
    public int getElementType()
    {
        return TYPE;
    }
    protected String getOperator()
    {
        return "-";
    }
}
