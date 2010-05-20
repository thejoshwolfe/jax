package net.wolfesoftware.jax.ast;

public class DoWhileLoop extends ParseElement
{
    public Expression expression1;
    public Expression expression2;
    public DoWhileLoop(Expression expression1, Expression expression2)
    {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("do ");
        expression1.decompile(indentation, out);
        out.append(" while (");
        expression2.decompile(indentation, out);
        out.append(')');
    }

    public static final int TYPE = 0x18c70447;
    public int getElementType()
    {
        return TYPE;
    }
}
