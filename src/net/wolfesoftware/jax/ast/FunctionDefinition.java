package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.lexiconizer.*;

public class FunctionDefinition extends ParseElement
{
    public ReturnBehavior returnBehavior;
    public RootLocalContext context;

    public TypeId typeId;
    public Id id;
    public ArgumentDeclarations argumentDeclarations;
    public Expression expression;
    public Method method;

    public FunctionDefinition(TypeId typeId, Id id, ArgumentDeclarations argumentDeclarations, Expression expression)
    {
        this.typeId = typeId;
        this.id = id;
        this.argumentDeclarations = argumentDeclarations;
        this.expression = expression;
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