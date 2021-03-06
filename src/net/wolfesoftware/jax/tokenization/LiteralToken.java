package net.wolfesoftware.jax.tokenization;

import net.wolfesoftware.jax.ast.LiteralElement;

public abstract class LiteralToken extends Token
{
    protected LiteralToken(int start, String text)
    {
        super(start, text);
    }

    public abstract LiteralElement makeElement();
}
