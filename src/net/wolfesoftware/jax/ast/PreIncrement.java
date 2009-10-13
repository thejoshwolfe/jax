package net.wolfesoftware.jax.ast;

public class PreIncrement extends ParseElement
{
    public Expression expression;
    public PreIncrement(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("++");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x1e3204cd;
    public int getElementType()
    {
        return TYPE;
    }

}
