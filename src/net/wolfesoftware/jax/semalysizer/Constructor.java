package net.wolfesoftware.jax.semalysizer;

import net.wolfesoftware.jax.codegen.MethodInfo;

public class Constructor extends TakesArguments
{
    public Constructor(Type declaringType, Type[] argumentSignature)
    {
        super(declaringType, argumentSignature, RuntimeType.VOID);
    }

    @Override
    public String getMethodCode()
    {
        return declaringType.getTypeName() + "/" + getName() + getDescriptor();
    }
    @Override
    public short getFlags()
    {
        return MethodInfo.ACC_PUBLIC;
    }
    @Override
    public String getName()
    {
        return "<init>";
    }
}
