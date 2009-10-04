package net.wolfesoftware.java.jax.lexiconizer;

import net.wolfesoftware.java.jax.ast.*;

public class LexicalException extends Exception
{
    public LexicalException(ParseElement element, String message)
    {
        super(message + " \"" + element.decompile() + "\"");
    }

    public static LexicalException cantCast(ParseElement element, Type from, Type to)
    {
        return new LexicalException(element, "Can't cast type " + from + " to type " + to + ".");
    }

    public static LexicalException cantResolveLocalVariable(Id id)
    {
        return new LexicalException(id, "Can't resolve local variable.");
    }
}
