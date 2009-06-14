package net.wolfesoftware.java.jax.ast;

public class StaticFunctionInvocation extends ParseElement
{
    public TypeId typeId;
    public FunctionInvocation functionInvocation;
    public StaticFunctionInvocation(TypeId typeId, FunctionInvocation functionInvocation)
    {
        this.typeId = typeId;
        this.functionInvocation = functionInvocation;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        typeId.decompile(indentation, out);
        out.append('.');
        functionInvocation.decompile(indentation, out);
    }

    public static final int TYPE = 0x78ab09c9;
    public int getElementType()
    {
        return TYPE;
    }
}
