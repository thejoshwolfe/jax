package net.wolfesoftware.jax.ast;

public class LongLiteral extends LiteralElement
{
    public final long value;
    public LongLiteral(long value)
    {
        this.value = value;
    }

    public static final int TYPE = 0x19a1045e;
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(value).append("L");
    }
}
