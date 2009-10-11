package net.wolfesoftware.jax.ast;

public class PrimitiveType extends ParseElement
{
    private final KeywordElement keywordElement;
    private PrimitiveType(KeywordElement keywordElement)
    {
        this.keywordElement = keywordElement;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        keywordElement.decompile(indentation, out);
    }

    public static final int TYPE = 0x2527055c;
    public int getElementType()
    {
        return TYPE;
    }

    public static final PrimitiveType KEYWORD_VOID = new PrimitiveType(KeywordElement.KEYWORD_VOID);
    public static final PrimitiveType KEYWORD_INT = new PrimitiveType(KeywordElement.KEYWORD_INT);
    public static final PrimitiveType KEYWORD_BOOLEAN = new PrimitiveType(KeywordElement.KEYWORD_BOOLEAN);
}
