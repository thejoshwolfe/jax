package net.wolfesoftware.jax.lexiconizer;

import java.util.*;

public class RuntimeType extends Type
{
    private final Class<?> underlyingType;
    /** constructor/method loading must be lazy to avoid caching entire base library */
    private LinkedList<Constructor> constructorsCache = null;
    private RuntimeType(Class<?> underlyingType)
    {
        super(underlyingType.getName(), underlyingType.getSimpleName());
        this.underlyingType = underlyingType;
    }

    private static final Comparator<java.lang.reflect.Method> overloadSorter = new Comparator<java.lang.reflect.Method>() {
        public int compare(java.lang.reflect.Method o1, java.lang.reflect.Method o2)
        {
            Class<?>[] params1 = o1.getParameterTypes();
            Class<?>[] params2 = o2.getParameterTypes();
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] == params2[i])
                    continue;
                return params1[i].isAssignableFrom(params2[i]) ? 1 : -1;
            }
            throw new RuntimeException("Duplicate method signatures: " + o1 + " : " + o2);
        }
    };
    @Override
    public Method resolveMethod(String name, Type[] argumentSignature)
    {
        Class<?>[] argumentTypes = new Class<?>[argumentSignature.length];
        for (int i = 0; i < argumentSignature.length; i++) {
            if (!(argumentSignature[i] instanceof RuntimeType))
                throw new RuntimeException("Runtime Types only reference other Runtime Types");
            argumentTypes[i] = ((RuntimeType)argumentSignature[i]).underlyingType;
        }
        java.lang.reflect.Method[] allMethods = underlyingType.getMethods();
        ArrayList<java.lang.reflect.Method> overloads = new ArrayList<java.lang.reflect.Method>();
        methods: for (java.lang.reflect.Method method : allMethods) {
            if (!method.getName().equals(name))
                continue; // wrong name
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != argumentSignature.length)
                continue; // wrong number of arguments
            for (int i = 0; i < argumentSignature.length; i++) {
                if (parameterTypes[i] == argumentTypes[i])
                    continue; // so far so good
                if (parameterTypes[i].isPrimitive() || argumentTypes[i].isPrimitive()) {
                    // TODO: type coercion and/or boxing/unboxing logic.
                    continue methods; // primitive type mismatch
                }
                if (parameterTypes[i].isAssignableFrom(argumentTypes[i]))
                    continue; // not a match, but it's castable. so far so good.
                continue methods; // not castable
            }
            // this method works
            overloads.add(method);
        }
        if (overloads.size() == 0)
            return null; // no eligible methods found
        Collections.sort(overloads, overloadSorter);
        return RuntimeMethod.getMethod(overloads.get(0));
    }
    @Override
    public Field resolveField(String name)
    {
        try {
            return RuntimeField.getField(underlyingType.getField(name));
        } catch (NoSuchFieldException e) {
            return null;
        }
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
    public boolean isPrimitive()
    {
        return underlyingType.isPrimitive();
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
    }
    // http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#84645
    public static final RuntimeType INT = new PrimitiveType(int.class, "I", 1);
    public static final RuntimeType BYTE = new PrimitiveType(byte.class, "B", 1);
    public static final RuntimeType FLOAT = new PrimitiveType(float.class, "F", 1);
    public static final RuntimeType DOUBLE = new PrimitiveType(double.class, "D", 2);
    public static final RuntimeType VOID = new PrimitiveType(void.class, "V", 0);
    public static final RuntimeType BOOLEAN = new PrimitiveType(boolean.class, "Z", 1);
    public static void initPrimitives(HashMap<String, Type> types)
    {
        types.put(INT.id, INT);
        types.put(BYTE.id, BYTE);
        types.put(FLOAT.id, FLOAT);
        types.put(DOUBLE.id, DOUBLE);
        types.put(VOID.id, VOID);
        types.put(BOOLEAN.id, BOOLEAN);
    }

    public static void initJavaLang(HashMap<String, Type> types)
    {
        for (Class<?> type : javaLangClasses)
            types.put(type.getSimpleName(), cache.get(type));
    }
    private static final Class<?>[] javaLangClasses = { AbstractMethodError.class, ArithmeticException.class, ArrayIndexOutOfBoundsException.class, ArrayStoreException.class, AssertionError.class,
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
    private static final HashMap<Class<?>, RuntimeType> cache = new HashMap<Class<?>, RuntimeType>();
    static
    {
        for (Class<?> type : javaLangClasses)
            cache.put(type, new RuntimeType(type));
        cache.put(INT.underlyingType, INT);
        cache.put(BYTE.underlyingType, BYTE);
        cache.put(FLOAT.underlyingType, FLOAT);
        cache.put(DOUBLE.underlyingType, DOUBLE);
        cache.put(VOID.underlyingType, VOID);
        cache.put(BOOLEAN.underlyingType, BOOLEAN);
    }
    public static Type getType(Class<?> underlyingType)
    {
        RuntimeType type = cache.get(underlyingType);
        if (type == null) {
            type = new RuntimeType(underlyingType);
            cache.put(underlyingType, type);
        }
        return type;
    }
    private static Type[] getTypes(Class<?>[] underlyingTypes)
    {
        Type[] returnTypes = new Type[underlyingTypes.length];
        for (int i = 0; i < underlyingTypes.length; i++) 
            returnTypes[i] = getType(underlyingTypes[i]);
        return returnTypes;
    }

    private static final int[][] primitiveConversionTable = {
    // to: char byte short int long float double    | from:
        {     0,  -1,   -1,  1,   1,    1,     1}, // char   0
        {    -1,   0,    1,  1,   1,    1,     1}, // byte   1
        {    -1,  -1,    0,  1,   1,    1,     1}, // short  2
        {     1,  -1,   -1,  0,   1,    1,     1}, // int    3
        {     1,  -1,   -1, -1,   0,    1,     1}, // long   4
        {     1,  -1,   -1, -1,  -1,    0,     1}, // float  5
        {     1,  -1,   -1, -1,  -1,   -1,     0}, // double 6
    };
    public static int getPrimitiveConversionType(Type fromType, Type toType) {
        return primitiveConversionTable[getPrimitiveIndex(fromType)][getPrimitiveIndex(toType)];
    }
    private static int getPrimitiveIndex(Type type) {
//        if (type == CHAR)
//            return 0;
        if (type == BYTE)
            return 1;
//        if (type == SHORT)
//            return 2;
        if (type == INT)
            return 3;
//        if (type == LONG)
//            return 4;
//        if (type == FLOAT)
//            return 5;
//        if (type == DOUBLE)
//            return 6;
        throw new RuntimeException("hey, this isn't a primitive type: " + type);
    }
}
