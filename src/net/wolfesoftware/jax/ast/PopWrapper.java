package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.RuntimeType;

public class PopWrapper extends ParseElement
{
    public Expression expression;
    private PopWrapper(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x15cf0411;
    public int getElementType()
    {
        return TYPE;
    }

    public static Expression wrap(Expression innerExpression)
    {
        Expression wrapperExpression = new Expression(new PopWrapper(innerExpression));
        wrapperExpression.returnType = RuntimeType.VOID;
        return wrapperExpression;
    }
}
