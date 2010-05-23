package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Type;

public class IfThenElse extends ParseElement
{
    public Type returnType;

    public Expression conditionExpression;
    public Expression thenBodyExpression;
    public Expression elseBodyExpression;

    public IfThenElse(Expression expression1, Expression expression2, Expression expression3)
    {
        this.conditionExpression = expression1;
        this.thenBodyExpression = expression2;
        this.elseBodyExpression = expression3;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("if (");
        conditionExpression.decompile(indentation, out);
        out.append(") ");
        thenBodyExpression.decompile(indentation, out);
        out.append(" else ");
        elseBodyExpression.decompile(indentation, out);
    }

    public static final int TYPE = 0x141903c8;
    public int getElementType()
    {
        return TYPE;
    }
}
