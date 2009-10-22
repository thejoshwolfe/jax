package net.wolfesoftware.jax.ast;

public class BooleanNot extends ParseElement
{
    public Expression expression;
    public BooleanNot(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append('!');
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x152803f2;
    public int getElementType()
    {
        return TYPE;
    }

}
