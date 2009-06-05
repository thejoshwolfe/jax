package net.wolfesoftware.java.jax.ast;

public class StringLiteral extends LiteralElement
{
    public final String value;
    public final String source;
    public StringLiteral(String value, String source)
    {
        this.value = value;
        this.source = source;
    }

    public static final int TYPE = 0x24b00545;
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(source);
    }
}
