package net.wolfesoftware.jax.ast;

public class AmbiguousAssignment extends ParseElement
{
    public Expression leftExpression;
    public String operator;
    public Expression rightExpression;
    public AmbiguousAssignment(Expression expression1, String operator, Expression expression2)
    {
        this.leftExpression = expression1;
        this.operator = operator;
        this.rightExpression = expression2;
    }

    @Override
    protected final void decompile(String indentation, StringBuilder out)
    {
        leftExpression.decompile(indentation, out);
        out.append(' ').append(operator).append(' ');
        rightExpression.decompile(indentation, out);
    }

    public static final int TYPE = 0x4bf007c6;
    public int getElementType()
    {
        return TYPE;
    }
}
