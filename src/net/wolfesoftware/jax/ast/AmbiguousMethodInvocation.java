package net.wolfesoftware.jax.ast;

public class AmbiguousMethodInvocation extends ParseElement
{
    public String methodName;
    public Arguments arguments;
    public AmbiguousMethodInvocation(String methodName, Arguments arguments)
    {
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(methodName);
        out.append("(");
        arguments.decompile(indentation, out);
        out.append(")");
    }

    public static final int TYPE = 0x82090a28;
    public int getElementType()
    {
        return TYPE;
    }
}
