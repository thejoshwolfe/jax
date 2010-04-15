package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class FieldAssignment extends AbstractAssignment
{
    public Field field;

    public Expression leftExpression;
    public String fieldName;
    public FieldAssignment(Expression leftExpression, String fieldName, String operator, Expression rightExpression)
    {
        super(operator, rightExpression);
        this.leftExpression = leftExpression;
        this.fieldName = fieldName;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        leftExpression.decompile(indentation, out);
        out.append('.').append(fieldName).append(' ').append(operator).append(' ');
        rightExpression.decompile(indentation, out);
    }

    public static final int TYPE = 0x2e3305fe;
    public int getElementType()
    {
        return TYPE;
    }
}
