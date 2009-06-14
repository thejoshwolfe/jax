package net.wolfesoftware.java.jax.ast;

public class DereferenceMethod extends ParseElement
{
    public Expression expression;
    public FunctionInvocation functionInvocation;
    public DereferenceMethod(Expression expression, FunctionInvocation functionInvocation)
    {
        this.expression = expression;
        this.functionInvocation = functionInvocation;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression.decompile(indentation, out);
        out.append('.');
        functionInvocation.decompile(indentation, out);
    }

    public static final int TYPE = 0x3ba506ba;
    public int getElementType()
    {
        return TYPE;
    }

}
