package net.wolfesoftware.jax.semalysis;

import java.util.Arrays;
import net.wolfesoftware.jax.CompileError;
import net.wolfesoftware.jax.ast.*;

public class SemalyticalError extends CompileError
{
    public SemalyticalError(String stringElement, String message)
    {
        this(new AmbiguousId(stringElement), message);
    }
    public SemalyticalError(ParseElement element, String message)
    {
        super(message + " \"" + element.decompile() + "\"");
    }

    public static SemalyticalError cantCast(ParseElement element, Type from, Type to)
    {
        return new SemalyticalError(element, "Can't cast type " + from + " to type " + to + ".");
    }

    public static SemalyticalError cantResolveLocalVariable(AmbiguousId id)
    {
        return new SemalyticalError(id, "Can't resolve local variable.");
    }

    public static SemalyticalError cantResolveImport(QualifiedName qualifiedName)
    {
        return new SemalyticalError(qualifiedName, "Can't resolve import.");
    }

    public static SemalyticalError mustBeVoid(Expression expression)
    {
        return new SemalyticalError(expression, "expression type must be void.");
    }

    public static SemalyticalError mustBeBoolean(Expression expression)
    {
        return new SemalyticalError(expression, "expression must evaluate to a boolean.");
    }
    public static SemalyticalError mustBeInt(Expression expression)
    {
        return new SemalyticalError(expression, "expression must evaluate to an int.");
    }

    public static SemalyticalError mustBeVariable(ParseElement parseElement)
    {
        return new SemalyticalError(parseElement, "this has to be a variable.");
    }

    public static SemalyticalError variableMustBeInt(AmbiguousId id)
    {
        return new SemalyticalError(id, "variable needs to be of type int.");
    }

    public static SemalyticalError cantResolveType(TypeId typeId)
    {
        return new SemalyticalError(typeId, "Dunno what this type is.");
    }

    public static SemalyticalError cantResolveField(Type type, AmbiguousId id)
    {
        return new SemalyticalError(id, "The type " + type + " doesn't have a field called " + id);
    }

    public static SemalyticalError cantResolveMethod(Type type, AmbiguousId id, Type[] argumentSignature)
    {
        return new SemalyticalError(id, "Can't resolve the method \"" + id + "\" in the type \"" + type + "\" with arguments " + Arrays.toString(argumentSignature) + ".");
    }

    public static SemalyticalError mustBeNumeric(ParseElement element, Type type)
    {
        return new SemalyticalError(element, "This thing is type \"" + type + "\" and it needs to be numeric.");
    }

    public static SemalyticalError cantConvert(Expression element, Type fromType, Type toType)
    {
        return new SemalyticalError(element, "Can't convert expression of type " + fromType + " to type " + toType + ".");
    }
    public static SemalyticalError cantCastBetweenPrimitivesAndNonPrimitives(ParseElement element)
    {
        return new SemalyticalError(element, "Can't cast between primitives and non-primitives");
    }
    public static SemalyticalError unreachableCode(Expression element)
    {
        return new SemalyticalError(element, "Unreachable code.");
    }
}
