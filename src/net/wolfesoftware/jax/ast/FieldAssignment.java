package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class FieldAssignment extends ParseElement
{
    public Expression expression1;
    public Field field;
    public Expression expression2;
    public FieldAssignment(Expression expression1, Field field, Expression expression2)
    {
        this.expression1 = expression1;
        this.field = field;
        this.expression2 = expression2;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression1.decompile(indentation, out);
        out.append('.').append(field.name);
        out.append(" = ");
        expression2.decompile(indentation, out);
    }

    public static final int TYPE = 0x2e3305fe;
    public int getElementType()
    {
        return TYPE;
    }
}
