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
    private final HashMap<Float, Short> floatMap = new HashMap<Float, Short>();
    private final HashMap<Long, Short> longMap = new HashMap<Long, Short>();
    private final HashMap<Double, Short> doubleMap = new HashMap<Double, Short>();
    private final HashMap<Short, Short> classMap = new HashMap<Short, Short>();
    private final HashMap<Short, Short> stringMap = new HashMap<Short, Short>();
    private final HashMap<Integer, Short> fieldMap = new HashMap<Integer, Short>();
    private final HashMap<Integer, Short> methodMap = new HashMap<Integer, Short>();
    private final HashMap<Integer, Short> interfaceMethodMap = new HashMap<Integer, Short>();
    private final HashMap<Integer, Short> nameAndTypeMap = new HashMap<Integer, Short>();
    public void write(DataOutputStream out) throws IOException
    {
        out.writeShort(totalSize + 1);
        byte[][] elements = new byte[totalSize][];
        for (Entry<String, Short> entry : utf8Map.entrySet())
            elements[entry.getValue() - 1] = encodeUtf8(entry.getKey());
        for (Entry<Integer, Short> entry : integerMap.entrySet())
            elements[entry.getValue() - 1] = encodeInteger(entry.getKey());
        for (Entry<Float, Short> entry : floatMap.entrySet())
            elements[entry.getValue() - 1] = encodeFloat(entry.getKey());
        for (Entry<Long, Short> entry : longMap.entrySet()) {
            elements[entry.getValue() - 1] = encodeLong(entry.getKey());
            elements[entry.getValue()] = new byte[0];
        }
        for (Entry<Double, Short> entry : doubleMap.entrySet()) {
            elements[entry.getValue() - 1] = encodeDouble(entry.getKey());
            elements[entry.getValue()] = new byte[0];
        }
        for (Entry<Short, Short> entry : classMap.entrySet())
            elements[entry.getValue() - 1] = encodeClass(entry.getKey());
        for (Entry<Short, Short> entry : stringMap.entrySet())
            elements[entry.getValue() - 1] = encodeString(entry.getKey());
        for (Entry<Integer, Short> entry : fieldMap.entrySet())
            elements[entry.getValue() - 1] = encodeField(entry.getKey());
        for (Entry<Integer, Short> entry : methodMap.entrySet())
            elements[entry.getValue() - 1] = encodeMethod(entry.getKey());
        for (Entry<Integer, Short> entry : interfaceMethodMap.entrySet())
            elements[entry.getValue() - 1] = encodeInterfaceMethod(entry.getKey());
        for (Entry<Integer, Short> entry : nameAndTypeMap.entrySet())
            elements[entry.getValue() - 1] = encodeNameAndType(entry.getKey());

        for (byte[] element : elements)
            out.write(element);
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

    private byte[] encodeInteger(int value)
    {
        return new byte[] {
                CONSTANT_Integer,
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)(value >>> 0),
        };
    }

    private byte[] encodeFloat(float value)
    {
        int intValue = Float.floatToIntBits(value);
        return new byte[] {
                CONSTANT_Float,
                (byte)(intValue >>> 24),
                (byte)(intValue >>> 16),
                (byte)(intValue >>> 8),
                (byte)(intValue >>> 0),
        };
    }

    private byte[] encodeLong(long value)
    {
        return new byte[] {
                CONSTANT_Long,
                (byte)(value >>> 56),
                (byte)(value >>> 48),
                (byte)(value >>> 40),
                (byte)(value >>> 32),
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)(value >>> 0),
        };
    }

    private byte[] encodeDouble(double value)
    {
        long longValue = Double.doubleToLongBits(value);
        return new byte[] {
                CONSTANT_Double,
                (byte)(longValue >>> 56),
                (byte)(longValue >>> 48),
                (byte)(longValue >>> 40),
                (byte)(longValue >>> 32),
                (byte)(longValue >>> 24),
                (byte)(longValue >>> 16),
                (byte)(longValue >>> 8),
                (byte)(longValue >>> 0),
        };
    }

    private byte[] encodeClass(short value)
    {
        return new byte[] {
                CONSTANT_Class,
                (byte)(value >>> 8),
                (byte)(value >>> 0),
        };
    }

    private byte[] encodeString(short value)
    {
        return new byte[] {
                CONSTANT_String,
                (byte)(value >>> 8),
                (byte)(value >>> 0),
        };
    }

    private byte[] encodeField(int value)
    {
        return new byte[] {
                CONSTANT_Fieldref,
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)(value >>> 0),
        };
    }
    private byte[] encodeMethod(int value)
    {
        return new byte[] {
                CONSTANT_Methodref,
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)(value >>> 0),
        };
    }
    private byte[] encodeInterfaceMethod(int value)
    {
        return new byte[] {
                CONSTANT_InterfaceMethodref,
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)(value >>> 0),
        };
    }

    private byte[] encodeNameAndType(int value)
    {
        return new byte[] {
                CONSTANT_NameAndType,
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)(value >>> 0),
        };
    }

    public short getUtf8(String value)
    {
        return get(utf8Map, value, 1);
    }
    public short getInteger(int value)
    {
        return get(integerMap, value, 1);
    }
    public short getFloat(float value)
    {
        return get(floatMap, value, 1);
    }
    public short getLong(long value)
    {
        return get(longMap, value, 2);
    }
    public short getDouble(double value)
    {
        return get(doubleMap, value, 2);
    }
    public short getClass(Type type)
    {
        return get(classMap, getUtf8(type.getTypeName()), 1);
    }
    public short getString(String value)
    {
        return get(stringMap, getUtf8(value), 1);
    }
    public short getField(Field field)
    {
        short class_index = getClass(field.declaringType);
        short name_and_type_index = getNameAndType(field);
        return get(fieldMap, (class_index << 16) | name_and_type_index, 1);
    }
    /**
     *  http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#42041
     */
    public short getMethod(TakesArguments value)
    {
        short class_index = getClass(value.declaringType);
        short name_and_type_index = getNameAndType(value);
        return get(methodMap, (class_index << 16) | name_and_type_index, 1);
    }
    public short getInterfaceMethod(Method method)
    {
        short class_index = getClass(method.declaringType);
        short name_and_type_index = getNameAndType(method);
        return get(interfaceMethodMap, (class_index << 16) | name_and_type_index, 1);
    }
    private short getNameAndType(Field field)
    {
        short name_index = getUtf8(field.name);
        short descriptor_index = getUtf8(field.getDescriptor());
        return get(nameAndTypeMap, (name_index << 16) | descriptor_index, 1);
    }
    private short getNameAndType(TakesArguments value)
    {
        short name_index = getUtf8(value.getName());
        short descriptor_index = getUtf8(value.getDescriptor());
        return get(nameAndTypeMap, (name_index << 16) | descriptor_index, 1);
    }

    private <T> short get(HashMap<T, Short> map, T value, int size)
    {
        Short index = map.get(value);
        if (index != null)
            return index.shortValue();
        index = nextIndex();
        if (size == 2)
            nextIndex();
        map.put(value, index);
        return index;
    }

    private Short nextIndex()
    {
        // 1-based index
        return ++totalSize;
    }
}
