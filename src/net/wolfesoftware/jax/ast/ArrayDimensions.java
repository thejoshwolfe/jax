package net.wolfesoftware.jax.ast;

import java.util.*;

public class ArrayDimensions extends ListElement<ArrayDimension>
{
    public ArrayDimensions(List<ArrayDimension> elements)
    {
        super(elements);
    }

    @Override
    protected String getDelimiter()
    {
        return "";
    }

    public static final int TYPE = 0x2f3a0619;
    public int getElementType()
    {
        return TYPE;
    }

    public static final ArrayDimensions EMPTY = new ArrayDimensions(new LinkedList<ArrayDimension>());
}
