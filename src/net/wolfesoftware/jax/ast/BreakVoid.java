package net.wolfesoftware.jax.ast;

public class BreakVoid extends BranchStatement
{
    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("break");
    }

    public static final int TYPE = 0x10e60378;
    public int getElementType()
    {
        return TYPE;
    }
}
