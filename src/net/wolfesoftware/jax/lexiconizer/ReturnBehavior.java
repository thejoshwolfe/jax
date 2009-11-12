package net.wolfesoftware.jax.lexiconizer;

public class ReturnBehavior
{
    public static final ReturnBehavior VOID = new ReturnBehavior(RuntimeType.VOID);
    public static final ReturnBehavior INT = new ReturnBehavior(RuntimeType.INT);
    public static final ReturnBehavior LONG = new ReturnBehavior(RuntimeType.LONG);
    public static final ReturnBehavior FLOAT = new ReturnBehavior(RuntimeType.FLOAT);
    public static final ReturnBehavior DOUBLE = new ReturnBehavior(RuntimeType.DOUBLE);
    public static final ReturnBehavior BOOLEAN = new ReturnBehavior(RuntimeType.BOOLEAN);
    public static final ReturnBehavior STRING = new ReturnBehavior(RuntimeType.STRING);
    public static final ReturnBehavior NULL = new ReturnBehavior(NullType.INSTANCE);
    public static final ReturnBehavior UNKNOWN = new ReturnBehavior(UnknownType.INSTANCE);

    public Type type;

    public ReturnBehavior(Type type)
    {
        this.type = type;
    }

    public String toString()
    {
        return type.toString();
    }
}
