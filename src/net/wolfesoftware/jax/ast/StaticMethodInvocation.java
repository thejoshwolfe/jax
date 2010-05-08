package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Method;
import net.wolfesoftware.jax.util.Util;

public class StaticMethodInvocation extends AbstractMethodInvocation
{
    public TypeId typeId;
    public StaticMethodInvocation(TypeId typeId, AmbiguousId methodName, Arguments arguments)
    {
        super(methodName, arguments);
        this.typeId = typeId;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        typeId.decompile(indentation, out);
        out.append('.');
        methodName.decompile(indentation, out);
        out.append('(');
        arguments.decompile(indentation, out);
        out.append(')');
    }

    public static final int TYPE = 0x78ab09c9;
    public int getElementType()
    {
        return TYPE;
    }

    public static ParseElement fromMethod(Method method, Expression ... args)
    {
        StaticMethodInvocation tmp = new StaticMethodInvocation(TypeId.fromType(method.declaringType), new AmbiguousId(method.name), new Arguments(Util.arrayToList(args)));
        tmp.method = method;
        return tmp;
    }
}
