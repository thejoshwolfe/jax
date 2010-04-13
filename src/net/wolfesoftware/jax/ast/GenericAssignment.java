package net.wolfesoftware.jax.ast;

public abstract class GenericAssignment extends ParseElement
{
    public Id id;
    public String operator;
    public Expression rightExpression;
    public GenericAssignment(Id id, String operator, Expression rightExpression)
    {
        this.id = id;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }
}
