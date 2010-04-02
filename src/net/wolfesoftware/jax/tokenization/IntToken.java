package net.wolfesoftware.jax.tokenization;

import net.wolfesoftware.jax.ast.*;

public class IntToken extends LiteralToken
{
    public final int value;

    public IntToken(int start, String text, int value)
    {
        super(start, text);
        this.value = value;
    }

    @Override
    public LiteralElement makeElement()
    {
        return new IntLiteral(value);
    }

    public static final int TYPE = 0x0de3032d;
    public int getType()
    {
        return TYPE;
    }
}
