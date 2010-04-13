package net.wolfesoftware.jax.ast;

public class IdAssignment extends GenericAssignment
{
    public IdAssignment(Id id, String operator, Expression rightExpression)
    {
        super(id, operator, rightExpression);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        id.decompile(indentation, out);
        out.append(' ').append(operator).append(' ');
        rightExpression.decompile(indentation, out);
    }

    public static final int TYPE = 0x1d9304c7;
    public int getElementType()
    {
        return TYPE;
    }
}
