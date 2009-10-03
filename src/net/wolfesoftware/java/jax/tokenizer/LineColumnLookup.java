package net.wolfesoftware.java.jax.tokenizer;

/**
 * TODO: implement this class
 */
public class LineColumnLookup
{
    private final String source;

    public LineColumnLookup(String source)
    {
        this.source = source;
    }

    public int getLine(int offset)
    {
        return -1;
    }

    public int getColumn(int offset)
    {
        return -1;
    }
}
