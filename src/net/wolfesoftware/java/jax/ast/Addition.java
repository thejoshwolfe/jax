package net.wolfesoftware.java.jax.ast;

public class Addition extends ParseElement
{
    public Expression expression1;
    public Expression expression2;

    public Addition(Expression expression1, Expression expression2)
    {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    public static final int TYPE = 0x0d88032d;
    public int getElementType()
    {
        return TYPE;
    }

    protected void decompile(String indentation, StringBuilder out)
    {
        expression1.decompile(indentation, out);
        out.append(" + ");
        expression2.decompile(indentation, out);
    }
}
