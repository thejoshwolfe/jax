package net.wolfesoftware.jax.ast;

public class MaybeThrows extends SwitchElement
{
    public MaybeThrows(ParseElement content)
    {
        super(content);
    }

    public static final int TYPE = 0x19ae0476;
    public int getElementType()
    {
        return TYPE;
    }

    public static final MaybeThrows DOESNT = new MaybeThrows(EmptyElement.INSTANCE);
}
