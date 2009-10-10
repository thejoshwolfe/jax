package net.wolfesoftware.jax.ast;

import java.util.List;

public class Arguments extends ListElement<Expression>
{
    public Arguments(List<Expression> elements)
    {
        super(elements);
    }

    @Override
    protected String getDelimiter()
    {
        return ", ";
    }

    public static final int TYPE = 0x11cb03b7;
    public int getElementType()
    {
        return TYPE;
    }

}
