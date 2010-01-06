package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysizer.Type;

public class PrimitiveConversion extends ParseElement
{
    public Expression expression;
    public byte instruction;
    public Type toType;
    public PrimitiveConversion(Expression expression, byte instruction, Type toType)
    {
        this.expression = expression;
        this.instruction = instruction;
        this.toType = toType;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append('(').append(toType.fullName).append(')');
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x4d9d07e0;
    public int getElementType()
    {
        return TYPE;
    }
}
