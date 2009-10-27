package net.wolfesoftware.jax.lexiconizer;

import java.util.HashMap;

public class Field
{
    public Type declaringType;
    public Type returnType;
    public String name;
    public Field(Type declaringType, Type returnType, String name)
    {
        this.declaringType = declaringType;
        this.returnType = returnType;
        this.name = name;
    }

    private static final HashMap<java.lang.reflect.Field, Field> cache = new HashMap<java.lang.reflect.Field, Field>();
    public static Field getField(java.lang.reflect.Field underlyingField)
    {
        Field field = cache.get(underlyingField);
        if (field == null)
        {
            field = new Field(RuntimeType.getType(underlyingField.getDeclaringClass()), RuntimeType.getType(underlyingField.getType()), underlyingField.getName());
            cache.put(underlyingField, field);
        }
        return field;
    }
    public String getDescriptor()
    {
        return returnType.getTypeCode();
    }
}
