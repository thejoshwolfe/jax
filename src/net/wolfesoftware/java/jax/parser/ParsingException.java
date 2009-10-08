package net.wolfesoftware.java.jax.parser;

import net.wolfesoftware.java.jax.CompileError;
import net.wolfesoftware.java.jax.tokenizer.*;
import net.wolfesoftware.java.jax.tokenizer.LineColumnLookup.LineAndColumn;

public class ParsingException extends CompileError
{
    public ParsingException(Token token, LineColumnLookup lineColumnLookup)
    {
        super(getMessage(token, lineColumnLookup));
    }
    private static String getMessage(Token token, LineColumnLookup lineColumnLookup)
    {
        LineAndColumn lineAndColumn = new LineAndColumn();
        lineColumnLookup.getLineAndColumn(token.start, lineAndColumn);
        return "Parse error on \"" + token.text + "\". line: " + (lineAndColumn.line+1) + ". column: " + (lineAndColumn.column+1);
    }
}
