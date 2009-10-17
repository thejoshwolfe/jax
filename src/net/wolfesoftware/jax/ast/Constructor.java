package net.wolfesoftware.jax.ast;

public class Constructor extends ParseElement
{
    public FunctionInvocation functionInvocation;
    public Constructor(FunctionInvocation functionInvocation)
    {
        this.functionInvocation = functionInvocation;
    }

    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("new ");
        functionInvocation.decompile(indentation, out);
    }

    public static final int TYPE = 0x1b1104a7;
    public int getElementType()
    {
        return TYPE;
    }
}
