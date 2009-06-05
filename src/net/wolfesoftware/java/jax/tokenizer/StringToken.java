package net.wolfesoftware.java.jax.tokenizer;

import net.wolfesoftware.java.jax.ast.*;

public class StringToken extends LiteralToken
{
    public final String value;
    public StringToken(int start, String text, String value)
    {
        super(start, text);
        this.value = value;
    }

    @Override
    public LiteralElement makeElement()
    {
        return new StringLiteral(value, text);
    }

    public static final int TYPE = 0x1ab30479;
    public int getType()
    {
        return TYPE;
    }
}
