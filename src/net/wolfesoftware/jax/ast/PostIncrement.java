package net.wolfesoftware.jax.ast;

public class PostIncrement extends IncrementDecrement
{
    public PostIncrement(Expression expression)
    {
        super(expression);
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
