package net.wolfesoftware.jax.ast;

public class ContinueVoid extends BranchStatement
{
    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("continue");
    }

    public static final int TYPE = 0x1f3104d8;
    public int getElementType()
    {
        return TYPE;
    }
}
