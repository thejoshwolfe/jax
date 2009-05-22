package net.wolfesoftware.java.jax.ast;

public abstract class SwitchElement extends ParseElement
{
    public ParseElement content;

    public SwitchElement(ParseElement content)
    {
        this.content = content;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        content.decompile(indentation, out);
    }
}
