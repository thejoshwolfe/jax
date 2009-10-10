package net.wolfesoftware.jax.ast;

public abstract class BinaryOperatorElement extends ParseElement
{
    public Expression expression1;
    public Expression expression2;

    public BinaryOperatorElement(Expression expression1, Expression expression2)
    {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    protected final void decompile(String indentation, StringBuilder out)
    {
        expression1.decompile(indentation, out);
        out.append(' ').append(getOperator()).append(' ');
        expression2.decompile(indentation, out);
    }
    protected abstract String getOperator();
}
