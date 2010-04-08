package net.wolfesoftware.jax.ast;

public class ThrowsDeclaration extends ParseElement
{
    public ThrowsList throwsList;
    public ThrowsDeclaration(ThrowsList throwsList)
    {
        this.throwsList = throwsList;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(" throws ");
        throwsList.decompile(indentation, out);
    }

    public static final int TYPE = 0x3d9c06ee;
    public int getElementType()
    {
        return TYPE;
    }
}
