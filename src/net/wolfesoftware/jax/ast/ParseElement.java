package net.wolfesoftware.jax.ast;

import java.lang.reflect.Field;

public abstract class ParseElement implements Cloneable
{
    public abstract int getElementType();
    public final String decompile()
    {
        StringBuilder out = new StringBuilder();
        decompile("", out);
        return out.toString();
    }
    protected abstract void decompile(String indentation, StringBuilder out);
    public final String toString()
    {
        return decompile();
    }

    public final ParseElement cloneElement()
    {
        try {
            return (ParseElement)clone();
        } catch (CloneNotSupportedException e) {
            throw null;
        }
    }
    protected final Object clone() throws CloneNotSupportedException
    {
        // deep copy all fields of this type
        Object object = super.clone();
        for (Field field : object.getClass().getFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()))
                continue;
            if (!ParseElement.class.isAssignableFrom(field.getType()))
                continue;
            try {
                ParseElement fieldParseElement = (ParseElement)field.get(this);
                if (fieldParseElement == null)
                    continue;
                if (fieldParseElement.isSingletonLike())
                    continue;
                field.set(object, (ParseElement)fieldParseElement.clone());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return object;
    }

    protected boolean isSingletonLike()
    {
        return false;
    }

    private static final String INDENTATION_UNIT = "    ";
    protected static String increaseIndentation(String previousIndentation)
    {
        return previousIndentation + INDENTATION_UNIT;
    }
}
