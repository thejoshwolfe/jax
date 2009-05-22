package net.wolfesoftware.java.jax.ast;

import java.util.List;

public class BlockContents extends ListElement<Expression>
{
    public BlockContents(List<Expression> elements)
    {
        super(elements);
    }
    @Override
    protected String getDelimiter()
    {
        return ";\n";
    }

    @Override
    protected String getPrimer()
    {
        return "\n";
    }
    
    public static final int TYPE = 0x230f053a;
    public int getElementType()
    {
        return TYPE;
    }
}
