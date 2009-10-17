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

    public static LexicalException cantResolveImport(QualifiedName qualifiedName)
    {
        return new LexicalException(qualifiedName, "Can't resolve import.");
    }

    public static LexicalException mustBeVoid(Expression expression)
    {
        return new LexicalException(expression, "expression type must be void.");
    }

    public static LexicalException mustBeBoolean(Expression expression)
    {
        return new LexicalException(expression, "expression must evaluate to a boolean.");
    }

    public static LexicalException mustBeVariable(ParseElement parseElement)
    {
        return new LexicalException(parseElement, "this has to be a variable.");
    }

    public static LexicalException variableMustBeInt(Id id)
    {
        return new LexicalException(id, "variable needs to be of type int");
    }

    public static LexicalException cantResolveType(TypeId typeId)
    {
        return new LexicalException(typeId, "Dunno what this type is.");
    }
}
