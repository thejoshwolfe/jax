package net.wolfesoftware.java.jax.parser.elements;

public abstract class ParseElement
{
    public abstract int getElementType();
    public final String decompile()
    {
        StringBuffer out = new StringBuffer();
        decompile("", out);
        return out.toString();
    }
    protected abstract void decompile(String indentation, StringBuffer out);
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
