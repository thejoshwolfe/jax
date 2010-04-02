package net.wolfesoftware.jax.tokenization;

import net.wolfesoftware.jax.CompileError;

public class TokenizingError extends CompileError
{
    public TokenizingError(int start, String text, String message)
    {
        super(message + " offset: " + start + " text: " + text);
    }
}
