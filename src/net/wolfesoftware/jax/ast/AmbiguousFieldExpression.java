package net.wolfesoftware.jax.ast;

public class AmbiguousFieldExpression extends ParseElement
{
    public Expression leftExpression;
    public AmbiguousId fieldName;
    public AmbiguousFieldExpression(Expression expression, AmbiguousId fieldName)
    {
        this.leftExpression = expression;
        this.fieldName = fieldName;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        leftExpression.decompile(indentation, out);
        out.append('.');
        fieldName.decompile(indentation, out);
    }

    public static final int TYPE = 0x7f7b09e9;
    public int getElementType()
    {
        return TYPE;
    }
}
