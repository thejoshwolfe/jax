package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class StaticDereferenceField extends ParseElement
{
    public Field field;

    public TypeId typeId;
    public Id id;
    public StaticDereferenceField(TypeId typeId, Id id)
    {
        this.typeId = typeId;
        this.id = id;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        typeId.decompile(indentation, out);
        out.append('.');
        id.decompile(indentation, out);
    }

    public static final int TYPE = 0x637908a5;
    public int getElementType()
    {
        return TYPE;
    }
}
