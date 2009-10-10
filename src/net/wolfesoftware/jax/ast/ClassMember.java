package net.wolfesoftware.jax.ast;

public class ClassMember extends SwitchElement
{
    public ClassMember(ParseElement content)
    {
        super(content);
    }

    public static final int TYPE = 0x1927044f;
    public int getElementType()
    {
        return TYPE;
    }
}
