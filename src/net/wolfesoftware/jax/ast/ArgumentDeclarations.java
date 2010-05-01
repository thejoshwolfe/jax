package net.wolfesoftware.jax.ast;

import java.util.*;

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

    public static final ArgumentDeclarations EMPTY = new ArgumentDeclarations(new LinkedList<VariableDeclaration>());
}
