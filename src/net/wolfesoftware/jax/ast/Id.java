package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.LocalVariable;

public class Id extends ParseElement
{
    public LocalVariable variable;

    public final String name;

    public Id(String name)
    {
        this.name = name;
    }

    public static final int TYPE = 0x00f800ae;
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
