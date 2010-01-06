package net.wolfesoftware.jax.semalysizer;

import java.util.*;
import net.wolfesoftware.jax.CompileError;
import net.wolfesoftware.jax.ast.*;

public class SemalyticalException extends CompileError
{
    public SemalyticalException(ParseElement element, String message)
    {
        super(message + " \"" + element.decompile() + "\"");
    }

    public static SemalyticalException cantCast(ParseElement element, Type from, Type to)
    {
        return new SemalyticalException(element, "Can't cast type " + from + " to type " + to + ".");
    }

    public static SemalyticalException cantResolveLocalVariable(Id id)
    {
        return new SemalyticalException(id, "Can't resolve local variable.");
    }

    public static SemalyticalException cantResolveImport(QualifiedName qualifiedName)
    {
        return new SemalyticalException(qualifiedName, "Can't resolve import.");
    }

    public static SemalyticalException mustBeVoid(Expression expression)
    {
        return new SemalyticalException(expression, "expression type must be void.");
    }

    public static SemalyticalException mustBeBoolean(Expression expression)
    {
        return new SemalyticalException(expression, "expression must evaluate to a boolean.");
    }
    public static SemalyticalException mustBeInt(Expression expression)
    {
        return new SemalyticalException(expression, "expression must evaluate to an int.");
    }

    public static SemalyticalException mustBeVariable(ParseElement parseElement)
    {
        return new SemalyticalException(parseElement, "this has to be a variable.");
    }

    public static SemalyticalException variableMustBeInt(Id id)
    {
        return new SemalyticalException(id, "variable needs to be of type int.");
    }

    public static SemalyticalException cantResolveType(TypeId typeId)
    {
        return new SemalyticalException(typeId, "Dunno what this type is.");
    }

    public static SemalyticalException cantResolveField(Type type, Id id)
    {
        return new SemalyticalException(id, "The type " + type + " doesn't have a field called " + id);
    }

    public static SemalyticalException cantResolveMethod(Type type, Id id, ReturnBehavior[] argumentSignature)
    {
        return new SemalyticalException(id, "Can't resolve the method \"" + id + "\" in the type \"" + type + "\" with arguments " + Arrays.toString(argumentSignature) + ".");
    }

    public static boolean mustBeNumeric(Expression expression, ArrayList<SemalyticalException> errors)
    {
        Type type = expression.returnBehavior.type;
        if (type == UnknownType.INSTANCE)
            return false;
        if (!(type.isPrimitive() && type != RuntimeType.VOID && type != RuntimeType.BOOLEAN)) {
            errors.add(new SemalyticalException(expression, "This thing is type \"" + type + "\" and it needs to be numeric."));
            return false;
        }
        return true;
    }

    public static SemalyticalException cantConvert(Expression element, Type fromType, Type toType)
    {
        return new SemalyticalException(element, "Can't convert expression of type " + fromType + " to type " + toType + ".");
    }
}
