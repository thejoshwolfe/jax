package net.wolfesoftware.jax.ast;

public class IdAssignment extends Assignment
{
    public Id id;
    public IdAssignment(Expression expression1, Expression expression2)
    {
        super(expression1, null, expression2);
        throw null;
    }

    public static final int TYPE = 0x1d9304c7;
    public int getElementType()
    {
        return TYPE;
    }
}
