package net.wolfesoftware.jax.ast;

public abstract class ParseElement
{
    public abstract int getElementType();
    public final String decompile()
    {
        StringBuilder out = new StringBuilder();
        decompile("", out);
        return out.toString();
    }
    protected abstract void decompile(String indentation, StringBuilder out);
    public final String toString()
    {
        return decompile();
    }
    private static final String INDENTATION_UNIT = "    ";
    protected static String increaseIndentation(String previousIndentation)
    {
        return previousIndentation + INDENTATION_UNIT;
    }
}
