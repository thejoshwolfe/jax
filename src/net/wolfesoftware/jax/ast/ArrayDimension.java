package net.wolfesoftware.jax.ast;

public class ArrayDimension extends ParseElement
{
    private ArrayDimension()
    {
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("[]");
    }

    public static final int TYPE = 0x97239821; // TODO
    public int getElementType()
    {
        return TYPE;
    }

    public static final ArrayDimension INSTANCE = new ArrayDimension();
}
