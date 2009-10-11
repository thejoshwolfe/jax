package net.wolfesoftware.jax.lexiconizer;

import java.util.HashMap;

public class ArrayType extends Type
{
    private ArrayType(Type basicType)
    {
        super(basicType.fullName + "[]", basicType.id + "[]");
    }

    @Override
    public boolean isInstanceOf(Type type)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Field resolveField(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Method resolveMethod(String name, Type[] argumentSignature)
    {
        // TODO Auto-generated method stub
        return null;
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
