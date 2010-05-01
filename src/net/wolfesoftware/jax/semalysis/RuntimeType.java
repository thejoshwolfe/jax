package net.wolfesoftware.jax.semalysis;

import java.util.*;

public class RuntimeType extends Type
{
    private final Class<?> underlyingType;
    /** constructor/method loading must be lazy to avoid caching entire base library */
    private LinkedList<Method> methodsCache = null;
    private LinkedList<Constructor> constructorsCache = null;
    private RuntimeType(Class<?> underlyingType)
    {
        super(underlyingType.getName(), underlyingType.getSimpleName());
        this.underlyingType = underlyingType;
    }

    @Override
    public Field resolveField(String name)
    {
        try {
            return Field.getField(underlyingType.getField(name));
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    @Override
    protected LinkedList<Method> getMethods()
    {
        if (methodsCache == null) {
            methodsCache = new LinkedList<Method>();
            for (java.lang.reflect.Method method : underlyingType.getMethods())
                methodsCache.add(Method.getMethod(method));
        }
        return methodsCache;
    }

    @Override
    protected LinkedList<Constructor> getConstructors()
    {
        if (constructorsCache == null) {
            constructorsCache = new LinkedList<Constructor>();
            for (java.lang.reflect.Constructor<?> constructor : underlyingType.getConstructors())
                constructorsCache.add(new Constructor(this, getTypes(constructor.getParameterTypes())));
        }
        return constructorsCache;
    }

    @Override
    public boolean isInstanceOf(Type type)
    {
        if (!(type instanceof RuntimeType))
            return false; // RuntimeTypes only descend from other RuntimeTypes
        return ((RuntimeType)type).underlyingType.isAssignableFrom(underlyingType);
    }

    @Override
    public boolean isInterface()
    {
        return underlyingType.isInterface();
    }

    @Override
    public Type getParent()
    {
        return getType(underlyingType.getSuperclass());
    }

    public static final int TYPE = 0x1b070487;
    public int getType()
    {
        return TYPE;
    }

    private static class PrimitiveType extends RuntimeType
    {
        private final String typeCode;
        private final int size;
        public PrimitiveType(Class<?> type, String typeCode, int size)
        {
            super(type);
            this.typeCode = typeCode;
            this.size = size;
        }
        @Override
        public String getTypeName()
        {
            throw new RuntimeException();
        }
        @Override
        public String getTypeCode()
        {
            return typeCode;
        }
        @Override
        public int getSize()
        {
            return size;
        }
        @Override
        public boolean isPrimitive()
        {
            return true;
        }
    }
    private static class NumericPrimitiveType extends PrimitiveType
    {
        public NumericPrimitiveType(Class<?> type, String typeCode, int size)
        {
            super(type, typeCode, size);
        }
        @Override
        public boolean isInstanceOf(Type type)
        {
            if (!(type instanceof NumericPrimitiveType))
                return false;
            return getPrimitiveConversionType(this, type) >= 0;
        }
    }
    // http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#84645
    public static final RuntimeType VOID = new PrimitiveType(void.class, "V", 0);
    public static final RuntimeType BOOLEAN = new PrimitiveType(boolean.class, "Z", 1);
    public static final RuntimeType BYTE = new NumericPrimitiveType(byte.class, "B", 1);
    public static final RuntimeType SHORT = new NumericPrimitiveType(short.class, "S", 1);
    public static final RuntimeType INT = new NumericPrimitiveType(int.class, "I", 1);
    public static final RuntimeType LONG = new NumericPrimitiveType(long.class, "J", 2); // J makes sense... :|
    public static final RuntimeType FLOAT = new NumericPrimitiveType(float.class, "F", 1);
    public static final RuntimeType DOUBLE = new NumericPrimitiveType(double.class, "D", 2);
    public static final RuntimeType CHAR = new NumericPrimitiveType(char.class, "C", 1);
    public static final RuntimeType[] allPrimitiveTypes = { VOID, BOOLEAN, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, CHAR };
    public static void initPrimitives(HashMap<String, Type> types)
    {
        for (RuntimeType primitiveType : allPrimitiveTypes)
            types.put(primitiveType.simpleName, primitiveType);
    }

    private static final Class<?>[] javaLangTypes = { AbstractMethodError.class, ArithmeticException.class, ArrayIndexOutOfBoundsException.class, ArrayStoreException.class, AssertionError.class,
            Boolean.class, Byte.class, Character.class, CharSequence.class, Class.class, ClassCastException.class, ClassCircularityError.class, ClassFormatError.class, ClassLoader.class,
            ClassNotFoundException.class, Cloneable.class, CloneNotSupportedException.class, Comparable.class, Compiler.class, Double.class, Error.class, Exception.class,
            ExceptionInInitializerError.class, Float.class, IllegalAccessError.class, IllegalAccessException.class, IllegalArgumentException.class, IllegalMonitorStateException.class,
            IllegalStateException.class, IllegalThreadStateException.class, IncompatibleClassChangeError.class, IndexOutOfBoundsException.class, InheritableThreadLocal.class,
            InstantiationError.class, InstantiationException.class, Integer.class, InternalError.class, InterruptedException.class, LinkageError.class, Long.class, Math.class,
            NegativeArraySizeException.class, NoClassDefFoundError.class, NoSuchFieldError.class, NoSuchFieldException.class, NoSuchMethodError.class, NoSuchMethodException.class,
            NullPointerException.class, Number.class, NumberFormatException.class, Object.class, OutOfMemoryError.class, Package.class, Process.class, Runnable.class, Runtime.class,
            RuntimeException.class, RuntimePermission.class, SecurityException.class, SecurityManager.class, Short.class, StackOverflowError.class, StackTraceElement.class, StrictMath.class,
            String.class, StringBuffer.class, StringIndexOutOfBoundsException.class, System.class, Thread.class, ThreadDeath.class, ThreadGroup.class, ThreadLocal.class, Throwable.class,
            UnknownError.class, UnsatisfiedLinkError.class, UnsupportedClassVersionError.class, UnsupportedOperationException.class, VerifyError.class, VirtualMachineError.class, Void.class, };
    public static void initJavaLang(HashMap<String, Type> types)
    {
        for (Class<?> type : javaLangTypes)
            types.put(type.getSimpleName(), cache.get(type));
    }
    private static final HashMap<Class<?>, RuntimeType> cache = new HashMap<Class<?>, RuntimeType>();
    static {
        for (Class<?> type : javaLangTypes)
            cache.put(type, new RuntimeType(type));
        for (RuntimeType primitiveType : allPrimitiveTypes)
            cache.put(primitiveType.underlyingType, primitiveType);
    }
    public static final RuntimeType STRING = cache.get(String.class);
    public static final RuntimeType OBJECT = cache.get(Object.class);
    public static Type getType(Class<?> underlyingType)
    {
        RuntimeType type = cache.get(underlyingType);
        if (type == null) {
            type = new RuntimeType(underlyingType);
            cache.put(underlyingType, type);
        }
        return type;
    }
    public static Type[] getTypes(Class<?>[] underlyingTypes)
    {
        Type[] returnTypes = new Type[underlyingTypes.length];
        for (int i = 0; i < underlyingTypes.length; i++) 
            returnTypes[i] = getType(underlyingTypes[i]);
        return returnTypes;
    }

    // http://java.sun.com/docs/books/jvms/second_edition/html/Concepts.doc.html#23435
    private static final int[][] primitiveConversionTable = {
    // to: char byte short int long float double    | from:
        {     0,  -1,   -1,  1,   1,    1,     1}, // char   0
        {    -1,   0,    1,  1,   1,    1,     1}, // byte   1
        {    -1,  -1,    0,  1,   1,    1,     1}, // short  2
        {    -1,  -1,   -1,  0,   1,    1,     1}, // int    3
        {    -1,  -1,   -1, -1,   0,    1,     1}, // long   4
        {    -1,  -1,   -1, -1,  -1,    0,     1}, // float  5
        {    -1,  -1,   -1, -1,  -1,   -1,     0}, // double 6
    };
    public static int getPrimitiveConversionType(Type fromType, Type toType) {
        return primitiveConversionTable[getPrimitiveIndex(fromType)][getPrimitiveIndex(toType)];
    }
    private static int getPrimitiveIndex(Type type) {
        if (type == CHAR)
            return 0;
        if (type == BYTE)
            return 1;
        if (type == SHORT)
            return 2;
        if (type == INT)
            return 3;
        if (type == LONG)
            return 4;
        if (type == FLOAT)
            return 5;
        if (type == DOUBLE)
            return 6;
        throw new RuntimeException("hey, this isn't a primitive type: " + type);
    }
}
