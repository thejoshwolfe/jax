package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public class StaticFieldAssignment extends AbstractAssignment
{
    public Field field;

    public StaticFieldAssignment(Field field, String operator, Expression rightExpression)
    {
        super(operator, rightExpression);
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
        out.append(field.declaringType.simpleName).append('.').append(field.name);
        out.append(' ').append(operator).append(' ');
        rightExpression.decompile(indentation, out);
    }

    public static final int TYPE = 0x5a9c0866;
    public int getElementType()
    {
        return TYPE;
    }
}
