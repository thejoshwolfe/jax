package net.wolfesoftware.java.jax.parser;

import net.wolfesoftware.java.jax.tokenizer.Token;

public class ParsingException extends Exception
{
    public final Token token;

    public ParsingException(Token token)
    {
        this.token = token;
    }
    public String getMessage()
    {
        return "Parse error on \"" + token.text + "\"";
    }
}
