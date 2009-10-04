package net.wolfesoftware.java.jax.lexiconizer;

import net.wolfesoftware.java.jax.ast.Id;

public class LexicalException extends Exception
{
    public LexicalException(String message)
    {
        super(message);
    }

    public static LexicalException cantCast(Type from, Type to)
    {
        return new LexicalException("Can't cast type " + from + " to type " + to + ".");
    }

    public static LexicalException cantResolveLocalVariable(Id id)
    {
        return new LexicalException("Can't resolve local variable \"" + id.name + "\".");
    }
}
