package net.wolfesoftware.jax.tokenizer;

public abstract class Token
{
    public final int start;
    public final String text;
    
    protected Token(int start, String text)
    {
        this.start = start;
        this.text = text;
    }
    public abstract int getType();
    
    public String toString()
    {
        return text;
    }
    
    public String toStringVerbose()
    {
        return start + ":" + text;
    }
}
