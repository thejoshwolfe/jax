package net.wolfesoftware.java.jax.parser.elements;

public class IntLiteral extends LiteralElement
{
    public final int value;
    public IntLiteral(int value)
    {
        this.value = value;
    }

    public static final int TYPE = 0x154803f9;
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected void decompile(String indentation, StringBuffer out)
    {
        out.append(value);
    }
}
