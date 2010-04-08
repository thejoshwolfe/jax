package net.wolfesoftware.jax.ast;

public class Throw extends ParseElement
{
    public Expression expression;

    public Throw(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("throw ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x05f40215;
    public int getElementType()
    {
        return TYPE;
    }
}
