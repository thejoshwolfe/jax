package net.wolfesoftware.jax.tokenization;

import net.wolfesoftware.jax.ast.*;

public class DoubleToken extends LiteralToken
{
    public final double value;

    public DoubleToken(int start, String text, double value)
    {
        super(start, text);
        this.value = value;
    }

    @Override
    public LiteralElement makeElement()
    {
        return new DoubleLiteral(value);
    }

    public static final int TYPE = 0x19a5045d;
    public int getType()
    {
        return TYPE;
    }
}
