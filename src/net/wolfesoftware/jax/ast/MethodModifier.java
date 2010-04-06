package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.codegen.MethodInfo;

public class MethodModifier extends ParseElement
{
    public final short bitmask;

    private final KeywordElement keywordElement;
    private MethodModifier(KeywordElement keywordElement, short bitmask)
    {
        this.keywordElement = keywordElement;
        this.bitmask = bitmask;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        keywordElement.decompile(indentation, out);
    }

    public static final int TYPE = 0x290c0591;
    public int getElementType()
    {
        return TYPE;
    }

    public static final MethodModifier PUBLIC = new MethodModifier(KeywordElement.PUBLIC, MethodInfo.ACC_PUBLIC);
    public static final MethodModifier PRIVATE = new MethodModifier(KeywordElement.PRIVATE, MethodInfo.ACC_PRIVATE);
    public static final MethodModifier PROTECTED = new MethodModifier(KeywordElement.PROTECTED, MethodInfo.ACC_PROTECTED);
    public static final MethodModifier STATIC = new MethodModifier(KeywordElement.STATIC, MethodInfo.ACC_STATIC);
    public static final MethodModifier FINAL = new MethodModifier(KeywordElement.FINAL, MethodInfo.ACC_FINAL);
    public static final MethodModifier SYNCHRONIZED = new MethodModifier(KeywordElement.SYNCHRONIZED, MethodInfo.ACC_SYNCHRONIZED);
    // public static final MethodModifier NATIVE = new MethodModifier(KeywordElement.NATIVE, MethodInfo.ACC_NATIVE);
    public static final MethodModifier ABSTRACT = new MethodModifier(KeywordElement.ABSTRACT, MethodInfo.ACC_ABSTRACT);
    public static final MethodModifier STRICTFP = new MethodModifier(KeywordElement.STRICTFP, MethodInfo.ACC_STRICT);
}
