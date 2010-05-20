package net.wolfesoftware.jax.ast;

import java.util.HashMap;

public class Modifier extends ParseElement
{
    public static final short ACC_PUBLIC = 0x0001;
    public static final short ACC_PRIVATE = 0x0002;
    public static final short ACC_PROTECTED = 0x0004;
    public static final short ACC_STATIC = 0x0008;
    public static final short ACC_FINAL = 0x0010;
    public static final short ACC_SYNCHRONIZED = 0x0020;
    public static final short ACC_SUPER = 0x0020;
    public static final short ACC_VOLATILE = 0x0040;
    public static final short ACC_TRANSIENT = 0x0080;
    public static final short ACC_NATIVE = 0x0100;
    public static final short ACC_INTERFACE = 0x0200;
    public static final short ACC_ABSTRACT = 0x0400;
    public static final short ACC_STRICT = 0x0800;

    public final short bitmask;

    private final KeywordElement keywordElement;
    private Modifier(KeywordElement keywordElement, short bitmask)
    {
        this.keywordElement = keywordElement;
        this.bitmask = bitmask;
        NAME_MAP.put(keywordElement.text, this);
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

    public static final HashMap<String, Modifier> NAME_MAP = new HashMap<String, Modifier>();

    public static final Modifier PUBLIC = new Modifier(KeywordElement.PUBLIC, ACC_PUBLIC);
    public static final Modifier PRIVATE = new Modifier(KeywordElement.PRIVATE, ACC_PRIVATE);
    public static final Modifier PROTECTED = new Modifier(KeywordElement.PROTECTED, ACC_PROTECTED);
    public static final Modifier STATIC = new Modifier(KeywordElement.STATIC, ACC_STATIC);
    public static final Modifier FINAL = new Modifier(KeywordElement.FINAL, ACC_FINAL);
    public static final Modifier SYNCHRONIZED = new Modifier(KeywordElement.SYNCHRONIZED, ACC_SYNCHRONIZED);
    // skip super
    public static final Modifier VOLATILE = new Modifier(KeywordElement.VOLATILE, ACC_VOLATILE);
    public static final Modifier TRANSIENT = new Modifier(KeywordElement.TRANSIENT, ACC_TRANSIENT);
    public static final Modifier NATIVE = new Modifier(KeywordElement.NATIVE, ACC_NATIVE);
    // skip interface
    public static final Modifier ABSTRACT = new Modifier(KeywordElement.ABSTRACT, ACC_ABSTRACT);
    public static final Modifier STRICTFP = new Modifier(KeywordElement.STRICTFP, ACC_STRICT);

    // modifier groups
    public static final short ACCESS_MODIFIERS = ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED;
    public static final short CLASS_MODIFIERS = ACC_PUBLIC | ACC_FINAL | ACC_ABSTRACT;
    public static final short FIELD_MODIFIERS = ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED | ACC_STATIC | ACC_FINAL | ACC_VOLATILE | ACC_TRANSIENT;
    public static final short METHOD_MODIFIERS = ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED | ACC_STATIC | ACC_FINAL | ACC_SYNCHRONIZED | ACC_NATIVE | ACC_ABSTRACT | ACC_STRICT;
    public static final short CONSTRUCTOR_MODIFIERS = ACC_PRIVATE | ACC_PROTECTED | ACC_PUBLIC | ACC_STRICT;
    public static final short INITIALIZER_MODIFIERS = ACC_STATIC;
    // http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#1513
    // If such a method has its ACC_ABSTRACT flag set it may not have any of its ACC_FINAL, ACC_NATIVE, ACC_PRIVATE, ACC_STATIC, ACC_STRICT, or ACC_SYNCHRONIZED  flags set (§2.13.3.2).
    public static final short ABSTRACT_METHOD_ENEMIES_MASK = ACC_FINAL | ACC_NATIVE | ACC_PRIVATE | ACC_STATIC | ACC_STRICT | ACC_SYNCHRONIZED;
    public static final Modifier[] ABSTRACT_METHOD_ENEMIES_ARRAY = { FINAL, NATIVE, PRIVATE, STATIC, STRICTFP, SYNCHRONIZED };
}

