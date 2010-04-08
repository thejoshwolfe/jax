package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Constructor;

public class ConstructorRedirectThis extends ParseElement
{
    public Constructor constructor;

    public Arguments arguments;
    public ConstructorRedirectThis(Arguments arguments)
    {
        this.arguments = arguments;
    }
    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("this(");
        arguments.decompile(indentation, out);
        out.append(')');
    }
    public static final int TYPE = 0x4e3207d9;
    public int getElementType()
    {
        return TYPE;
    }
}
