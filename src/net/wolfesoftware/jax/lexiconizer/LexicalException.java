package net.wolfesoftware.jax.lexiconizer;

import java.util.*;
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
    public static LexicalException mustBeInt(Expression expression)
    {
        return new LexicalException(expression, "expression must evaluate to an int.");
    }

    public static LexicalException mustBeVariable(ParseElement parseElement)
    {
        return new LexicalException(parseElement, "this has to be a variable.");
    }

    public static LexicalException variableMustBeInt(Id id)
    {
        return new LexicalException(id, "variable needs to be of type int.");
    }

    public static LexicalException cantResolveType(TypeId typeId)
    {
        return new LexicalException(typeId, "Dunno what this type is.");
    }

    public static LexicalException cantResolveField(Type type, Id id)
    {
        return new LexicalException(id, "The type " + type + " doesn't have a field called " + id);
    }

    public static LexicalException cantResolveMethod(Type type, Id id, ReturnBehavior[] argumentSignature)
    {
        return new LexicalException(id, "Can't resolve the method \"" + id + "\" in the type \"" + type + "\" with arguments " + Arrays.toString(argumentSignature) + ".");
    }

    public static boolean mustBeNumeric(Expression expression, ArrayList<LexicalException> errors)
    {
        Type type = expression.returnBehavior.type;
        if (type == UnknownType.INSTANCE)
            return false;
        if (!(type.isPrimitive() && type != RuntimeType.VOID)) {
            errors.add(new LexicalException(expression, "This thing is type \"" + type + "\" and it needs to be numeric."));
            return false;
        }
        return true;
    }

    public static LexicalException cantConvert(Expression element, Type fromType, Type toType)
    {
        return new LexicalException(element, "Can't convert expression of type " + fromType + " to type " + toType + ".");
    }
}
