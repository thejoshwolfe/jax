package net.wolfesoftware.jax.ast;

public class ClassModifier extends ParseElement
{
    private final KeywordElement keywordElement;
    private ClassModifier(KeywordElement keywordElement)
    {
        this.keywordElement = keywordElement;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        keywordElement.decompile(indentation, out);
    }

    public static final int TYPE = 0x231d0526;
    public int getElementType()
    {
        return TYPE;
    }

    public static final ClassModifier PUBLIC = new ClassModifier(KeywordElement.PUBLIC);
    public static final ClassModifier FINAL = new ClassModifier(KeywordElement.FINAL);
}
