package net.wolfesoftware.jax.ast;

public class AmbiguousPostIncrementDecrement extends ParseElement
{
    public Expression expression;
    public String operator;
    public AmbiguousPostIncrementDecrement(Expression expression, String operator)
    {
        this.expression = expression;
        this.operator = operator;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression.decompile(indentation, out);
        out.append(operator);
    }

    public static final int TYPE = 0xc7440c8f;
    public int getElementType()
    {
        return TYPE;
    }
}
