package net.wolfesoftware.java.jax.lexiconizer;

public class RuntimeType extends Type
{
    private final Class<?> underlyingType;
    public RuntimeType(String packageId, String id, Class<?> underlyingType)
    {
        super(packageId, id);
        this.underlyingType = underlyingType;
    }

    @Override
    public Method resolveMethod(String name, Type[] argumentSignature)
    {
        return null;
    }
}
