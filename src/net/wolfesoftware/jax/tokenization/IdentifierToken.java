package net.wolfesoftware.jax.tokenization;

public class IdentifierToken extends Token
{
    public IdentifierToken(int start, String text)
    {
        super(start, text);
    }
    public static final int TYPE = 0x2f530605;
    public int getType()
    {
        return TYPE;
    }
}
