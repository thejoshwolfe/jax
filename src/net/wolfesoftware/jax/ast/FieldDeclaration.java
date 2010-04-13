package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class FieldDeclaration extends ParseElement
{
    public Field field;

    public FieldModifiers fieldModifiers;
    public TypeId typeId;
    public Id id;

    public FieldDeclaration(FieldModifiers fieldModifiers, TypeId type, Id id)
    {
        this.fieldModifiers = fieldModifiers;
        this.typeId = type;
        this.id = id;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        fieldModifiers.decompile(indentation, out);
        if (!fieldModifiers.elements.isEmpty())
            out.append(" ");
        typeId.decompile(indentation, out);
        out.append(" ");
        id.decompile(indentation, out);
    }

    public static final int TYPE = 0x3391064b;
    public int getElementType()
    {
        return TYPE;
    }
}
