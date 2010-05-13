package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.RuntimeType;

public class ReturnVoid extends BranchStatement
{
    private ReturnVoid()
    {
        this.branchType = RuntimeType.VOID;
    }

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

    @Override
    protected boolean isSingletonLike()
    {
        return true;
    }

    public static final ReturnVoid INSTANCE = new ReturnVoid();
}
