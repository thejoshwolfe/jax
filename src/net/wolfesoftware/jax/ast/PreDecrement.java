package net.wolfesoftware.jax.ast;

public class PreDecrement extends IncrementDecrement
{
    public PreDecrement(Expression expression)
    {
        super(expression);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("--");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x1dbd04bf;
    public int getElementType()
    {
        return TYPE;
    }

}
