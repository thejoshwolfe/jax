package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public class AmbiguousPreIncrementDecrement extends AmbiguousIncrementDecrement
{
    public AmbiguousPreIncrementDecrement(String operator, Expression expression)
    {
        super(expression, operator);
    }
    @Override
    public LocalVariableIncrementDecrement makeLocalVariableDisambiguation(LocalVariable variable)
    {
        return new LocalVariablePreIncrementDecrement(variable, operator);
    }
    @Override
    public InstanceFieldIncrementDecrement makeInstanceFieldDisambiguation(Expression leftExpression, Field field)
    {
        return new InstanceFieldPreIncrementDecrement(leftExpression, field, operator);
    }
    @Override
    public StaticFieldIncrementDecrement makeStaticFieldDisambiguation(Field field)
    {
        return new StaticFieldPreIncrementDecrement(field, operator);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(operator);
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0xb8fb0c10;
    public int getElementType()
    {
        return TYPE;
    }
}
