package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.codegen.ClassFile;

public class ClassModifier extends ParseElement
{
    public final short bitmask;

    private final KeywordElement keywordElement;
    private ClassModifier(KeywordElement keywordElement, short bitmask)
    {
        this.keywordElement = keywordElement;
        this.bitmask = bitmask;
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

    @Override
    protected boolean isSingletonLike()
    {
        return true;
    }

    public static final ClassModifier PUBLIC = new ClassModifier(KeywordElement.PUBLIC, ClassFile.ACC_PUBLIC);
    public static final ClassModifier FINAL = new ClassModifier(KeywordElement.FINAL, ClassFile.ACC_FINAL);
}
