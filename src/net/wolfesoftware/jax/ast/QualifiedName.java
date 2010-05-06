package net.wolfesoftware.jax.ast;

import java.util.List;
import net.wolfesoftware.jax.util.Util;

public class QualifiedName extends ParseElement
{
    public String qualifiedName;
    public QualifiedName(List<String> parts)
    {
        qualifiedName = Util.join(Util.withoutNulls(parts), ".");
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(qualifiedName);
    }

    public static final int TYPE = 0x22e0050b;
    public int getElementType()
    {
        return TYPE;
    }
}
