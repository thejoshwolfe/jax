package net.wolfesoftware.jax.parsing;

import net.wolfesoftware.jax.CompileError;
import net.wolfesoftware.jax.tokenization.*;
import net.wolfesoftware.jax.tokenization.LineColumnLookup.LineAndColumn;

public class ParsingError extends CompileError
{
    public ParsingError(Token token, LineColumnLookup lineColumnLookup)
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
