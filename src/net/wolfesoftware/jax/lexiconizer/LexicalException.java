package net.wolfesoftware.jax.lexiconizer;

import net.wolfesoftware.jax.CompileError;
import net.wolfesoftware.jax.ast.*;

public class LexicalException extends CompileError
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
