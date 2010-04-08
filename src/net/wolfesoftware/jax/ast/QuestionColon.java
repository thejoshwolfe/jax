package net.wolfesoftware.jax.ast;

public class QuestionColon extends ParseElement
{
    public String label1;
    public String label2;
    
    public Expression expression1;
    public Expression expression2;
    public Expression expression3;

    public QuestionColon(Expression expression1, Expression expression2, Expression expression3)
    {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.expression3 = expression3;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression1.decompile(indentation, out);
        out.append(" ? ");
        expression2.decompile(indentation, out);
        out.append(" : ");
        expression3.decompile(indentation, out);
    }

    public static final int TYPE = 0x250f0554;
    public int getElementType()
    {
        return TYPE;
    }
}
