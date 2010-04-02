package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public class ConstructorInvocation extends ParseElement
{
    public FunctionInvocation functionInvocation;
    public Constructor constructor;
    public ConstructorInvocation(FunctionInvocation functionInvocation)
    {
        this.functionInvocation = functionInvocation;
    }

    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("new ");
        functionInvocation.decompile(indentation, out);
    }

    public static final int TYPE = 0x1b1104a7;
    public int getElementType()
    {
        return TYPE;
    }
}
