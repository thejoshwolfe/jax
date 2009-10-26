package net.wolfesoftware.jax.codegen;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import net.wolfesoftware.jax.lexiconizer.*;

/**
 * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#20080
 */
public class ConstantPool
{
    private static final byte
    CONSTANT_Class = 7,
    CONSTANT_Fieldref = 9,
    CONSTANT_Methodref = 10,
    CONSTANT_InterfaceMethodref = 11,
    CONSTANT_String = 8,
    CONSTANT_Integer = 3,
    CONSTANT_Float = 4,
    CONSTANT_Long = 5,
    CONSTANT_Double = 6,
    CONSTANT_NameAndType = 12,
    CONSTANT_Utf8 = 1;

    private short totalSize = 0;
    private final HashMap<String, Short> utf8Map = new HashMap<String, Short>();
    private final HashMap<Integer, Short> integerMap = new HashMap<Integer, Short>();
    private final HashMap<Short, Short> classMap = new HashMap<Short, Short>();
    private final HashMap<Integer, Short> methodMap = new HashMap<Integer, Short>();
    private final HashMap<Integer, Short> nameAndTypeMap = new HashMap<Integer, Short>();
    public void write(DataOutputStream out) throws IOException
    {
        out.writeShort(totalSize);
        byte[][] elements = new byte[totalSize][];
        for (Entry<String, Short> entry : utf8Map.entrySet())
            elements[entry.getValue() - 1] = encodeUtf8(entry.getKey());
        for (Entry<Integer, Short> entry : integerMap.entrySet())
            elements[entry.getValue() - 1] = encodeInteger(entry.getKey());
        for (Entry<Short, Short> entry : classMap.entrySet())
            elements[entry.getValue() - 1] = encodeClass(entry.getKey());
        for (Entry<Integer, Short> entry : methodMap.entrySet())
            elements[entry.getValue() - 1] = encodeMethod(entry.getKey());
        for (Entry<Integer, Short> entry : nameAndTypeMap.entrySet())
            elements[entry.getValue() - 1] = encodeNameAndType(entry.getKey());

        for (byte[] element : elements)
            out.write(element);
    }

    private byte[] encodeNameAndType(Integer value)
    {
        return new byte[] {
                CONSTANT_NameAndType,
                (byte)(value >>> 12),
                (byte)(value >>> 8),
                (byte)(value >>> 4),
                (byte)(value >>> 0),
        };
    }

    private byte[] encodeMethod(Integer value)
    {
        return new byte[] {
                CONSTANT_Methodref,
                (byte)(value >>> 12),
                (byte)(value >>> 8),
                (byte)(value >>> 4),
                (byte)(value >>> 0),
        };
    }

    private byte[] encodeClass(Short value)
    {
        return new byte[] {
                CONSTANT_Class,
                (byte)(value >>> 8),
                (byte)(value >>> 0),
        };
    }

    private byte[] encodeInteger(Integer value)
    {
        return new byte[] {
                CONSTANT_Integer,
                (byte)(value >>> 12),
                (byte)(value >>> 8),
                (byte)(value >>> 4),
                (byte)(value >>> 0),
        };
    }

    private byte[] encodeUtf8(String value)
    {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(buffer);
            out.writeByte(CONSTANT_Utf8);
            byte[] bytes = value.getBytes();
            out.writeShort(bytes.length);
            out.write(bytes);
            return buffer.toByteArray();
        } catch (IOException e) {
            throw null;
        }
    }

    public short getUtf8(String value)
    {
        return get(utf8Map, value);
    }
    public short getInteger(int value)
    {
        return get(integerMap, value);
    }
    /**
     *  http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#42041
     */
    public short getMethod(TakesArguments value)
    {
        short class_index = getClass(value.declaringType.getTypeName());
        short name_and_type_index = getNameAndType(value);
        return get(methodMap, (class_index << 16) | name_and_type_index);
    }
    private short getNameAndType(TakesArguments value)
    {
        short name_index = getUtf8(value.getName());
        short descriptor_index = getUtf8(value.getDescriptor());
        return get(nameAndTypeMap, (name_index << 16) | descriptor_index);
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

    public short getClass(String className)
    {
        return get(classMap, getUtf8(className));
    }

    private Short nextIndex()
    {
        // 1-based index
        return ++totalSize;
    }
}
