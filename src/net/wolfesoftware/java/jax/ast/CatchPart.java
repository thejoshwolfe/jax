package net.wolfesoftware.java.jax.ast;

public class CatchPart extends ParseElement
{
    public CatchList catchList;

    public CatchPart(CatchList catchList)
    {
        this.catchList = catchList;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("catch ");
        catchList.decompile(indentation, out);
    }

    public static final int TYPE = 0x10ad037b;
    public int getElementType()
    {
        return TYPE;
    }
}
