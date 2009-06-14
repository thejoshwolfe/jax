package net.wolfesoftware.java.jax.ast;

import net.wolfesoftware.java.jax.lexiconizer.Type;

public class TypeId extends SwitchElement
{
    public Type type;

    public TypeId(ParseElement content)
    {
        super(content);
    }

    public static final int TYPE = 0x040401a3;
    public int getElementType()
    {
        return TYPE;
    }

    public static final TypeId KEYWORD_INT = new TypeId(KeywordElement.KEYWORD_INT);
    public static final TypeId KEYWORD_VOID = new TypeId(KeywordElement.KEYWORD_VOID);
    public static final TypeId KEYWORD_BOOLEAN = new TypeId(KeywordElement.KEYWORD_BOOLEAN);
}
