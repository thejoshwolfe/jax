package net.wolfesoftware.java.jax.tokenizer;

import net.wolfesoftware.java.jax.ast.LiteralElement;

public abstract class LiteralToken extends Token
{
    protected LiteralToken(int start, String text)
    {
        super(start, text);
    }

    public abstract LiteralElement makeElement();
}
