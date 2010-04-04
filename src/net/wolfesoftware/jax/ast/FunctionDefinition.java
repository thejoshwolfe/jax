package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

/**
 * TODO: rename to MethodDefinition
 */
public class FunctionDefinition extends ConstructorOrMethodElement
{
    public Id id;
    public Method method;

    public FunctionDefinition(TypeId typeId, Id id, ArgumentDeclarations argumentDeclarations, Expression expression)
    {
        super(typeId, argumentDeclarations, expression);
        this.id = id;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
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
