package net.wolfesoftware.jax.codegen;

import java.io.DataOutputStream;
import java.util.*;
import java.util.Map.Entry;

/**
 * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#20080
 */
public class ConstantPool
{
    private short totalSize = 0;
    private final HashMap<String, Short> utf8Map = new HashMap<String, Short>();
    private final HashMap<Integer, Short> integerMap = new HashMap<Integer, Short>();
    public void write(DataOutputStream out)
    {
        byte[][] elements = new byte[totalSize][];
        for (Entry<String, Short> entry : utf8Map.entrySet())
            elements[entry.getValue()] = encodeUtf8(entry.getKey());
        throw null;
    }

    private byte[] encodeUtf8(String key)
    {
        return null;
    }

    public short getUtf8(String value)
    {
        return get(utf8Map, value);
    }
    public short getInteger(int value)
    {
        return get(integerMap, value);
    }
    private <T> short get(HashMap<T, Short> map, T value)
    {
        Short index = map.get(value);
        if (index != null)
            return index.shortValue();
        index = nextIndex();
        map.put(value, index);
        return index;
    }

    public short getClass(String _interface)
    {
        throw null;
    }

    private Short nextIndex()
    {
        // 1-based index
        return ++totalSize;
    }

}
