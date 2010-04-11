package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Method;

public class MethodInvocation extends ParseElement
{
    public Method method;
    
    public Id id;
    public Arguments arguments;
    public MethodInvocation(Id id, Arguments arguments)
    {
        this.id = id;
        this.arguments = arguments;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        id.decompile(indentation, out);
        out.append("(");
        arguments.decompile(indentation, out);
        out.append(")");
    }

    public static final int TYPE = 0x450a0761;
    public int getElementType()
    {
        return TYPE;
    }
}