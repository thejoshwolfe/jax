package net.wolfesoftware.java.jax.tokenizer;

public class KeywordToken extends Token
{
    public KeywordToken(int start, String text)
    {
        super(start, text.intern());
    }

    public static final int TYPE = 0x1f9704e7;
    public int getType()
    {
        return TYPE;
    }
}
