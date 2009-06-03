package net.wolfesoftware.java.jax.lexiconizer;

import java.util.HashMap;
import net.wolfesoftware.java.jax.tokenizer.Lang;

public class Type
{
    public final String packageId;
    public final String id;
    public final int size;
    public Type(String packageId, String id, int size)
    {
        this.packageId = packageId;
        this.id = id;
        this.size = size;
    }

    public static final Type KEYWORD_INT = new Type(null, Lang.KEYWORD_INT, 1);
    public static final Type KEYWORD_VOID = new Type(null, Lang.KEYWORD_VOID, 0);
    public static final Type KEYWORD_BOOLEAN = new Type(null, Lang.KEYWORD_BOOLEAN, 1);

    public static void initPrimitives(HashMap<String, Type> types)
    {
        types.put(KEYWORD_INT.id, KEYWORD_INT);
        types.put(KEYWORD_VOID.id, KEYWORD_VOID);
        types.put(KEYWORD_BOOLEAN.id, KEYWORD_BOOLEAN);
    }

    public String toString()
    {
        return (packageId == null) ? id : packageId + "." + id;
    }
}
