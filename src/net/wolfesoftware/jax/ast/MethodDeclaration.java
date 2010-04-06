package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Method;

public class MethodDeclaration extends ConstructorOrMethodDeclaration
{
    public Id id;
    public Method method;

    public MethodDeclaration(MethodModifiers methodModifiers, TypeId typeId, Id id, ArgumentDeclarations argumentDeclarations, Expression expression)
    {
        super(methodModifiers, typeId, argumentDeclarations, expression);
        this.id = id;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        methodModifiers.decompile(indentation, out);
        if (!methodModifiers.elements.isEmpty())
            out.append(' ');
        typeId.decompile(indentation, out);
        out.append(' ');
        id.decompile(indentation, out);
        out.append('(');
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
