package net.wolfesoftware.jax.tokenization;

import net.wolfesoftware.jax.ast.*;

public class FloatToken extends LiteralToken
{
    public final float value;

    public FloatToken(int start, String text, float value)
    {
        super(start, text);
        this.value = value;
    }

    @Override
    public LiteralElement makeElement()
    {
        return new FloatLiteral(value);
    }

    public static final int TYPE = 0x154203f8;
    public int getType()
    {
        return TYPE;
    }
}
