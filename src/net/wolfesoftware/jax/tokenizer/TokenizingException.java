package net.wolfesoftware.jax.tokenizer;

import net.wolfesoftware.jax.CompileError;

public class TokenizingException extends CompileError
{
    public TokenizingException(int start, String text, String message)
    {
        super(message + " offset: " + start + " text: " + text);
    }
}
