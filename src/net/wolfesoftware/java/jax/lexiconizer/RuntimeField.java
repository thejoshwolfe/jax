package net.wolfesoftware.java.jax.lexiconizer;

import java.util.HashMap;

public class RuntimeField extends Field
{
    public RuntimeField(java.lang.reflect.Field underlyingField)
    {
        super(RuntimeType.getType(underlyingField.getDeclaringClass()), RuntimeType.getType(underlyingField.getType()));
    }

    private static final HashMap<java.lang.reflect.Field, RuntimeField> cache = new HashMap<java.lang.reflect.Field, RuntimeField>();
    public static RuntimeField getField(java.lang.reflect.Field underlyingField)
    {
        RuntimeField field = cache.get(underlyingField);
        if (field == null)
        {
            field = new RuntimeField(underlyingField);
            cache.put(underlyingField, field);
        }
        return field;
    }
}
