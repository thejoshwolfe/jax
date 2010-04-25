package net.wolfesoftware.jax.ast;

public class StaticMethodInvocation extends AbstractMethodInvocation
{
    public TypeId typeId;
    public StaticMethodInvocation(TypeId typeId, AmbiguousId methodName, Arguments arguments)
    {
        super(methodName, arguments);
        this.typeId = typeId;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        typeId.decompile(indentation, out);
        out.append('.');
        methodName.decompile(indentation, out);
        out.append('(');
        arguments.decompile(indentation, out);
        out.append(')');
    }

    public static final int TYPE = 0x78ab09c9;
    public int getElementType()
    {
        return TYPE;
    }
}
