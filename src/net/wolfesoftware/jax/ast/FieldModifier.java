package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.codegen.FieldInfo;

public class FieldModifier extends ParseElement
{
    public final short bitmask;

    private final KeywordElement keywordElement;
    private FieldModifier(KeywordElement keywordElement, short bitmask)
    {
        this.keywordElement = keywordElement;
        this.bitmask = bitmask;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        keywordElement.decompile(indentation, out);
    }

    public static final int TYPE = 0x227f0514;
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected boolean isSingletonLike()
    {
        return true;
    }

    public static final FieldModifier PUBLIC = new FieldModifier(KeywordElement.PUBLIC, FieldInfo.ACC_PUBLIC);
    public static final FieldModifier PRIVATE = new FieldModifier(KeywordElement.PRIVATE, FieldInfo.ACC_PRIVATE);
    public static final FieldModifier PROTECTED = new FieldModifier(KeywordElement.PROTECTED, FieldInfo.ACC_PROTECTED);
    public static final FieldModifier STATIC = new FieldModifier(KeywordElement.STATIC, FieldInfo.ACC_STATIC);
    public static final FieldModifier FINAL = new FieldModifier(KeywordElement.FINAL, FieldInfo.ACC_FINAL);
    public static final FieldModifier VOLATILE = new FieldModifier(KeywordElement.VOLATILE, FieldInfo.ACC_VOLATILE);
    public static final FieldModifier TRANSIENT = new FieldModifier(KeywordElement.TRANSIENT, FieldInfo.ACC_TRANSIENT);
}
