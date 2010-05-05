package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.LocalVariable;

public class LocalVariablePostIncrementDecrement extends LocalVariableIncrementDecrement
{
    public LocalVariablePostIncrementDecrement(LocalVariable variable, String operator)
    {
        super(variable, operator);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(variable.name).append(operator);
    }

    public static final int TYPE = 0xf77e0df4;
    public int getElementType()
    {
        return TYPE;
    }
}
