package net.wolfesoftware.jax.ast;

import java.util.List;

public class QualifiedName extends ListElement<Id>
{
    public QualifiedName(List<Id> elements)
    {
        super(elements);
    }

    @Override
    protected String getDelimiter()
    {
        return ".";
    }

    public static final int TYPE = 0x22e0050b;
    public int getElementType()
    {
        return TYPE;
    }

}
