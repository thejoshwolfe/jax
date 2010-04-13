package net.wolfesoftware.jax.ast;

import java.util.List;

public class MethodModifiers extends ListElement<MethodModifier>
{
    public short bitmask = 0;

    public MethodModifiers(List<MethodModifier> elements)
    {
        super(elements);
    }

    @Override
    protected String getDelimiter()
    {
        return " ";
    }

    public static final int TYPE = 0x2f100604;
    public int getElementType()
    {
        return TYPE;
    }
}
