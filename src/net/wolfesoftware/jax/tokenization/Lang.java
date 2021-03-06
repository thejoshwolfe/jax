package net.wolfesoftware.jax.tokenization;

import java.lang.reflect.*;
import java.util.*;

public final class Lang
{
    private Lang()
    {
    }

    public static final String 
    KEYWORD_ABSTRACT = "abstract",
    KEYWORD_BOOLEAN = "boolean",
    KEYWORD_BREAK = "break",
    KEYWORD_BYTE = "byte",
    KEYWORD_CATCH = "catch",
    KEYWORD_CLASS = "class",
    KEYWORD_CONTINUE = "continue",
    KEYWORD_DO = "do",
    KEYWORD_DOUBLE = "double",
    KEYWORD_ELSE = "else",
    KEYWORD_FALSE = "false",
    KEYWORD_FLOAT = "float",
    KEYWORD_FINAL = "final",
    KEYWORD_FINALLY = "finally",
    KEYWORD_FOR = "for",
    KEYWORD_IF = "if",
    KEYWORD_IMPLEMENTS = "implements",
    KEYWORD_IMPORT = "import",
    KEYWORD_INSTANCEOF = "instanceof",
    KEYWORD_INT = "int",
    KEYWORD_NATIVE = "native",
    KEYWORD_NEW = "new",
    KEYWORD_NULL = "null",
    KEYWORD_PACKAGE = "package",
    KEYWORD_PRIVATE = "private",
    KEYWORD_PROTECTED = "protected",
    KEYWORD_PUBLIC = "public",
    KEYWORD_RETURN = "return",
    KEYWORD_STATIC = "static",
    KEYWORD_STRICTFP = "strictfp",
    KEYWORD_SUPER = "super",
    KEYWORD_SYNCHRONIZED = "synchronized",
    KEYWORD_THIS = "this",
    KEYWORD_THROW = "throw",
    KEYWORD_THROWS = "throws",
    KEYWORD_TRANSIENT = "transient",
    KEYWORD_TRUE = "true",
    KEYWORD_TRY = "try",
    KEYWORD_VOID = "void",
    KEYWORD_VOLATILE = "volatile",
    KEYWORD_WHILE = "while";

    public static final String 
    SYMBOL_BANG = "!",
    SYMBOL_BANG_EQUALS = "!=",
    SYMBOL_PERCENT = "%",
    SYMBOL_PERCENT_EQUALS = "%=",
    SYMBOL_AMPERSAND = "&",
    SYMBOL_AMPERSAND_AMPERSAND = "&&",
    SYMBOL_AMPERSAND_EQUALS = "&=",
    SYMBOL_OPEN_PARENS = "(",
    SYMBOL_CLOSE_BRACE = "}",
    SYMBOL_ASTERISK = "*",
    SYMBOL_ASTERISK_EQUALS = "*=",
    SYMBOL_PLUS = "+",
    SYMBOL_PLUS_PLUS = "++",
    SYMBOL_PLUS_EQUALS = "+=",
    SYMBOL_COMMA = ",",
    SYMBOL_MINUS = "-",
    SYMBOL_MINUS_MINUS = "--",
    SYMBOL_MINUS_EQUALS = "-=",
    SYMBOL_PERIOD = ".",
    SYMBOL_SLASH = "/",
    SYMBOL_SLASH_EQUALS = "/=",
    SYMBOL_COLON = ":",
    SYMBOL_SEMICOLON = ";",
    SYMBOL_LESS_THAN = "<",
    SYMBOL_LESS_THAN_EQUALS = "<=",
    SYMBOL_LESS_THAN_LESS_THAN = "<<",
    SYMBOL_LESS_THAN_LESS_THAN_EQUALS = "<<=",
    SYMBOL_EQUALS = "=",
    SYMBOL_EQUALS_EQUALS = "==",
    SYMBOL_GREATER_THAN = ">",
    SYMBOL_GREATER_THAN_EQUALS = ">=",
    SYMBOL_GREATER_THAN_GREATER_THAN = ">>",
    SYMBOL_GREATER_THAN_GREATER_THAN_EQUALS = ">>=",
    SYMBOL_GREATER_THAN_GREATER_THAN_GREATER_THAN = ">>>",
    SYMBOL_GREATER_THAN_GREATER_THAN_GREATER_THAN_EQUALS = ">>>=",
    SYMBOL_QUESTION = "?",
    SYMBOL_OPEN_BRACKET = "[",
    SYMBOL_CLOSE_BRACKET = "]",
    SYMBOL_CARET = "^",
    SYMBOL_CARET_EQUALS = "^=",
    SYMBOL_OPEN_BRACE = "{",
    SYMBOL_PIPE = "|",
    SYMBOL_PIPE_EQUALS = "|=",
    SYMBOL_PIPE_PIPE = "||",
    SYMBOL_CLOSE_PARENS = ")",
    SYMBOL_TILDE = "~",
    SYMBOL_TILDE_EQUALS = "~=";

    public static final ArrayList<String> ALL = new ArrayList<String>();
    static {
        Field[] fields = Lang.class.getFields();
        for (Field field : fields) {
            try {
                if (field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL) && field.getType() == String.class)
                    ALL.add((String)field.get(null));
            } catch (IllegalAccessException e) {
            }
        }
        Collections.sort(ALL, new Comparator<String>() {
            public int compare(String o1, String o2)
            {
                return o2.length() - o1.length();
            }
        });
    }
}
