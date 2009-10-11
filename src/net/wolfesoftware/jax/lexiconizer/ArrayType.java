package net.wolfesoftware.jax.lexiconizer;

import java.util.HashMap;

/**
 * http://java.sun.com/docs/books/jls/second_edition/html/arrays.doc.html
 */
public class ArrayType extends Type
{
    private final Type scalarType;
    private ArrayType(Type scalarType)
    {
        super(scalarType.fullName + "[]", scalarType.id + "[]");
        this.scalarType = scalarType;
    }

    @Override
    public boolean isInstanceOf(Type type)
    {
        throw new RuntimeException("TODO: Auto-generated method stub");
    }

    @Override
    public Field resolveField(String name)
    {
        throw new RuntimeException("TODO: Auto-generated method stub");
    }

    @Override
    public Method resolveMethod(String name, Type[] argumentSignature)
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
