package net.wolfesoftware.java.jax.tokenizer;

import java.lang.reflect.*;
import java.util.*;

public final class Lang
{
    // http://www.fileformat.info/tool/hash.htm
    private Lang()
    {
    }

    public static final String KEYWORD_VOID = "void";
    public static final String KEYWORD_INT = "int";
    public static final String KEYWORD_BOOLEAN = "boolean";
    public static final String KEYWORD_IF = "if";
    public static final String KEYWORD_ELSE = "else";
    public static final String KEYWORD_TRUE = "true";
    public static final String KEYWORD_FALSE = "false";

    public static final String SYMBOL_BANG_EQUALS = "!=";
    public static final String SYMBOL_OPEN_PARENS = "(";
    public static final String SYMBOL_CLOSE_PARENS = ")";
    public static final String SYMBOL_ASTERISK = "*";
    public static final String SYMBOL_PLUS = "+";
    public static final String SYMBOL_COMMA = ",";
    public static final String SYMBOL_MINUS = "-";
    public static final String SYMBOL_SLASH = "/";
    public static final String SYMBOL_SEMICOLON = ";";
    public static final String SYMBOL_EQUALS = "=";
    public static final String SYMBOL_EQUALS_EQUALS = "==";
    public static final String SYMBOL_OPEN_BRACE = "{";
    public static final String SYMBOL_CLOSE_BRACE = "}";

    public static final ArrayList<String> ALL = new ArrayList<String>();
    static {
        Field[] fields = Lang.class.getFields();
        for (Field field : fields)
        {
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
