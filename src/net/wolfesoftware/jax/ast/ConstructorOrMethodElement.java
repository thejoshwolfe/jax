package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysizer.*;

public abstract class ConstructorOrMethodElement extends ParseElement
{
    public ReturnBehavior returnBehavior;
    public RootLocalContext context;

    public TypeId typeId;
    public ArgumentDeclarations argumentDeclarations;
    public Expression expression;

    protected ConstructorOrMethodElement(TypeId typeId, ArgumentDeclarations argumentDeclarations, Expression expression)
    {
        this.typeId = typeId;
        this.argumentDeclarations = argumentDeclarations;
        this.expression = expression;
    }
}
