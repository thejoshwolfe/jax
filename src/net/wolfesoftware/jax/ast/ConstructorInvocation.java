package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public class ConstructorInvocation extends ParseElement
{
    public String typeName;
    public Arguments arguments;
    public Constructor constructor;
    public ConstructorInvocation(String typeName, Arguments arguments)
    {
        this.typeName = typeName;
        this.arguments = arguments;
    }

    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("new ").append(typeName).append('(');
        arguments.decompile(indentation, out);
        out.append(')');
    }

    public static final int TYPE = 0x1b1104a7;
    public int getElementType()
    {
        return TYPE;
    }
}
