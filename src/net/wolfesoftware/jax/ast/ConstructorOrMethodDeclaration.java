package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public abstract class ConstructorOrMethodDeclaration extends ParseElement
{
    public ReturnBehavior returnBehavior;
    public RootLocalContext context;

    public TypeId typeId;
    public ArgumentDeclarations argumentDeclarations;
    public Expression expression;

    protected ConstructorOrMethodDeclaration(TypeId typeId, ArgumentDeclarations argumentDeclarations, Expression expression)
    {
        this.typeId = typeId;
        this.argumentDeclarations = argumentDeclarations;
        this.expression = expression;
    }

    public abstract boolean isStatic();
}
