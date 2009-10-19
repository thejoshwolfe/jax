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

    public static final ScalarType VOID = new ScalarType(PrimitiveType.VOID);
    public static final ScalarType INT = new ScalarType(PrimitiveType.INT);
    public static final ScalarType BYTE = new ScalarType(PrimitiveType.BYTE);
    public static final ScalarType BOOLEAN = new ScalarType(PrimitiveType.BOOLEAN);
}
