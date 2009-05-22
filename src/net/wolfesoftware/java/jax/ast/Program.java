package net.wolfesoftware.java.jax.ast;

import java.util.List;

public class Program extends ListElement<TopLevelItem>
{
    public Program(List<TopLevelItem> declarations)
    {
        super(declarations);
    }

    @Override
    protected String getDelimiter()
    {
        return ";\n";
    }
    
    public static final int TYPE = 0x0B2F02D9;
    public int getElementType()
    {
        return TYPE;
    }
}
