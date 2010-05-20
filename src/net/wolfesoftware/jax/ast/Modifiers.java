package net.wolfesoftware.jax.ast;

import java.util.List;

public class Modifiers extends ListElement<Modifier>
{
    public short bitmask = 0;

    public Modifiers(List<Modifier> elements)
    {
        super(elements);
    }

    @Override
    protected String getDelimiter()
    {
        return " ";
    }

    public static final int TYPE = 0x28b60599;
    public int getElementType()
    {
        return TYPE;
    }
}
