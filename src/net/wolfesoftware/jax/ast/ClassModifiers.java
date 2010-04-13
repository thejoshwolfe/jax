package net.wolfesoftware.jax.ast;

import java.util.List;

public class ClassModifiers extends ListElement<ClassModifier>
{
    public short bitmask = 0;

    public ClassModifiers(List<ClassModifier> elements)
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
