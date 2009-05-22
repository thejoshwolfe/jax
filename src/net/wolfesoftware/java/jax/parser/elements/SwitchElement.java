package net.wolfesoftware.java.jax.parser.elements;

public abstract class SwitchElement extends ParseElement
{
    public ParseElement content;

    public SwitchElement(ParseElement content)
    {
        this.content = content;
    }

    @Override
    protected void decompile(String indentation, StringBuffer out)
    {
        content.decompile(indentation, out);
    }
}
