package net.wolfesoftware.jax.ast;

public class InstanceMethodInvocation extends AbstractMethodInvocation
{
    public Expression leftExpression;
    public InstanceMethodInvocation(Expression leftExpression, AmbiguousId methodName, Arguments arguments)
    {
        super(methodName, arguments);
        this.leftExpression = leftExpression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        leftExpression.decompile(indentation, out);
        out.append('.');
        methodName.decompile(indentation, out);
        out.append('(');
        arguments.decompile(indentation, out);
        out.append(')');
    }

    public static final int TYPE = 0x777b09b1;
    public int getElementType()
    {
        return TYPE;
    }
}
