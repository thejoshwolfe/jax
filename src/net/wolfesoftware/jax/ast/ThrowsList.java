package net.wolfesoftware.jax.ast;

import java.util.List;

public class ThrowsList extends ListElement<TypeId>
{
    public ThrowsList(List<TypeId> elements)
    {
        super(elements);
    }

    @Override
    protected String getDelimiter()
    {
        return ", ";
    }

    public static final int TYPE = 0x16610424;
    public int getElementType()
    {
        return TYPE;
    }
}
