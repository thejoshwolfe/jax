package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.Type;

public class Expression extends SwitchElement
{
    public Type returnType;

    public Expression(ParseElement content)
    {
        super(content);
    }

    public static final int TYPE = 0x16820431;
    public int getElementType()
    {
        return TYPE;
    }
}
