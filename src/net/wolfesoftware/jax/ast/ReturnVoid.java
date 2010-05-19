package net.wolfesoftware.jax.ast;

public class ReturnVoid extends BranchStatement
{
    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("return");
    }

    public static final int TYPE = 0x164b0413;
    public int getElementType()
    {
        return TYPE;
    }
}
