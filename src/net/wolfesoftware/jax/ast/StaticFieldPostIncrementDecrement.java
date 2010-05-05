package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class StaticFieldPostIncrementDecrement extends StaticFieldIncrementDecrement
{
    public StaticFieldPostIncrementDecrement(Field field, String operator)
    {
        super(field, operator);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(field.declaringType.simpleName).append('.').append(field.name).append(operator);
    }

    public static final int TYPE = 0xdd700d2f;
    public int getElementType()
    {
        return TYPE;
    }
}
