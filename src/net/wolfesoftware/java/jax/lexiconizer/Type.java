package net.wolfesoftware.java.jax.lexiconizer;

import java.util.HashMap;
import net.wolfesoftware.java.jax.tokenizer.Lang;

public abstract class Type
{
    public final String packageId;
    public final String id;

    public Type(String packageId, String id)
    {
        this.packageId = packageId;
        this.id = id;
    }

    public abstract Method resolveMethod(String name, Type[] argumentSignature);

    public String toString()
    {
        return (packageId == null) ? id : packageId + "." + id;
    }





    public static final Type KEYWORD_INT = new PrimitiveType(Lang.KEYWORD_INT);
    public static final Type KEYWORD_VOID = new PrimitiveType(Lang.KEYWORD_VOID);
    public static final Type KEYWORD_BOOLEAN = new PrimitiveType(Lang.KEYWORD_BOOLEAN);

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
    public static void initPrimitives(HashMap<String, Type> types)
    {
        types.put(KEYWORD_INT.id, KEYWORD_INT);
        types.put(KEYWORD_VOID.id, KEYWORD_VOID);
        types.put(KEYWORD_BOOLEAN.id, KEYWORD_BOOLEAN);
    }
    public static void initJavaLang(HashMap<String, Type> types)
    {
        for (Class<?> type : javaLangClasses)
            types.put(type.getSimpleName(), new RuntimeType("java.lang", type.getSimpleName(), type));
    }
}
