package net.wolfesoftware.java.jax.ast;

import java.util.List;

public class CatchList extends ListElement<CatchBody>
{
    public CatchList(List<CatchBody> elements)
    {
        super(elements);
    }
    @Override
    protected String getPrimer()
    {
        return "\n";
    }
    @Override
    protected String getDelimiter()
    {
        return "catch ";
    }

    public static final int TYPE = 0x10b70380;
    public int getElementType()
    {
        return TYPE;
    }
}
