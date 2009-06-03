package net.wolfesoftware.java.jax.ast;

public class BooleanLiteral extends LiteralElement
{
    public final boolean value;
    private BooleanLiteral(boolean value)
    {
        this.value = value;
    }

    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(value);
    }

    public static final int TYPE = 0x28d6058e;
    public int getElementType()
    {
        return TYPE;
    }

    public static final BooleanLiteral TRUE = new BooleanLiteral(true);
    public static final BooleanLiteral FALSE = new BooleanLiteral(false);
}
