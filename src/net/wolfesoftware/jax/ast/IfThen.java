package net.wolfesoftware.jax.ast;

public class IfThen extends ParseElement
{
    public String label;

    public Expression expression1;
    public Expression expression2;

    public IfThen(Expression expression1, Expression expression2)
    {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("if (");
        expression1.decompile(indentation, out);
        out.append(") ");
        expression2.decompile(indentation, out);
    }

    public static final int TYPE = 0x077a023f;
    public int getElementType()
    {
        return TYPE;
    }
}
