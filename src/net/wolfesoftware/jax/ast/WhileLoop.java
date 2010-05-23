package net.wolfesoftware.jax.ast;

public class WhileLoop extends ParseElement
{
    public Expression conditionExpression;
    public Expression bodyExpression;
    public WhileLoop(Expression expression1, Expression expression2)
    {
        this.conditionExpression = expression1;
        this.bodyExpression = expression2;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("while (");
        conditionExpression.decompile(indentation, out);
        out.append(") ");
        bodyExpression.decompile(indentation, out);
    }

    public static final int TYPE = 0x11830394;
    public int getElementType()
    {
        return TYPE;
    }
}
