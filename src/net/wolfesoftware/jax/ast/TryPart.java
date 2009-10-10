package net.wolfesoftware.jax.ast;

public class TryPart extends ParseElement
{
    public Expression expression;
    public String startLabel;
    public String endLabel;

    public TryPart(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("try ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x0b1702d7;
    public int getElementType()
    {
        return TYPE;
    }
}
