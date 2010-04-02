package net.wolfesoftware.jax.tokenization;

import net.wolfesoftware.jax.ast.*;

public class LongToken extends LiteralToken
{
    public final long value;

    public LongToken(int start, String text, long value)
    {
        super(start, text);
        this.value = value;
    }

    @Override
    public LiteralElement makeElement()
    {
        return new LongLiteral(value);
    }

    public static final int TYPE = 0x0de3032d;
    public int getType()
    {
        return TYPE;
    }
}
