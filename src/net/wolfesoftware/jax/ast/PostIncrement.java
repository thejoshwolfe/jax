package net.wolfesoftware.jax.ast;

public class PostIncrement extends ParseElement
{
    public Expression expression;
    public PostIncrement(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression.decompile(indentation, out);
        out.append("++");
    }

    public static final int TYPE = 0x2458054c;
    public int getElementType()
    {
        return TYPE;
    }

}
