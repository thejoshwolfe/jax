package net.wolfesoftware.jax.ast;

public class DereferenceMethod extends ParseElement
{
    public Expression expression;
    public String methodName;
    public Arguments arguments;
    public DereferenceMethod(Expression expression, String methodName, Arguments arguments)
    {
        this.expression = expression;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression.decompile(indentation, out);
        out.append('.').append(methodName).append('(');
        arguments.decompile(indentation, out);
        out.append(')');
    }

    public static final int TYPE = 0x3ba506ba;
    public int getElementType()
    {
        return TYPE;
    }

}
