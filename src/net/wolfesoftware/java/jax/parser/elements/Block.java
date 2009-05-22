package net.wolfesoftware.java.jax.parser.elements;

public class Block extends ParseElement
{
    public BlockContents blockContents;
    public Block(BlockContents blockContents)
    {
        this.blockContents = blockContents;
    }

    public static final int TYPE = 0x057d01ec;
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected void decompile(String indentation, StringBuffer out)
    {
        out.append("{");
        blockContents.decompile(increaseIndentation(indentation), out);
        out.append("\n").append(indentation);
        out.append("}");
    }
}
