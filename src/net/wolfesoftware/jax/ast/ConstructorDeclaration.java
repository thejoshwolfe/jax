package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Constructor;

public class ConstructorDeclaration extends ConstructorOrMethodDeclaration
{
    public Constructor constructor;

    public ConstructorDeclaration(Modifiers methodModifiers, TypeId typeId, ArgumentDeclarations argumentDeclarations, MaybeThrows maybeThrows, Expression expression)
    {
        super(methodModifiers, typeId, argumentDeclarations, maybeThrows, expression);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        methodModifiers.decompile(indentation, out);
        if (!methodModifiers.elements.isEmpty())
            out.append(' ');
        typeId.decompile(indentation, out);
        out.append('(');
        argumentDeclarations.decompile(indentation, out);
        out.append(") ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x5ed308b0;
    public int getElementType()
    {
        return TYPE;
    }
}
