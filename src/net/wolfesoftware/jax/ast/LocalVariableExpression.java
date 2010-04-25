package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.LocalVariable;

public class LocalVariableExpression extends ParseElement
{
    public LocalVariable variable;
    public LocalVariableExpression(LocalVariable variable)
    {
        this.variable = variable;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(variable.name);
    }

    public static final int TYPE = 0x6c170942;
    public int getElementType()
    {
        return TYPE;
    }
}
