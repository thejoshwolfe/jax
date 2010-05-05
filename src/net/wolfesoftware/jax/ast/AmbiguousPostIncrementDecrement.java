package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public class AmbiguousPostIncrementDecrement extends AmbiguousIncrementDecrement
{
    public AmbiguousPostIncrementDecrement(Expression expression, String operator)
    {
        super(expression, operator);
    }
    @Override
    public LocalVariableIncrementDecrement makeLocalVariableDisambiguation(LocalVariable variable)
    {
        return new LocalVariablePostIncrementDecrement(variable, operator);
    }
    @Override
    public InstanceFieldIncrementDecrement makeInstanceFieldDisambiguation(Expression leftExpression, Field field)
    {
        return new InstanceFieldPostIncrementDecrement(leftExpression, field, operator);
    }
    @Override
    public StaticFieldIncrementDecrement makeStaticFieldDisambiguation(Field field)
    {
        return new StaticFieldPostIncrementDecrement(field, operator);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression.decompile(indentation, out);
        out.append(operator);
    }

    public static final int TYPE = 0xc7440c8f;
    public int getElementType()
    {
        return TYPE;
    }
}
