package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Constructor;

public class ConstructorRedirectSuper extends ParseElement
{
    public Constructor constructor;

    public Arguments arguments;
    public ConstructorRedirectSuper(Arguments arguments)
    {
        this.arguments = arguments;
    }
    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("super(");
        arguments.decompile(indentation, out);
        out.append(')');
    }
    public static final int TYPE = 0x7b6e09e8;
    public int getElementType()
    {
        return TYPE;
    }
}
