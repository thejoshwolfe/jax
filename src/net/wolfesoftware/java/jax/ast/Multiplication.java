package net.wolfesoftware.java.jax.ast;

public class Multiplication extends BinaryOperatorElement
{
    public Multiplication(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
    }

    public static final int TYPE = 0x2b1805cf;
    public int getElementType()
    {
        return TYPE;
    }

    protected String getOperator()
    {
        return "*";
    }
}
