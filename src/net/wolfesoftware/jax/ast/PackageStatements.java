package net.wolfesoftware.jax.ast;

import java.util.List;

public class PackageStatements extends ListElement<PackageStatement>
{
    public PackageStatements(List<PackageStatement> elements)
    {
        super(elements);
    }

    @Override
    protected String getDelimiter()
    {
        return "\n";
    }

    public static final int TYPE = 0x34c70670;
    public int getElementType()
    {
        return TYPE;
    }
}
