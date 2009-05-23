package net.wolfesoftware.java.jax.ast;

import net.wolfesoftware.java.jax.lexiconizer.ReturnBehavior;

public class Expression extends SwitchElement
{
    public ReturnBehavior returnBehavior;

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