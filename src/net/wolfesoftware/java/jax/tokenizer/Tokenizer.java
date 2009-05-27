package net.wolfesoftware.java.jax.tokenizer;

import java.util.ArrayList;
import java.util.regex.*;
import net.wolfesoftware.java.jax.ast.Lang;

public final class Tokenizer
{

    public static Tokenization tokenize(String source)
    {
        return new Tokenizer(source).tokenize();
    }

    private final String source;
    private int start;
    private ArrayList<TokenizingException> errors = new ArrayList<TokenizingException>();

    private Tokenizer(String source)
    {
        this.source = source;
    }

    private static final String lineCommentCode = "\\/\\/.*?\\n";
    private static final String multiLineCommentCode = "\\/\\*.*?\\*\\/";
    private static final String skipCode = "\\s+|" + multiLineCommentCode  + "|" + lineCommentCode;
    private static final String identifierCode = "[A-Za-z_][A-Za-z0-9_]*";
    private static final String literalCode = "\\-?\\d+";
    private static final Pattern tokenPattern;
    static {
        StringBuilder tokenCode = new StringBuilder(128);
        tokenCode.append("(").append(skipCode).append(")|(");
        tokenCode.append(identifierCode).append(")|(");
        tokenCode.append(literalCode).append(")|(");
        for (String keyword : Lang.ALL)
            tokenCode.append(Pattern.quote(keyword)).append('|');
        tokenCode.setCharAt(tokenCode.length() - 1, ')');
        tokenPattern = Pattern.compile(tokenCode.toString(), Pattern.DOTALL);
    }

    private Tokenization tokenize()
    {
        ArrayList<Token> tokens = new ArrayList<Token>();
        Matcher tokenMatcher = tokenPattern.matcher(source);
        while (tokenMatcher.lookingAt())
        {
            start = tokenMatcher.start();
            String tokenText = tokenMatcher.group(0);
            Token token = createToken(tokenText);
            if (token != null)
                tokens.add(token);
            tokenMatcher.region(tokenMatcher.end(), source.length());
        }
        if (!tokenMatcher.hitEnd())
            errors.add(TokenizingException.newInstance(tokenMatcher.end(), source.substring(tokenMatcher.end()), "Invalid Token"));
//        String lastBit = source.substring(prevEnd);
//        if (!skipPattern.matcher(lastBit).matches())
//            errors.add(TokenizingException.newInstance(prevEnd, lastBit, "Invalid Token"));

        return new Tokenization(source, tokens, errors);
    }

    private Token createToken(String text)
    {
        char c = text.charAt(0);

        if (Lang.ALL.contains(text))
            return new KeywordToken(start, text);

        if (Character.isLetter(c))
            return new IdentifierToken(start, text);
        if (Character.isDigit(c))
        {
            try {
                return new IntToken(start, text, Integer.parseInt(text));
            } catch (NumberFormatException e) {
                errors.add(TokenizingException.newInstance(start, text, "Format Error"));
                return null;
            }
        }
        if (Character.isWhitespace(c) || text.startsWith("//") || text.startsWith("/*"))
            return null;
        errors.add(TokenizingException.newInstance(start, text, "Unknown Token"));
        return null;
    }
}
