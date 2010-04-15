package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class StaticDereferenceField extends ParseElement
{
    public Field field;

    public StaticDereferenceField(Field field)
    {
        this.field = field;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(field.declaringType.simpleName).append('.').append(field.name);
    }

    public static final int TYPE = 0x637908a5;
    public int getElementType()
    {
        return TYPE;
    }
}
