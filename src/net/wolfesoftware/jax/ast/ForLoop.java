package net.wolfesoftware.jax.ast;

public class ForLoop extends ParseElement
{
    public String initialJumpToLabel;
    public String continueToLabel;
    public String breakToLabel;

    public Expression expression1;
    public Expression expression2;
    public Expression expression3;
    public Expression expression4;
    public ForLoop(Expression expression1, Expression expression2, Expression expression3, Expression expression4)
    {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.expression3 = expression3;
        this.expression4 = expression4;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("for (");
        expression1.decompile(indentation, out);
        out.append("; ");
        expression2.decompile(indentation, out);
        out.append("; ");
        expression3.decompile(indentation, out);
        out.append(") ");
        expression4.decompile(indentation, out);
    }

    public static final int TYPE = 0x0a9002c2;
    public int getElementType()
    {
        return TYPE;
    }
}
