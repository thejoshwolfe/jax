package net.wolfesoftware.jax.tokenization;

import java.util.ArrayList;
import java.util.regex.*;

public final class Tokenizer
{
    public static Tokenization tokenize(String source)
    {
        return new Tokenizer(source).tokenize();
    }

    private final String source;
    private int start;
    private ArrayList<TokenizingException> errors = new ArrayList<TokenizingException>();
    private final ArrayList<Token> tokens = new ArrayList<Token>();

    private Tokenizer(String source)
    {
        this.source = source;
    }

    private static final String lineCommentCode = "\\/\\/.*?\\n";
    private static final String multiLineCommentCode = "\\/\\*.*?\\*\\/";
    private static final String skipCode = "\\s+|" + multiLineCommentCode  + "|" + lineCommentCode;
    private static final String identifierCode = "[A-Za-z_][A-Za-z0-9_]*";
    private static final String literalCode = "\\d+(\\.\\d+)?[fFlL]?";
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
        Matcher tokenMatcher = tokenPattern.matcher(source);
        int end = 0;
        while (tokenMatcher.lookingAt()) {
            start = tokenMatcher.start();
            String tokenText = tokenMatcher.group();
            Token token = createToken(tokenText);
            if (token != null)
                tokens.add(token);
            end = tokenizeSpecial(tokenMatcher.end());

            tokenMatcher.region(end, source.length());
        }

        if (!tokenMatcher.hitEnd())
            errors.add(new TokenizingException(end, source.substring(end), "Invalid Token."));

        return new Tokenization(source, tokens, errors);
    }

    private Token createToken(String text)
    {
        if (Lang.ALL.contains(text)) {
            if (text.equals(Lang.KEYWORD_TRUE))
                return new BooleanToken(start, text, true);
            if (text.equals(Lang.KEYWORD_FALSE))
                return new BooleanToken(start, text, false);
            return new KeywordToken(start, text);
        }

        char c = text.charAt(0);
        if (Character.isLetter(c) || c == '$' || c == '_')
            return new IdentifierToken(start, text);
        if (Character.isDigit(c)) {
            try {
                if (text.contains(".")) {
                    if (text.endsWith("f") || text.endsWith("F"))
                        return new FloatToken(start, text, Float.parseFloat(text));
                    else
                        return new DoubleToken(start, text, Double.parseDouble(text));
                } else {
                    if (text.endsWith("l") || text.endsWith("L"))
                        return new LongToken(start, text, Long.parseLong(text.substring(0, text.length() - 1)));
                    else
                        return new IntToken(start, text, Integer.parseInt(text));
                }
            } catch (NumberFormatException e) {
                errors.add(new TokenizingException(start, text, "number format is wrong."));
                return null;
            }
        }
        if (Character.isWhitespace(c) || text.startsWith("//") || text.startsWith("/*"))
            return null;
        errors.add(new TokenizingException(start, text, "Unknown Token."));
        return null;
    }

    private int tokenizeSpecial(int index)
    {
        if (index < source.length() && source.charAt(index) == '"')
            return tokenizeStringLiteral(index);
        return index;
    }

    private int tokenizeStringLiteral(int start)
    {
        StringBuilder stringBuilder = new StringBuilder();
        boolean escape = false;
        for (int i = start + 1; i < source.length(); i++) {
            char c = source.charAt(i);
            if (escape) {
                escape = false;
                switch (c) {
                    case 'b':
                        stringBuilder.append('\b');
                        break;
                    case 't':
                        stringBuilder.append('\t');
                        break;
                    case 'n':
                        stringBuilder.append('\n');
                        break;
                    case 'f':
                        stringBuilder.append('\f');
                        break;
                    case 'r':
                        stringBuilder.append('\r');
                        break;
                    case '"':
                        stringBuilder.append('"');
                        break;
                    case '\'':
                        stringBuilder.append('\'');
                        break;
                    case '\\':
                        stringBuilder.append('\\');
                        break;
                    default:
                        errors.add(new TokenizingException(i - 1, source.substring(i - 1, i + 1), "Invalid escape"));
                        break;
                }
            } else {
                if (c == '"') {
                    tokens.add(new StringToken(i, source.substring(start, i + 1), stringBuilder.toString()));
                    return i + 1;
                }
                if (c == '\\') {
                    escape = true;
                    continue;
                }
                stringBuilder.append(c);
            }
        }
        errors.add(new TokenizingException(start, "\"", "Stray quote mark"));
        return start + 1;
    }
}
