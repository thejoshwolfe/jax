package net.wolfesoftware.jax.ast;

public class AmbiguousPreIncrementDecrement extends ParseElement
{
    public String operator;
    public Expression expression;
    public AmbiguousPreIncrementDecrement(String operator, Expression expression)
    {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(operator);
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0xb8fb0c10;
    public int getElementType()
    {
        return TYPE;
    }
}
