package net.wolfesoftware.java.jax.lexiconizer;

import java.util.HashMap;
import net.wolfesoftware.java.jax.ast.Lang;

public class Type
{
    public final String packageId;
    public final String id;
    public Type(String packageId, String id)
    {
        this.packageId = packageId;
        this.id = id;
    }

    public static final Type KEYWORD_INT = new Type(null, Lang.KEYWORD_INT);
    public static final Type KEYWORD_VOID = new Type(null, Lang.KEYWORD_VOID);

    public static void initPrimitives(HashMap<String, Type> types)
    {
        types.put(KEYWORD_INT.id, KEYWORD_INT);
        types.put(KEYWORD_VOID.id, KEYWORD_VOID);
    }

    public String toString()
    {
        return (packageId == null) ? id : packageId + "." + id;
    }
}
