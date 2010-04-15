package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class DereferenceField extends ParseElement
{
    public Field field;

    public Expression expression;
    public String fieldName;
    public DereferenceField(Expression expression, String fieldName)
    {
        this.expression = expression;
        this.fieldName = fieldName;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression.decompile(indentation, out);
        out.append('.').append(fieldName);
    }

    public static final int TYPE = 0x34a8063d;
    public int getElementType()
    {
        return TYPE;
    }
}
