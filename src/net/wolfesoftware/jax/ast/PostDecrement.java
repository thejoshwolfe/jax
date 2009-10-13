package net.wolfesoftware.jax.ast;

public class PostDecrement extends ParseElement
{
    public Expression expression;
    public PostDecrement(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression.decompile(indentation, out);
        out.append("--");
    }

    public static final int TYPE = 0x23e3053e;
    public int getElementType()
    {
        return TYPE;
    }

}