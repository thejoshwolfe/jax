package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Constructor;
import net.wolfesoftware.jax.util.Util;

public class ConstructorInvocation extends ParseElement
{
    public Constructor constructor;

    public AmbiguousId typeName;
    public Arguments arguments;
    public ConstructorInvocation(AmbiguousId typeName, Arguments arguments)
    {
        this.typeName = typeName;
        this.arguments = arguments;
    }

    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("new ");
        typeName.decompile(indentation, out);
        out.append('(');
        arguments.decompile(indentation, out);
        out.append(')');
    }

    public static final int TYPE = 0x1b1104a7;
    public int getElementType()
    {
        return TYPE;
    }

    public static ConstructorInvocation fromConstructor(Constructor constructor, Expression... args)
    {
        ConstructorInvocation tmp = new ConstructorInvocation(new AmbiguousId(constructor.declaringType.simpleName), new Arguments(Util.arrayToList(args)));
        tmp.constructor = constructor;
        return tmp;
    }
}
