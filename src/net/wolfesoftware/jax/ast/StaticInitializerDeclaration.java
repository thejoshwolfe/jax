package net.wolfesoftware.jax.ast;

import java.util.ArrayList;
import net.wolfesoftware.jax.semalysis.*;
import net.wolfesoftware.jax.util.Util;

public class StaticInitializerDeclaration extends ConstructorOrMethodDeclaration
{
    private static final MethodModifiers METHOD_MODIFIERS;
    static {
        METHOD_MODIFIERS = new MethodModifiers(Util.arrayToList(MethodModifier.STATIC));
        METHOD_MODIFIERS.bitmask = MethodModifier.STATIC.bitmask;
    }

    public StaticInitializer method;

    public StaticInitializerDeclaration(RootLocalContext rootLocalContext)
    {
        super(METHOD_MODIFIERS, TypeId.fromName("<clinit>"), ArgumentDeclarations.EMPTY, MaybeThrows.DOESNT, new Expression(new Block(new BlockContents(new ArrayList<Expression>()))));
        context = rootLocalContext;
        returnType = RuntimeType.VOID;
        method = new StaticInitializer(context.getClassContext());
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        methodModifiers.decompile(indentation, out);
        if (!methodModifiers.elements.isEmpty())
            out.append(' ');
        typeId.decompile(indentation, out);
        out.append('(');
        argumentDeclarations.decompile(indentation, out);
        out.append(") ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x171d010e;
    public int getElementType()
    {
        return TYPE;
    }
}
