package net.wolfesoftware.java.jax.parser.elements;

public class Quantity extends ParseElement
{
    public Expression expression;

    public Quantity(Expression expression)
    {
        this.expression = expression;
    }

    public static final int TYPE = 0x0e9b0360;
    public int getElementType()
    {
        return TYPE;
    }
    @Override
    protected void decompile(String indentation, StringBuffer out)
    {
        out.append("(");
        expression.decompile(indentation, out);
        out.append(")");
    }
}
