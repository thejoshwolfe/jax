package net.wolfesoftware.java.jax.ast;

import java.util.List;

public class ArgumentDeclarations extends ListElement<VariableDeclaration>
{
    public ArgumentDeclarations(List<VariableDeclaration> elements)
    {
        super(elements);
    }

    @Override
    protected String getDelimiter()
    {
        return ", ";
    }

    public static final int TYPE = 0x5365081d;
    public int getElementType()
    {
        return TYPE;
    }
}
