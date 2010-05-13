package net.wolfesoftware.jax.ast;

public class ReturnExpression extends BranchStatement
{
    public Expression expression;

    public ReturnExpression(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("return ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x37ee06b1;
    public int getElementType()
    {
        return TYPE;
    }
}
