package net.wolfesoftware.jax.ast;

public class FieldCreation extends ParseElement
{
    public FieldModifiers fieldModifiers;
    public TypeId typeId;
    public Id id;
    public Expression expression;

    public FieldCreation(FieldModifiers fieldModifiers, TypeId type, Id id, Expression expression)
    {
        this.fieldModifiers = fieldModifiers;
        this.typeId = type;
        this.id = id;
        this.expression = expression;
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
        out.append(" = ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x3391064b;
    public int getElementType()
    {
        return TYPE;
    }
}
