package net.wolfesoftware.java.jax.ast;

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
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("(");
        expression.decompile(indentation, out);
        out.append(")");
    }
}
