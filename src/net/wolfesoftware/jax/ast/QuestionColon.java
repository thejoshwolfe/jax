package net.wolfesoftware.jax.ast;

public class QuestionColon extends IfThenElse
{
    public QuestionColon(Expression expression1, Expression expression2, Expression expression3)
    {
        super(expression1, expression2, expression3);
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
