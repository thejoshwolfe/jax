package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysizer.Constructor;

public class ConstructorRedirect extends ParseElement
{
    public Constructor constructor;

    public String thisOrSuper;
    public Arguments arguments;
    public ConstructorRedirect(String thisOrSuper, Arguments arguments)
    {
        this.thisOrSuper = thisOrSuper;
        this.arguments = arguments;
    }
    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(thisOrSuper).append('(');
        arguments.decompile(indentation, out);
        out.append(')');
    }
    public static final int TYPE = 0x4e3207d9;
    public int getElementType()
    {
        return TYPE;
    }
}
