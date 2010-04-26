package net.wolfesoftware.jax.ast;

public class AmbiguousPostIncrementDecrement extends AmbiguousIncrementDecrement
{
    public AmbiguousPostIncrementDecrement(Expression expression, String operator)
    {
        super(expression, operator);
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
