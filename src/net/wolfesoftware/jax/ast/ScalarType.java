package net.wolfesoftware.jax.ast;

public class ScalarType extends SwitchElement
{
    public ScalarType(ParseElement content)
    {
        super(content);
    }

    public static final int TYPE = 0x153f03f9;
    public int getElementType()
    {
        return TYPE;
    }

    public static final ScalarType KEYWORD_VOID = new ScalarType(PrimitiveType.KEYWORD_VOID);
    public static final ScalarType KEYWORD_INT = new ScalarType(PrimitiveType.KEYWORD_INT);
    public static final ScalarType KEYWORD_BOOLEAN = new ScalarType(PrimitiveType.KEYWORD_BOOLEAN);
}
