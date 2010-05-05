package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public abstract class AmbiguousIncrementDecrement extends ParseElement
{
    public Expression expression;
    public String operator;
    protected AmbiguousIncrementDecrement(Expression expression, String operator)
    {
        this.expression = expression;
        this.operator = operator;
    }
    public abstract LocalVariableIncrementDecrement makeLocalVariableDisambiguation(LocalVariable variable);
    public abstract InstanceFieldIncrementDecrement makeInstanceFieldDisambiguation(Expression leftExpression, Field field);
    public abstract StaticFieldIncrementDecrement makeStaticFieldDisambiguation(Field field);
}
