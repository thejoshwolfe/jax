package net.wolfesoftware.java.jax.ast;

import net.wolfesoftware.java.jax.lexiconizer.Method;

public class FunctionInvocation extends ParseElement
{
    public Method method;
    
    public Id id;
    public Arguments arguments;
    public FunctionInvocation(Id id, Arguments arguments)
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
