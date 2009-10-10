package net.wolfesoftware.jax.parser;

import net.wolfesoftware.jax.CompileError;
import net.wolfesoftware.jax.tokenizer.*;
import net.wolfesoftware.jax.tokenizer.LineColumnLookup.LineAndColumn;

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
