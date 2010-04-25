package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Method;

public abstract class AbstractMethodInvocation extends ParseElement
{
    public Method method;

    public AmbiguousId methodName;
    public Arguments arguments;
    protected AbstractMethodInvocation(AmbiguousId methodName, Arguments arguments)
    {
        this.methodName = methodName;
        this.arguments = arguments;
    }
}
