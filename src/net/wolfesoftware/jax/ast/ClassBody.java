package net.wolfesoftware.jax.ast;

import java.util.*;

public class ClassBody extends ListElement<ClassMember>
{
    public ClassBody(List<ClassMember> elements)
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
        return ";\n";
    }

    public static final int TYPE = 0x10f20385;
    public int getElementType()
    {
        return TYPE;
    }
}
