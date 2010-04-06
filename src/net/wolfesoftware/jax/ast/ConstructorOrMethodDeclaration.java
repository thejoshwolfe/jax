package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.codegen.MethodInfo;
import net.wolfesoftware.jax.semalysis.*;

public abstract class ConstructorOrMethodDeclaration extends ParseElement
{
    public ReturnBehavior returnBehavior;
    public RootLocalContext context;

    public MethodModifiers methodModifiers;
    public TypeId typeId;
    public ArgumentDeclarations argumentDeclarations;
    public Expression expression;

    protected ConstructorOrMethodDeclaration(MethodModifiers methodModifiers, TypeId typeId, ArgumentDeclarations argumentDeclarations, Expression expression)
    {
        this.methodModifiers = methodModifiers;
        this.typeId = typeId;
        this.argumentDeclarations = argumentDeclarations;
        this.expression = expression;
    }

    public boolean isStatic()
    {
        return (methodModifiers.bitmask & MethodInfo.ACC_STATIC) != 0;
    }
}
