package net.wolfesoftware.java.jax.ast;

public class TopLevelItem extends SwitchElement
{
    public TopLevelItem(ParseElement content)
    {
        super(content);
    }

    public static final int TYPE = 0x1e6004bb;
    public int getElementType()
    {
        return TYPE;
    }
}
