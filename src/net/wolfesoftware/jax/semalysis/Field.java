package net.wolfesoftware.jax.semalysis;

import java.util.HashMap;
import net.wolfesoftware.jax.ast.Modifier;

public class Field
{
    public Type declaringType;
    public Type returnType;
    public String name;
    public short modifiers;
    public Field(Type declaringType, Type returnType, String name, short modifiers)
    {
        this.declaringType = declaringType;
        this.returnType = returnType;
        this.name = name;
        this.modifiers = modifiers;
    }

    private static final HashMap<java.lang.reflect.Field, Field> cache = new HashMap<java.lang.reflect.Field, Field>();
    public static Field getField(java.lang.reflect.Field underlyingField)
    {
        Field field = cache.get(underlyingField);
        if (field == null) {
            Type declaringType = RuntimeType.getType(underlyingField.getDeclaringClass());
            Type returnType = RuntimeType.getType(underlyingField.getType());
            field = new Field(declaringType, returnType, underlyingField.getName(), (short)underlyingField.getModifiers());
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

    public String toString()
    {
        return returnType + " " + name;
    }
    public boolean isStatic()
    {
        return (modifiers & Modifier.ACC_STATIC) != 0;
    }
}
