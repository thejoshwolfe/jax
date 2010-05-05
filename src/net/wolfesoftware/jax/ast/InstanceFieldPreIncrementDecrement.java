package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class InstanceFieldPreIncrementDecrement extends InstanceFieldIncrementDecrement
{
    public InstanceFieldPreIncrementDecrement(Expression leftExpresion, Field field, String operator)
    {
        super(leftExpresion, field, operator);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(operator);
        leftExpression.decompile(indentation, out);
        out.append('.').append(field.name);
    }

    public static final int TYPE = 0xe9490d7d;
    public int getElementType()
    {
        return TYPE;
    }
}
