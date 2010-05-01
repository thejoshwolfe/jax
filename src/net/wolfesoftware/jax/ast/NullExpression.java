package net.wolfesoftware.jax.ast;

public class NullExpression extends ParseElement
{
    private NullExpression()
    {
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("null");
    }

    public static final int TYPE = 0x2a6f05cc;
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected boolean isSingletonLike()
    {
        return true;
    }

    public static final NullExpression INSTANCE = new NullExpression();
}
