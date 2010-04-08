package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class DereferenceFieldAssignment extends ParseElement
{
    public Field field;

    public Expression expression1;
    public Id id;
    public Expression expression2;
    public DereferenceFieldAssignment(Expression expression1, Id id, Expression expression2)
    {
        this.expression1 = expression1;
        this.id = id;
        this.expression2 = expression2;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression1.decompile(indentation, out);
        out.append('.');
        id.decompile(indentation, out);
        out.append(" = ");
        expression2.decompile(indentation, out);
    }

    public static final int TYPE = 0x88d90a56;
    public int getElementType()
    {
        return TYPE;
    }
}
