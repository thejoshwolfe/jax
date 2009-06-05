package net.wolfesoftware.java.jax.lexiconizer;

import java.util.HashMap;
import net.wolfesoftware.java.jax.tokenizer.Lang;

public class Type
{
    public final String packageId;
    public final String id;
    public final int size;
    
    public Type(String packageId, String id)
    {
        this(packageId, id, 1);
    }
    public Type(String packageId, String id, int size)
    {
        this.packageId = packageId;
        this.id = id;
        this.size = size;
    }

    public static final Type KEYWORD_INT = new Type(null, Lang.KEYWORD_INT);
    public static final Type KEYWORD_VOID = new Type(null, Lang.KEYWORD_VOID, 0);
    public static final Type KEYWORD_BOOLEAN = new Type(null, Lang.KEYWORD_BOOLEAN);

    private static final String[] javaLangTypeNames = { "AbstractMethodError", "ArithmeticException", "ArrayIndexOutOfBoundsException", "ArrayStoreException", "AssertionError", "Boolean", "Byte",
            "Character", "CharSequence", "Class", "ClassCastException", "ClassCircularityError", "ClassFormatError", "ClassLoader", "ClassNotFoundException", "Cloneable",
            "CloneNotSupportedException", "Comparable", "Compiler", "Double", "Error", "Exception", "ExceptionInInitializerError", "Float", "IllegalAccessError", "IllegalAccessException",
            "IllegalArgumentException", "IllegalMonitorStateException", "IllegalStateException", "IllegalThreadStateException", "IncompatibleClassChangeError", "IndexOutOfBoundsException",
            "InheritableThreadLocal", "InstantiationError", "InstantiationException", "Integer", "InternalError", "InterruptedException", "LinkageError", "Long", "Math", "NegativeArraySizeException",
            "NoClassDefFoundError", "NoSuchFieldError", "NoSuchFieldException", "NoSuchMethodError", "NoSuchMethodException", "NullPointerException", "Number", "NumberFormatException", "Object",
            "OutOfMemoryError", "Package", "Process", "Runnable", "Runtime", "RuntimeException", "RuntimePermission", "SecurityException", "SecurityManager", "Short", "StackOverflowError",
            "StackTraceElement", "StrictMath", "String", "StringBuffer", "StringIndexOutOfBoundsException", "System", "Thread", "ThreadDeath", "ThreadGroup", "ThreadLocal", "Throwable",
            "UnknownError", "UnsatisfiedLinkError", "UnsupportedClassVersionError", "UnsupportedOperationException", "VerifyError", "VirtualMachineError", "Void", };

    public static void initPrimitives(HashMap<String, Type> types)
    {
        types.put(KEYWORD_INT.id, KEYWORD_INT);
        types.put(KEYWORD_VOID.id, KEYWORD_VOID);
        types.put(KEYWORD_BOOLEAN.id, KEYWORD_BOOLEAN);
    }
    public static void initJavaLang(HashMap<String, Type> types)
    {
        for (String typeName : javaLangTypeNames)
            types.put(typeName, new Type("java.lang", typeName));
    }

    public String toString()
    {
        return (packageId == null) ? id : packageId + "." + id;
    }
}
