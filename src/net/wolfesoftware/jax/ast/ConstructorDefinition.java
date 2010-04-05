package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Constructor;

public class ConstructorDefinition extends ConstructorOrMethodElement
{
    public Constructor constructor;

    public ConstructorDefinition(TypeId typeId, ArgumentDeclarations argumentDeclarations, Expression expression)
    {
        super(typeId, argumentDeclarations, expression);
    }

    @Override
    public boolean isStatic()
    {
        // all constructors are non-static
        return false;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
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
