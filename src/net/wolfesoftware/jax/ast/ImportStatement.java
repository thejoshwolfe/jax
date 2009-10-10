package net.wolfesoftware.jax.ast;

public class ImportStatement extends SwitchElement
{
    public ImportStatement(ParseElement content)
    {
        super(content);
    }

    public static final int TYPE = 0x30b40631;
    public int getElementType()
    {
        return TYPE;
    }
}
