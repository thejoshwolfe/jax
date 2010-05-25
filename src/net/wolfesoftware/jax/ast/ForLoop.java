package net.wolfesoftware.jax.ast;

public class ForLoop extends LoopElement
{
    public Expression initExpression;
    public Expression conditionExpression;
    public Expression incrementExpression;
    public Expression bodyExpression;
    public ForLoop(Expression initExpression, Expression conditionExpression, Expression incrementExpression, Expression bodyExpression)
    {
        this.initExpression = initExpression;
        this.conditionExpression = conditionExpression;
        this.incrementExpression = incrementExpression;
        this.bodyExpression = bodyExpression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("for (");
        initExpression.decompile(indentation, out);
        out.append("; ");
        conditionExpression.decompile(indentation, out);
        out.append("; ");
        incrementExpression.decompile(indentation, out);
        out.append(") ");
        bodyExpression.decompile(indentation, out);
    }

    public static final int TYPE = 0x0a9002c2;
    public int getElementType()
    {
        return TYPE;
    }
}
