package net.wolfesoftware.jax.ast;

public class WhileLoop extends ParseElement
{
    public String continueToLabel;
    public String breakToLabel;

    public Expression expression1;
    public Expression expression2;
    public WhileLoop(Expression expression1, Expression expression2)
    {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("while (");
        expression1.decompile(indentation, out);
        out.append(") ");
        expression2.decompile(indentation, out);
    }

    public static final int TYPE = 0x11830394;
    public int getElementType()
    {
        return TYPE;
    }
}
