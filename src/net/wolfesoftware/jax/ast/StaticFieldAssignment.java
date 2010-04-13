package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public class StaticFieldAssignment extends ParseElement
{
    public Field field;
    public Expression expression;
    public StaticFieldAssignment(Field field, Expression expression)
    {
        this.field = field;
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(field.declaringType.qualifiedName).append('.').append(field.name);
        out.append(" = ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x5a9c0866;
    public int getElementType()
    {
        return TYPE;
    }
}
