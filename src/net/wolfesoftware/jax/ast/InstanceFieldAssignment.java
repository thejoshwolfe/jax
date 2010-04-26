package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public class InstanceFieldAssignment extends AbstractAssignment
{
    public Field field;

    public Expression leftExpression;
    public InstanceFieldAssignment(Expression leftExpression, Field field, String operator, Expression rightExpression)
    {
        super(operator, rightExpression);
        this.leftExpression = leftExpression;
        this.field = field;
    }

    @Override
    public Type getLeftType()
    {
        return field.returnType;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        leftExpression.decompile(indentation, out);
        out.append('.').append(field.name).append(' ').append(operator).append(' ');
        rightExpression.decompile(indentation, out);
    }

    public static final int TYPE = 0x6c8f0933;
    public int getElementType()
    {
        return TYPE;
    }
}
