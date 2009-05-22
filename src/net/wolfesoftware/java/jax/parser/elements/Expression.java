package net.wolfesoftware.java.jax.parser.elements;

import net.wolfesoftware.java.jax.lexiconizer.Type;

public class Expression extends SwitchElement
{
    public Type type = null;

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
