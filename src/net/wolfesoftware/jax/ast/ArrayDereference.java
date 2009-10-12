package net.wolfesoftware.jax.ast;

public class ArrayDereference extends ParseElement
{
    public Expression expression1;
    public Expression expression2;

    public ArrayDereference(Expression expression1, Expression expression2)
    {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression1.decompile(indentation, out);
        out.append("[");
        expression2.decompile(indentation, out);
        out.append("]");
    }

    public static final int TYPE = 0x81454541;
    public int getElementType()
    {
        return TYPE;
    }
}
