package net.wolfesoftware.jax.ast;

public class DoWhileLoop extends LoopElement
{
    public Expression bodyExpression;
    public Expression conditionExpression;
    public DoWhileLoop(Expression expression1, Expression expression2)
    {
        this.bodyExpression = expression1;
        this.conditionExpression = expression2;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("do ");
        bodyExpression.decompile(indentation, out);
        out.append(" while (");
        conditionExpression.decompile(indentation, out);
        out.append(')');
    }

    public static final int TYPE = 0x18c70447;
    public int getElementType()
    {
        return TYPE;
    }
}
