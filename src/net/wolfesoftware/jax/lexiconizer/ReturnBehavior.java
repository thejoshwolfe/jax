package net.wolfesoftware.jax.lexiconizer;

public class ReturnBehavior
{
    public static final ReturnBehavior VOID = new ReturnBehavior(RuntimeType.VOID);
    public static final ReturnBehavior INT = new ReturnBehavior(RuntimeType.INT);
    public static final ReturnBehavior FLOAT = new ReturnBehavior(RuntimeType.FLOAT);
    public static final ReturnBehavior DOUBLE = new ReturnBehavior(RuntimeType.DOUBLE);
    public static final ReturnBehavior BOOLEAN = new ReturnBehavior(RuntimeType.BOOLEAN);
    public static final ReturnBehavior STRING = new ReturnBehavior(RuntimeType.getType(String.class));


    public Type type;

    public ReturnBehavior(Type type)
    {
        this.type = type;
    }
}
