package net.wolfesoftware.jax.ast;

public class Initializer extends ParseElement
{
    public Modifiers methodModifiers;
    public Block block;
    public Initializer(Modifiers methodModifiers, Block block)
    {
        this.methodModifiers = methodModifiers;
        this.block = block;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        if (!methodModifiers.elements.isEmpty()) {
            methodModifiers.decompile(indentation, out);
            out.append(' ');
        }
        block.decompile(indentation, out);
    }

    public static final int TYPE = 0x541d0831;
    public int getElementType()
    {
        return TYPE;
    }
}
