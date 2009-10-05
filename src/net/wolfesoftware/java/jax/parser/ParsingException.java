package net.wolfesoftware.java.jax.parser;

import net.wolfesoftware.java.jax.CompileError;
import net.wolfesoftware.java.jax.tokenizer.*;

public class ParsingException extends CompileError
{
    public ParsingException(Token token, LineColumnLookup lineColumnLookup)
    {
        super("Parse error on \"" + token.text + "\". line: " + lineColumnLookup.getLine(token.start) + ". column: " + lineColumnLookup.getColumn(token.start));
    }
}
