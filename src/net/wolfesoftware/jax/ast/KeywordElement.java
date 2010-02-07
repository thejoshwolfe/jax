package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.tokenizer.Lang;

public class KeywordElement extends ParseElement
{
    public final String text;

    public KeywordElement(String text)
    {
        this.text = text;
    }

    public static final int TYPE = 0x2a2105b0;
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(text);
    }

    public static final KeywordElement INT = new KeywordElement(Lang.KEYWORD_INT);
    public static final KeywordElement BYTE = new KeywordElement(Lang.KEYWORD_BYTE);
    public static final KeywordElement FLOAT = new KeywordElement(Lang.KEYWORD_FLOAT);
    public static final KeywordElement DOUBLE = new KeywordElement(Lang.KEYWORD_DOUBLE);
    public static final KeywordElement VOID = new KeywordElement(Lang.KEYWORD_VOID);
    public static final KeywordElement BOOLEAN = new KeywordElement(Lang.KEYWORD_BOOLEAN);
    
    public static final KeywordElement PUBLIC = new KeywordElement(Lang.KEYWORD_PUBLIC);
    public static final KeywordElement FINAL = new KeywordElement(Lang.KEYWORD_FINAL);
}
