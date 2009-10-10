package net.wolfesoftware.jax.tokenizer;

import net.wolfesoftware.jax.ast.*;

public class BooleanToken extends LiteralToken
{
    public final boolean value;

    public BooleanToken(int start, String text, boolean value)
    {
        super(start, text);
        this.value = value;
    }

    public LiteralElement makeElement()
    {
        return value ? BooleanLiteral.TRUE : BooleanLiteral.FALSE;
    }

    public static final int TYPE = 0x1e4704c2;
    public int getType()
    {
        return TYPE;
    }

}
