package net.wolfesoftware.java.jax.tokenizer;

public class TokenizingException extends Exception
{
    private final int start;
    private final String text;

    public TokenizingException(int start, String text, String message)
    {
        super(message);
        this.start = start;
        this.text = text;
    }
    public int getStart()
    {
        return start;
    }
    public String getText()
    {
        return text;
    }

    public static TokenizingException newInstance(int start, String text, String message)
    {
        return new TokenizingException(start, text, message);
    }

    public String toString()
    {
        return text;
    }
    public String toStringVerbose()
    {
        return start + ":" + text;
    }
}
