package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysizer.*;

public class ReferenceConversion extends ParseElement
{
    public Expression expression;
    public Type toType;
    public ReferenceConversion(Expression expression, Type toType)
    {
        this.expression = expression;
        this.toType = toType;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append('(').append(toType.fullName).append(')');
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x4b3807b6;
    public int getElementType()
    {
        return TYPE;
    }
}
