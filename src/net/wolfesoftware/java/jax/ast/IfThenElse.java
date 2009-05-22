package net.wolfesoftware.java.jax.ast;

public class IfThenElse extends ParseElement
{
    public Expression expression1;
    public Expression expression2;
    public Expression expression3;

    public IfThenElse(Expression expression1, Expression expression2, Expression expression3)
    {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.expression3 = expression3;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("if (");
        expression1.decompile(indentation, out);
        out.append(") ");
        expression2.decompile(indentation, out);
        out.append(" else ");
        expression3.decompile(indentation, out);
    }

    public static final int TYPE = 0x141903c8;
    public int getElementType()
    {
        return TYPE;
    }
}
