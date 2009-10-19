package net.wolfesoftware.jax.ast;

public class DoubleLiteral extends LiteralElement
{
    public final double value;
    public DoubleLiteral(double value)
    {
        this.value = value;
    }

    public static final int TYPE = 0x236a0529;
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(value);
    }
}
