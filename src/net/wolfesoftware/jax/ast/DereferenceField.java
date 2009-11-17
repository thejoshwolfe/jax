package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.lexiconizer.Field;

public class DereferenceField extends ParseElement
{
    public Field field;

    public Expression expression;
    public Id id;
    public DereferenceField(Expression expression, Id id)
    {
        this.expression = expression;
        this.id = id;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression.decompile(indentation, out);
        out.append('.');
        id.decompile(indentation, out);
    }

    public static final int TYPE = 0x34a8063d;
    public int getElementType()
    {
        return TYPE;
    }
}
