package net.wolfesoftware.java.jax.parser.elements;

public class TypeId extends SwitchElement
{
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
}
