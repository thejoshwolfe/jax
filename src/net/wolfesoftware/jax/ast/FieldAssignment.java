package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class FieldAssignment extends GenericAssignment
{
    public Field field;

    public Expression leftExpression;
    public FieldAssignment(Expression leftExpression, Id id, String operator, Expression rightExpression)
    {
        super(id, operator, rightExpression);
        this.leftExpression = leftExpression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        leftExpression.decompile(indentation, out);
        out.append('.');
        id.decompile(indentation, out);
        out.append(' ').append(operator).append(' ');
        rightExpression.decompile(indentation, out);
    }

    public static final int TYPE = 0x2e3305fe;
    public int getElementType()
    {
        return TYPE;
    }
}
