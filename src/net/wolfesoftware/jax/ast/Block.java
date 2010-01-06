package net.wolfesoftware.jax.ast;

import java.util.LinkedList;
import net.wolfesoftware.jax.semalysizer.LocalContext;

public class Block extends ParseElement
{
    public LocalContext context;

    public BlockContents blockContents;
    public Block(BlockContents blockContents)
    {
        this.blockContents = blockContents;
    }

    public static final int TYPE = 0x057d01ec;

    public static final Block EMPTY = new Block(new BlockContents(new LinkedList<Expression>()));
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("{");
        blockContents.decompile(increaseIndentation(indentation), out);
        out.append("\n").append(indentation);
        out.append("}");
    }
}
