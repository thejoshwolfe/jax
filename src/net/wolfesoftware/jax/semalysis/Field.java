package net.wolfesoftware.jax.semalysis;

import java.lang.reflect.Modifier;
import java.util.HashMap;

public class Field
{
    public Type declaringType;
    public Type returnType;
    public String name;
    public final boolean isStatic;
    public Field(Type declaringType, Type returnType, String name, boolean isStatic)
    {
        this.declaringType = declaringType;
        this.returnType = returnType;
        this.name = name;
        this.isStatic = isStatic;
    }

    private static final HashMap<java.lang.reflect.Field, Field> cache = new HashMap<java.lang.reflect.Field, Field>();
    public static Field getField(java.lang.reflect.Field underlyingField)
    {
        Field field = cache.get(underlyingField);
        if (field == null) {
            Type declaringType = RuntimeType.getType(underlyingField.getDeclaringClass());
            Type returnType = RuntimeType.getType(underlyingField.getType());
            field = new Field(declaringType, returnType, underlyingField.getName(), Modifier.isStatic(underlyingField.getModifiers()));
            cache.put(underlyingField, field);
        }
        return field;
    }
    public String getDescriptor()
    {
        return returnType.getTypeCode();
    }
    public boolean isArrayLength()
    {
        return false;
    }
}
