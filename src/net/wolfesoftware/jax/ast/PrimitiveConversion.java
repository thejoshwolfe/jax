package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.lexiconizer.*;

public class PrimitiveConversion extends ParseElement
{
    public Expression expression;
    public String instruction;
    private Type toType;
    private PrimitiveConversion(Expression expression, String instruction, Type toType)
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

    public static PrimitiveConversion getConversion(Type fromType, Type toType, Expression expression)
    {
        if (fromType == RuntimeType.INT) {
            if (toType == RuntimeType.BYTE)
                return new PrimitiveConversion(expression, "i2b", toType);
        }
        return null;
    }
}
