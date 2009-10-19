package net.wolfesoftware.jax.ast;

public class FloatLiteral extends LiteralElement
{
    public final float value;
    public FloatLiteral(float value)
    {
        this.value = value;
    }

    public static final int TYPE = 0x1e3d04c4;
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
