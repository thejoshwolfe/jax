package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public class ConstructorInvocation extends ParseElement
{
    public MethodInvocation methodInvocation;
    public Constructor constructor;
    public ConstructorInvocation(MethodInvocation methodInvocation)
    {
        this.methodInvocation = methodInvocation;
    }

    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("new ");
        methodInvocation.decompile(indentation, out);
    }

    public static final int TYPE = 0x1b1104a7;
    public int getElementType()
    {
        return TYPE;
    }
}
