package net.wolfesoftware.jax.ast;

import java.util.List;

public class FieldModifiers extends ListElement<FieldModifier>
{
    public short bitmask = 0;

    public FieldModifiers(List<FieldModifier> elements)
    {
        super(elements);
    }

    @Override
    protected String getDelimiter()
    {
        return " ";
    }

    public static final int TYPE = 0x28060587;
    public int getElementType()
    {
        return TYPE;
    }
}
