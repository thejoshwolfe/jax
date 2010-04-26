package net.wolfesoftware.jax.ast;

public class AmbiguousImplicitThisMethodInvocation extends ParseElement
{
    public AmbiguousId methodName;
    public Arguments arguments;
    public AmbiguousImplicitThisMethodInvocation(AmbiguousId methodName, Arguments arguments)
    {
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        methodName.decompile(indentation, out);
        out.append("(");
        arguments.decompile(indentation, out);
        out.append(")");
    }

    public static final int TYPE = 0x1a2f0efb;
    public int getElementType()
    {
        return TYPE;
    }
}
