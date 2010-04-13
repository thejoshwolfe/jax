package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class StaticFieldAssignment extends GenericAssignment
{
    public Field field;

    public TypeId typeId;
    public StaticFieldAssignment(TypeId typeId, Id id, String operator, Expression rightExpression)
    {
        super(id, operator, rightExpression);
        this.typeId = typeId;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        typeId.decompile(indentation, out);
        out.append('.');
        id.decompile(indentation, out);
        out.append(' ').append(operator).append(' ');
        rightExpression.decompile(indentation, out);
    }

    public static final int TYPE = 0x5a9c0866;
    public int getElementType()
    {
        return TYPE;
    }
}
