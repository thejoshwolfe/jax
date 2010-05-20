package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class FieldDeclaration extends ParseElement
{
    public Field field;

    public Modifiers fieldModifiers;
    public TypeId typeId;
    public String fieldName;

    public FieldDeclaration(Modifiers fieldModifiers, TypeId type, String fieldName)
    {
        this.fieldModifiers = fieldModifiers;
        this.typeId = type;
        this.fieldName = fieldName;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        fieldModifiers.decompile(indentation, out);
        if (!fieldModifiers.elements.isEmpty())
            out.append(" ");
        typeId.decompile(indentation, out);
        out.append(" ");
        out.append(fieldName);
    }

    public static final int TYPE = 0x3391064b;
    public int getElementType()
    {
        return TYPE;
    }
}
