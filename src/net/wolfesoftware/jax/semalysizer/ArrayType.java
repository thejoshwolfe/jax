package net.wolfesoftware.jax.semalysizer;

import java.util.*;

/**
 * http://java.sun.com/docs/books/jls/second_edition/html/arrays.doc.html
 */
public class ArrayType extends Type
{
    public final Type scalarType;
    private final Field lengthField;
    private ArrayType(Type scalarType)
    {
        super(scalarType.fullName + "[]", scalarType.id + "[]");
        this.scalarType = scalarType;
        lengthField = new Field(this, RuntimeType.INT, "length") {
            @Override
            public boolean isArrayLength()
            {
                return true;
            }
        };
    }

    @Override
    public boolean isInstanceOf(Type type)
    {
        throw new RuntimeException("TODO: Auto-generated method stub");
    }

    @Override
    public Field resolveField(String name)
    {
        if (name.equals("length"))
            return lengthField;
        return null;
    }

    @Override
    protected LinkedList<Method> getMethods()
    {
        return RuntimeType.OBJECT.getMethods();
    }

    @Override
    protected LinkedList<Constructor> getConstructors()
    {
        throw new RuntimeException("TODO: Auto-generated method stub");
    }

    @Override
    public String getTypeCode()
    {
        return "[" + scalarType.getTypeCode();
    }

    public static final int TYPE = 0x51691576; // TODO
    public int getType()
    {
        return TYPE;
    }


    private static final HashMap<Type, ArrayType> cache = new HashMap<Type, ArrayType>();
    public static ArrayType getType(Type type)
    {
        ArrayType arrayType = cache.get(type);
        if (arrayType == null) {
            arrayType = new ArrayType(type);
            cache.put(type, arrayType);
        }
        return arrayType;
    }
}
