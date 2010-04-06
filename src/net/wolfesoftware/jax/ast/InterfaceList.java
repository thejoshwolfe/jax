package net.wolfesoftware.jax.ast;

import java.util.List;

public class InterfaceList extends ListElement<TypeId>
{
    public InterfaceList(List<TypeId> elements)
    {
        super(elements);
    }

    @Override
    protected String getDelimiter()
    {
        return ", ";
    }

    public static final int TYPE = 0x23c1052e;
    public int getElementType()
    {
        return TYPE;
    }
}
