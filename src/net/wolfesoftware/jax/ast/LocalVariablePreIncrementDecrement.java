package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.LocalVariable;

public class LocalVariablePreIncrementDecrement extends LocalVariableIncrementDecrement
{
    public LocalVariablePreIncrementDecrement(LocalVariable variable, String operator)
    {
        super(variable, operator);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(operator).append(variable.name);
    }

    public static final int TYPE = 0xe7d00d75;
    public int getElementType()
    {
        return TYPE;
    }
}
