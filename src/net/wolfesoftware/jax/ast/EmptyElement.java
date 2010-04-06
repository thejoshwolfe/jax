package net.wolfesoftware.jax.ast;

public class EmptyElement extends ParseElement
{
    private EmptyElement()
    {
    }

    public static final EmptyElement INSTANCE = new EmptyElement();

    public static final int TYPE = 0x1eca04da;
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
    }
}
