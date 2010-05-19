package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public class ArrayAssignment extends AbstractAssignment
{
    public Expression arrayExpression;
    public Expression indexExpression;
    public ArrayAssignment(Expression arrayExpression, Expression indexExpression, String operator, Expression rightExpression)
    {
        super(operator, rightExpression);
        this.arrayExpression = arrayExpression;
        this.indexExpression = indexExpression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        arrayExpression.decompile(indentation, out);
        out.append('[');
        indexExpression.decompile(indentation, out);
        out.append("] ").append(operator).append(' ');
        rightExpression.decompile(indentation, out);
    }

    @Override
    public Type getLeftType()
    {
        Type arrayType = arrayExpression.returnType;
        if (!(arrayType instanceof ArrayType))
            return UnknownType.INSTANCE;
        return ((ArrayType)arrayType).scalarType;
    }

    public static final int TYPE = 0x2f720619;
    public int getElementType()
    {
        return TYPE;
    }
}
