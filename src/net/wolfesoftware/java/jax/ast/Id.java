package net.wolfesoftware.java.jax.ast;

public class Id extends ParseElement
{
    public final String name;

    public Id(String name)
    {
        this.name = name;
    }

    public static final int TYPE = 3;
    public int getElementType()
    {
        return TYPE;
    }
    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(name);
    }
}