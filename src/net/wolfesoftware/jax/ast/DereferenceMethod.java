package net.wolfesoftware.jax.ast;

public class DereferenceMethod extends ParseElement
{
    public Expression expression;
    public MethodInvocation methodInvocation;
    public DereferenceMethod(Expression expression, MethodInvocation methodInvocation)
    {
        this.expression = expression;
        this.methodInvocation = methodInvocation;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression.decompile(indentation, out);
        out.append('.');
        methodInvocation.decompile(indentation, out);
    }

    public static final int TYPE = 0x3ba506ba;
    public int getElementType()
    {
        return TYPE;
    }

}
