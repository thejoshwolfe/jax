package net.wolfesoftware.java.jax.parser.elements;

import java.util.List;

public class Program extends ListElement<Declaration>
{
    public Program(List<Declaration> declarations)
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
