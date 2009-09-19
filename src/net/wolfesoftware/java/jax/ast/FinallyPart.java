package net.wolfesoftware.java.jax.ast;

public class FinallyPart extends ParseElement
{
    public Expression expression;

    public FinallyPart(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("finally ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x19a10467;
    public int getElementType()
    {
        return TYPE;
    }
}
