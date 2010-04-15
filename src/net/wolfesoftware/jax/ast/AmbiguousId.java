package net.wolfesoftware.jax.ast;

public class AmbiguousId extends ParseElement
{
    public final String text;

    public AmbiguousId(String text)
    {
        this.text = text;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(text);
    }

    public static final int TYPE = 0x19af045a;
    public int getElementType()
    {
        return TYPE;
    }
}
