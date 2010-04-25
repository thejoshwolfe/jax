package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Field;

public class InstanceFieldExpression extends ParseElement
{
    public Expression leftExpression;
    public Field field;
    public InstanceFieldExpression(Expression leftExpression, Field field)
    {
        this.leftExpression = leftExpression;
        this.field = field;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        leftExpression.decompile(indentation, out);
        out.append('.').append(field.name);
    }

    public static final int TYPE = 0x75b20972;
    public int getElementType()
    {
        return TYPE;
    }
}
