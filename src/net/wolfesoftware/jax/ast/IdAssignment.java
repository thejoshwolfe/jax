package net.wolfesoftware.jax.ast;

public class IdAssignment extends ParseElement
{
    public Id id;
    public String operator;
    public Expression expression;
    public IdAssignment(Id id, String operator, Expression expression)
    {
        this.id = id;
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        id.decompile(indentation, out);
        out.append(' ').append(operator).append(' ');
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x1d9304c7;
    public int getElementType()
    {
        return TYPE;
    }
}
