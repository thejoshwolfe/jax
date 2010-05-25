package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public class MethodDeclaration extends ConstructorOrMethodDeclaration
{
    public Method method;
    public BranchDestination returnDestination = new BranchDestination();

    public String methodName;
    public MethodDeclaration(Modifiers methodModifiers, TypeId typeId, String methodName, ArgumentDeclarations argumentDeclarations, MaybeThrows maybeThrows, Expression expression)
    {
        super(methodModifiers, typeId, argumentDeclarations, maybeThrows, expression);
        this.methodName = methodName;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        methodModifiers.decompile(indentation, out);
        if (!methodModifiers.elements.isEmpty())
            out.append(' ');
        typeId.decompile(indentation, out);
        out.append(' ').append(methodName).append('(');
        argumentDeclarations.decompile(indentation, out);
        out.append(") ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x44470750;
    public int getElementType()
    {
        return TYPE;
    }
}
