package net.wolfesoftware.jax.ast;

public class AmbiguousPreIncrementDecrement extends AmbiguousIncrementDecrement
{
    public AmbiguousPreIncrementDecrement(String operator, Expression expression)
    {
        super(expression, operator);
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
