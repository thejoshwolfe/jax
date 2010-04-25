package net.wolfesoftware.jax.ast;

public class AmbiguousMethodInvocation extends ParseElement
{
    public Expression leftExpression;
    public AmbiguousId methodName;
    public Arguments arguments;
    public AmbiguousMethodInvocation(Expression leftExpression, AmbiguousId methodName, Arguments arguments)
    {
        this.leftExpression = leftExpression;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        leftExpression.decompile(indentation, out);
        out.append('.');
        methodName.decompile(indentation, out);
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
