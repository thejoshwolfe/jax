package net.wolfesoftware.java.jax.tokenizer;

import java.util.ArrayList;

public class Tokenization
{
    public final String source;
    public final ArrayList<Token> tokens;
    public final ArrayList<TokenizingException> errors;

    public Tokenization(String source, ArrayList<Token> tokens, ArrayList<TokenizingException> errors)
    {
        this.source = source;
        this.tokens = tokens;
        this.errors = errors;
    }

    public String toString()
    {
        return
        "souce.length: " + source.length() + "\n" + 
        "tokens: " + tokens.size() + "\n" +
        "errors: " + errors.size() + "\n"
        ;
    }
    public String toStringVerbose()
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append("source.length: ").append(source.length()).append("\n");
        
        if (errors.size() != 0)
        {
            builder.append("\nerrors: ").append(errors.size()).append('\n');
            for (TokenizingException error : errors)
                builder.append(error.toStringVerbose()).append('\n');
        }
        if (tokens.size() != 0)
        {
            builder.append("\ntokens: ").append(tokens.size()).append('\n');
            for (Token token : tokens)
                builder.append(token.toStringVerbose()).append('\n');
        }
        return builder.toString();
    }
}
