package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class InstanceFieldPostIncrementDecrement extends InstanceFieldIncrementDecrement
{
    public InstanceFieldPostIncrementDecrement(Expression leftExpresion, Field field, String operator)
    {
        super(leftExpresion, field, operator);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        leftExpression.decompile(indentation, out);
        out.append('.').append(field.name).append(operator);
    }

    public static final int TYPE = 0xf8ff0dfc;
    public int getElementType()
    {
        return TYPE;
    }
}
