package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class StaticFieldPreIncrementDecrement extends StaticFieldIncrementDecrement
{
    public StaticFieldPreIncrementDecrement(Field field, String operator)
    {
        super(field, operator);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(operator).append(field.declaringType.simpleName).append('.').append(field.name);
    }

    public static final int TYPE = 0xce870cb0;
    public int getElementType()
    {
        return TYPE;
    }

}
