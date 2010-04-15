package net.wolfesoftware.jax.ast;

public class StaticMethodInvocation extends ParseElement
{
    public TypeId typeId;
    public AmbiguousMethodInvocation methodInvocation;
    public StaticMethodInvocation(TypeId typeId, AmbiguousMethodInvocation methodInvocation)
    {
        this.typeId = typeId;
        this.methodInvocation = methodInvocation;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        typeId.decompile(indentation, out);
        out.append('.');
        methodInvocation.decompile(indentation, out);
    }

    public static final int TYPE = 0x78ab09c9;
    public int getElementType()
    {
        return TYPE;
    }
}
