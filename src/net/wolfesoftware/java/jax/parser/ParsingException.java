package net.wolfesoftware.java.jax.parser;

import net.wolfesoftware.java.jax.tokenizer.*;

public class ParsingException extends Exception
{
    private final Token token;
    private final LineColumnLookup lineColumnLookup;

    public ParsingException(Token token, LineColumnLookup lineColumnLookup)
    {
        this.token = token;
        this.lineColumnLookup = lineColumnLookup;
    }
    public String getMessage()
    {
        return "Parse error on \"" + token.text + "\". line: " + lineColumnLookup.getLine(token.start) + ". column: " + lineColumnLookup.getColumn(token.start);
    }
}
