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

    public static final PrimitiveType VOID = new PrimitiveType(KeywordElement.VOID);
    public static final PrimitiveType INT = new PrimitiveType(KeywordElement.INT);
    public static final PrimitiveType BYTE = new PrimitiveType(KeywordElement.BYTE);
    public static final PrimitiveType BOOLEAN = new PrimitiveType(KeywordElement.BOOLEAN);
}
