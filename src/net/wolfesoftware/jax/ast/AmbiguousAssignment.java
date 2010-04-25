package net.wolfesoftware.jax.ast;

public class AmbiguousAssignment extends ParseElement
{
    public Expression expression1;
    public String operator;
    public Expression expression2;
    public AmbiguousAssignment(Expression expression1, String operator, Expression expression2)
    {
        this.expression1 = expression1;
        this.operator = operator;
        this.expression2 = expression2;
    }

    @Override
    protected final void decompile(String indentation, StringBuilder out)
    {
        expression1.decompile(indentation, out);
        out.append(' ').append(operator).append(' ');
        expression2.decompile(indentation, out);
    }

    public static final int TYPE = 0x4bf007c6;
    public int getElementType()
    {
        return TYPE;
    }
}
