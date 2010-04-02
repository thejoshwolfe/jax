package net.wolfesoftware.jax.tokenization;

import java.util.ArrayList;

public class Tokenization
{
    public final String source;
    public final LineColumnLookup lineColumnLookup;
    public final ArrayList<Token> tokens;
    public final ArrayList<TokenizingError> errors;

    public Tokenization(String source, ArrayList<Token> tokens, ArrayList<TokenizingError> errors)
    {
        this.source = source;
        this.lineColumnLookup = new LineColumnLookup(source);
        this.tokens = tokens;
        this.errors = errors;
    }

    public String toString()
    {
        return "souce.length: " + source.length() + "\n" + 
               "tokens.size(): " + tokens.size() + "\n" +
               "errors.size(): " + errors.size() + "\n";
    }
}
