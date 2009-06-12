package net.wolfesoftware.java.jax.ast;

import java.util.List;

public class Imports extends ListElement<ImportStatement>
{
    public Imports(List<ImportStatement> elements)
    {
        super(elements);
    }

    @Override
    protected String getDelimiter()
    {
        return "\n";
    }

    public static final int TYPE = 0x0b3102ef;
    public int getElementType()
    {
        return TYPE;
    }

}
