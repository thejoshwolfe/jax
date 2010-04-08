package net.wolfesoftware.jax.ast;

public class ThisExpression extends ParseElement
{
    private ThisExpression()
    {
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("this");
    }

    public static final int TYPE = 0x2a4305c9;
    public int getElementType()
    {
        return TYPE;
    }

    public static final ThisExpression INSTANCE = new ThisExpression();
}
