package net.wolfesoftware.jax.lexiconizer;

import net.wolfesoftware.jax.codegen.MethodInfo;

public class Constructor extends TakesArguments
{
    public Type type;
    public Constructor(Type type, Type[] argumentSignature)
    {
        super(argumentSignature, RuntimeType.VOID);
        this.type = type;
    }

    @Override
    public String getMethodCode()
    {
        return type.getTypeName() + "/" + getName() + getDescriptor();
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
