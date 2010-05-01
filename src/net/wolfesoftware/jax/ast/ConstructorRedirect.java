package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Constructor;

public class ConstructorRedirect extends ParseElement
{
    public Constructor constructor;

    public String keyword;
    public Arguments arguments;
    public ConstructorRedirect(String keyword, Arguments arguments)
    {
        this.keyword = keyword;
        this.arguments = arguments;
    }
    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(keyword).append('(');
        arguments.decompile(indentation, out);
        out.append(')');
    }
    public static final int TYPE = 0x4e3207d9;
    public int getElementType()
    {
        return TYPE;
    }
}
