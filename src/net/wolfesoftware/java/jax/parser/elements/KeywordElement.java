package net.wolfesoftware.java.jax.parser.elements;

import net.wolfesoftware.java.jax.Lang;

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
    protected void decompile(String indentation, StringBuffer out)
    {
        out.append(text);
    }

    public static final KeywordElement KEYWORD_INT = new KeywordElement(Lang.KEYWORD_INT);
    public static final KeywordElement KEYWORD_VOID = new KeywordElement(Lang.KEYWORD_VOID);
}
