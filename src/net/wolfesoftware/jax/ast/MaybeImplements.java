package net.wolfesoftware.jax.ast;

public class MaybeImplements extends SwitchElement
{
    public MaybeImplements(ParseElement content)
    {
        super(content);
    }

    public static final int TYPE = 0x2ecf060d;
    public int getElementType()
    {
        return TYPE;
    }
}
